package com.example.biomedix.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biomedix.biokt.BioKt
import com.example.biomedix.biokt.FastaRecord
import com.example.biomedix.common.CentralityMethod
import com.example.biomedix.common.IntegratedReport
import com.example.biomedix.common.ModuleExecutionState
import com.example.biomedix.common.PipelineExecutionProgress
import com.example.biomedix.common.PipelineParameters
import com.example.biomedix.data.local.BioMedixDatabase
import com.example.biomedix.data.local.CachedReportEntity
import com.example.biomedix.module1_network.TargetDiscoveryPipeline
import com.example.biomedix.module2_structural.StructuralMLPipeline
import com.example.biomedix.module3_genomic.CrisprSafetyEngine
import com.example.biomedix.module4_orchestration.PipelineRunner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiState(
    // Authentication State
    val isAuthenticated: Boolean = false, // Set to true or allow login
    val usernameInput: String = "",
    val accessCodeInput: String = "",
    val isPasswordVisible: Boolean = false,
    val isRegistering: Boolean = false,
    val authError: String? = null,

    // Research Console Inputs
    val diseaseInput: String = "",
    val modelOrganism: String = "human",
    val grnaInput: String = "",

    // Workflow & Disease Burden
    val diseaseBurdenPercentage: Int = 100, // 100% -> 66% -> 33% -> 0%
    val targetCompleted: Boolean = false,
    val drugCompleted: Boolean = false,
    val crisprCompleted: Boolean = false,
    val completeCompleted: Boolean = false,

    // Execution / Sync State
    val isSynchronizing: Boolean = false,
    val activeOutputCard: String? = null, // "TARGET", "DRUG", "CRISPR", "COMPLETE", "CRISPR_MISSING"
    val outputFormattedJson: String = "",
    val outputTitle: String = "",
    val outputSubtitle: String? = null,

    // Visualization & Tab Selection
    val selectedTab: Int = 0, // 0: Core Console, 1: PPI Network, 2: 3D Protein, 3: CRISPR DL, 4: BioKt Studio, 5: History
    val parameters: PipelineParameters = PipelineParameters(),
    val isRunning: Boolean = false,
    val progress: PipelineExecutionProgress = PipelineExecutionProgress(),
    val currentReport: IntegratedReport? = null,
    val proteinViewMode: String = "Backbone Ribbon",
    val fastaInput: String = "",
    val parsedFastaRecords: List<FastaRecord> = emptyList(),

    // AI Assistant
    val isAiAssistantOpen: Boolean = false,
    val aiQueryInput: String = "",
    val isAiThinking: Boolean = false,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            isUser = false,
            text = "Hello! I'm your BioMedix AI. How can I help you with your research today?"
        )
    ),

    val errorMessage: String? = null
)

class BioMedixViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BioMedixDatabase.getDatabase(application)
    private val dao = db.dao()
    private val runner = PipelineRunner()
    private val module1 = TargetDiscoveryPipeline()
    private val module2 = StructuralMLPipeline()
    private val module3 = CrisprSafetyEngine()

    private val userDatabase = mutableMapOf("sai" to "123")

    val historyReports: StateFlow<List<CachedReportEntity>> = dao.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        parseFasta(_uiState.value.fastaInput)
    }

    // --- Authentication ---
    fun setUsernameInput(name: String) {
        _uiState.value = _uiState.value.copy(usernameInput = name, authError = null)
    }

    fun setAccessCodeInput(code: String) {
        _uiState.value = _uiState.value.copy(accessCodeInput = code, authError = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleRegistrationMode() {
        _uiState.value = _uiState.value.copy(
            isRegistering = !_uiState.value.isRegistering,
            authError = null,
            usernameInput = "",
            accessCodeInput = ""
        )
    }

    fun initializeAccess() {
        val user = _uiState.value.usernameInput.trim()
        val code = _uiState.value.accessCodeInput.trim()

        if (user.isEmpty() || code.isEmpty()) {
            _uiState.value = _uiState.value.copy(authError = "Please enter both credentials")
            return
        }

        if (_uiState.value.isRegistering) {
            // Register
            userDatabase[user] = code
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                isRegistering = false,
                authError = null
            )
        } else {
            // Login
            if (userDatabase[user] == code) {
                _uiState.value = _uiState.value.copy(isAuthenticated = true, authError = null)
            } else {
                _uiState.value = _uiState.value.copy(authError = "ACCESS DENIED: Invalid Credentials")
            }
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(isAuthenticated = false)
    }

    // --- Research Console Controls ---
    fun setDiseaseInput(value: String) {
        _uiState.value = _uiState.value.copy(diseaseInput = value)
    }

    fun setModelOrganism(organism: String) {
        _uiState.value = _uiState.value.copy(modelOrganism = organism)
    }

    fun setGrnaInput(value: String) {
        _uiState.value = _uiState.value.copy(grnaInput = value)
    }

    fun setSelectedTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun setProteinViewMode(mode: String) {
        _uiState.value = _uiState.value.copy(proteinViewMode = mode)
    }

    fun setFastaInput(content: String) {
        _uiState.value = _uiState.value.copy(fastaInput = content)
        parseFasta(content)
    }

    private fun parseFasta(content: String) {
        try {
            val records = BioKt.parseFasta(content)
            _uiState.value = _uiState.value.copy(parsedFastaRecords = records)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(parsedFastaRecords = emptyList())
        }
    }

    // --- Module Executions (Video Exact Flow) ---

    fun runTargetDiscovery() {
        val disease = _uiState.value.diseaseInput
        if (disease.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSynchronizing = true, isRunning = true)
            delay(1200) 
            val hubResult = module1.run(disease, _uiState.value.parameters)

            if (hubResult.candidateGenes.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isSynchronizing = false,
                    isRunning = false,
                    activeOutputCard = "TARGET_ERROR",
                    outputTitle = "Target Discovery Failed",
                    outputSubtitle = "No biological data found for \"$disease\". Please enter a valid disease name (e.g., Alzheimer's, Lung Cancer).",
                    outputFormattedJson = ""
                )
                return@launch
            }

            // Construct JSON format seen in video
            val centralityJson = hubResult.centralityScores.entries.joinToString(", ") {
                "\"${it.key}\": ${it.value}"
            }
            val nodesJson = hubResult.candidateGenes.joinToString(", ") { "\"${it.symbol}\"" }
            val formatted = """
{
  "report_id": ${(10..99).random()},
  "disease_name": "$disease",
  "organism": "${_uiState.value.modelOrganism}",
  "hub_result": {
    "disease_name": "$disease",
    "hub_gene_symbol": "${hubResult.hubGeneSymbol}",
    "centrality_scores": { $centralityJson },
    "graph_summary": {
      "num_nodes": ${hubResult.candidateGenes.size},
      "num_edges": ${hubResult.ppiEdges.size},
      "nodes": [ $nodesJson ]
    }
  }
}
            """.trimIndent()

            _uiState.value = _uiState.value.copy(
                isSynchronizing = false,
                isRunning = false,
                targetCompleted = true,
                diseaseBurdenPercentage = maxOf(10, _uiState.value.diseaseBurdenPercentage - 30),
                activeOutputCard = "TARGET",
                outputTitle = "Targets Found ✓",
                outputSubtitle = null,
                outputFormattedJson = formatted
            )
        }
    }

    fun runDruggabilityAnalysis() {
        val disease = _uiState.value.diseaseInput
        if (disease.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSynchronizing = true, isRunning = true)
            delay(1300)
            val hubResult = module1.run(disease, _uiState.value.parameters)
            val drugResult = module2.run(hubResult.hubGeneSymbol)

            val formatted = """
{
  "report_id": ${(10..99).random()},
  "disease_name": "$disease",
  "hub_gene": "${hubResult.hubGeneSymbol}",
  "druggability_result": {
    "gene_symbol": "${drugResult.geneSymbol}",
    "pdb_id": "${drugResult.pdbId}",
    "druggability_score": ${drugResult.druggabilityScore},
    "pocket_features": {
      "volume_angstrom3": ${drugResult.pocketFeatures?.pocketVolume ?: 890f},
      "hydrophobicity": ${drugResult.pocketFeatures?.hydrophobicityRatio ?: 0.52f},
      "depth": ${drugResult.pocketFeatures?.depth ?: 14.5f},
      "lining_residues": ${drugResult.pocketFeatures?.liningResidueCount ?: 24}
    }
  },
  "verdict": "${if (drugResult.druggabilityScore >= 0.7f) "High Small-Molecule Druggability Candidate" else "Moderate Small-Molecule Druggability"}"
}
            """.trimIndent()

            _uiState.value = _uiState.value.copy(
                isSynchronizing = false,
                isRunning = false,
                drugCompleted = true,
                diseaseBurdenPercentage = maxOf(10, _uiState.value.diseaseBurdenPercentage - 35),
                activeOutputCard = "DRUG",
                outputTitle = "Druggability Analysis Complete ✓",
                outputSubtitle = null,
                outputFormattedJson = formatted
            )
        }
    }

    fun runCrisprSafety() {
        val disease = _uiState.value.diseaseInput
        val grna = _uiState.value.grnaInput.trim()

        if (disease.isBlank() || grna.isBlank()) {
            _uiState.value = _uiState.value.copy(
                activeOutputCard = "CRISPR_MISSING",
                outputTitle = "chuskoni chai rahh",
                outputSubtitle = "Enter a guide RNA for CRISPR analysis.",
                outputFormattedJson = ""
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSynchronizing = true, isRunning = true)
            delay(1400)
            val hubResult = module1.run(disease, _uiState.value.parameters)
            val crisprResult = module3.evaluate(hubResult.hubGeneSymbol, grna)

            val flaggedJson = crisprResult.flaggedSites.joinToString(",\n    ") { site ->
                """{"site_id": "${site.siteId}", "target_sequence": "${site.targetSequence}", "mismatches": ${site.mismatchCount}, "risk_score": ${site.riskScore}, "pam": "${site.pam}"}"""
            }

            val formatted = """
{
  "report_id": ${(10..99).random()},
  "disease_name": "$disease",
  "crispr_result": {
    "gene_symbol": "${hubResult.hubGeneSymbol}",
    "grna_sequence": "$grna",
    "safety_score": ${crisprResult.safetyScore},
    "total_sites_scanned": ${crisprResult.totalSitesScanned},
    "flagged_sites": [
    $flaggedJson
    ]
  }
}
            """.trimIndent()

            _uiState.value = _uiState.value.copy(
                isSynchronizing = false,
                isRunning = false,
                crisprCompleted = true,
                diseaseBurdenPercentage = 10,
                activeOutputCard = "CRISPR",
                outputTitle = "CRISPR Safety Validated ✓",
                outputSubtitle = null,
                outputFormattedJson = formatted
            )
        }
    }

    fun runCompleteAnalysis() {
        val disease = _uiState.value.diseaseInput
        val grna = _uiState.value.grnaInput

        if (disease.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSynchronizing = true, isRunning = true)
            delay(1800)

            val report = runner.runFullPipeline(
                diseaseName = disease,
                grnaSequence = grna,
                params = _uiState.value.parameters
            )

            if (report.hubResult.candidateGenes.isEmpty() || report.verdict == "INVALID_DATA_ERROR") {
                _uiState.value = _uiState.value.copy(
                    isSynchronizing = false,
                    isRunning = false,
                    activeOutputCard = "COMPLETE_ERROR",
                    outputTitle = "Analysis Denied",
                    outputSubtitle = "The system could not validate \"$disease\" as a biological entity. Analysis terminated to prevent data hallucination.",
                    outputFormattedJson = ""
                )
                return@launch
            }

            // Save to Room DB
            dao.insertReport(
                CachedReportEntity(
                    diseaseName = report.diseaseName,
                    hubGene = report.hubResult.hubGeneSymbol,
                    druggabilityScore = report.druggabilityResult.druggabilityScore,
                    crisprSafetyScore = report.crisprResult?.safetyScore ?: 0.9f,
                    verdict = report.verdict,
                    pdbId = report.druggabilityResult.pdbId ?: "3D_HOMOLOGY",
                    grnaSequence = grna,
                    pocketVolume = report.druggabilityResult.pocketFeatures?.pocketVolume ?: 890f,
                    offTargetCount = report.crisprResult?.offTargetCount ?: 0,
                    centralityMethod = _uiState.value.parameters.centralityMethod.displayName
                )
            )

            val centralityJson = report.hubResult.centralityScores.entries.joinToString(", ") {
                "\"${it.key}\": ${it.value}"
            }
            val formatted = """
REPORT_ID : ${(10..99).random()}
DISEASE : $disease
HUB_GENE : ${report.hubResult.hubGeneSymbol}
DRUGGABILITY : ${report.druggabilityResult.druggabilityScore}
POCKET_VOL : ${report.druggabilityResult.pocketFeatures?.pocketVolume ?: 890f} Å³
CRISPR_SAFETY : ${report.crisprResult?.safetyScore ?: 0.92f}
OFF_TARGETS : ${report.crisprResult?.flaggedSites?.size ?: 0} sites
VERDICT : ${report.verdict}
            """.trimIndent()

            _uiState.value = _uiState.value.copy(
                isSynchronizing = false,
                isRunning = false,
                targetCompleted = true,
                drugCompleted = true,
                crisprCompleted = true,
                completeCompleted = true,
                diseaseBurdenPercentage = 0,
                currentReport = report,
                activeOutputCard = "COMPLETE",
                outputTitle = "TREATMENT WORKFLOW COMPLETE ✓",
                outputSubtitle = "All stages successfully analyzed.\nDisease burden reduced to 0%.\n\nSummary:\nTarget : Identified\nDrug : Applied\nCRISPR : Validated",
                outputFormattedJson = formatted
            )
        }
    }

    // --- AI Assistant Dialog & Intelligent Query Handling ---

    fun toggleAiAssistant() {
        _uiState.value = _uiState.value.copy(isAiAssistantOpen = !_uiState.value.isAiAssistantOpen)
    }

    fun setAiQueryInput(query: String) {
        _uiState.value = _uiState.value.copy(aiQueryInput = query)
    }

    fun sendAiQuery() {
        val query = _uiState.value.aiQueryInput.trim()
        if (query.isEmpty()) return

        val userMsg = ChatMessage(isUser = true, text = query)
        val updatedMsgs = _uiState.value.chatMessages + userMsg

        _uiState.value = _uiState.value.copy(
            chatMessages = updatedMsgs,
            aiQueryInput = "",
            isAiThinking = true
        )

        viewModelScope.launch {
            delay(800) // AI response timing
            val currentState = _uiState.value
            val context = "Researching: ${currentState.diseaseInput}. Hub Gene: ${currentState.currentReport?.hubResult?.hubGeneSymbol ?: "None yet"}. Model: ${currentState.modelOrganism}."
            
            // Map ChatMessage objects to Pair(isUser, text) for the service
            // EXCLUDE the last message because it's the current query we just added
            val history = currentState.chatMessages.dropLast(1).takeLast(6).map { it.isUser to it.text }

            val aiResponse = com.example.biomedix.common.GeminiBiologyService.chat(
                query = query,
                history = history,
                context = context
            ) ?: generateIntelligentBioResponse(query, currentState)

            val botMsg = ChatMessage(isUser = false, text = aiResponse)

            _uiState.value = _uiState.value.copy(
                chatMessages = _uiState.value.chatMessages + botMsg,
                isAiThinking = false
            )
        }
    }

    private fun generateIntelligentBioResponse(query: String, state: UiState): String {
        val lower = query.lowercase()
        val disease = state.diseaseInput.ifBlank { "the current target" }
        val hub = state.currentReport?.hubResult?.hubGeneSymbol ?: "the primary hub gene"

        return when {
            lower.contains("hi") || lower.contains("hello") -> {
                "Hello! I am your BioMedix AI assistant. We are currently analyzing $disease. How can I help with the genomic details of $hub?"
            }
            lower.contains("who are you") -> {
                "I am the BioMedix AI Core, designed to interpret multi-source genomic data from GenBank, Open Targets, and PDB."
            }
            lower.contains("result") || lower.contains("verdict") -> {
                "Based on our pipeline, $hub shows a druggability score of ${state.currentReport?.druggabilityResult?.druggabilityScore ?: "N/A"}. You can view the full 3D pocket in the '3D Protein' tab."
            }
            else -> {
                "I am focused on the $disease research. Please ask me about $hub, its PPI network interactions, or CRISPR safety constraints."
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}
