package com.example.biomedix.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data contracts for BioMedixAI as defined in IEEE SRS & SDD Section 6.2 / 7.1.
 */

enum class CentralityMethod(val displayName: String) {
    BETWEENNESS("Betweenness Centrality"),
    DEGREE("Degree Centrality"),
    CLOSENESS("Closeness Centrality"),
    EIGENVECTOR("Eigenvector Centrality")
}

data class PipelineParameters(
    val centralityMethod: CentralityMethod = CentralityMethod.BETWEENNESS,
    val stringConfidenceThreshold: Int = 400, // 0 - 1000
    val pamType: String = "NGG",
    val offTargetRiskThreshold: Float = 0.45f,
    val maxCandidateGenes: Int = 15
)

data class GeneInfo(
    val symbol: String,
    val ncbiGeneId: String = "",
    val uniprotAccession: String = "",
    val description: String = "",
    val associationScore: Float = 0.0f,
    val source: String = "DisGeNET"
)

data class PpiEdge(
    val source: String,
    val target: String,
    val combinedScore: Int // 0 - 1000
)

data class HubResult(
    val diseaseName: String,
    val hubGeneSymbol: String,
    val umlsCui: String = "",
    val centralityScores: Map<String, Float>,
    val graphSummary: Map<String, Int>, // "nodes": 12, "edges": 38
    val candidateGenes: List<GeneInfo>,
    val ppiEdges: List<PpiEdge>,
    val usedFallback: Boolean = false,
    val logMessage: String = ""
)

data class PocketFeatureVector(
    val pocketVolume: Float,         // cubic Angstroms
    val hydrophobicityRatio: Float,  // 0.0 - 1.0
    val depth: Float,                // Angstroms
    val polarityScore: Float,        // polar / non-polar ratio
    val liningResidueCount: Int,
    val liningResidues: List<String> = emptyList(),
    val pocketCenter: Triple<Float, Float, Float> = Triple(0f, 0f, 0f)
)

data class CandidatePocket(
    val id: Int,
    val name: String,
    val volume: Float,
    val score: Float,
    val features: PocketFeatureVector
)

data class DruggabilityResult(
    val geneSymbol: String,
    val pdbId: String?,
    val uniprotAccession: String,
    val resolution: Float?,
    val chainCount: Int = 1,
    val druggabilityScore: Float, // 0.0 - 1.0
    val pocketFeatures: PocketFeatureVector?,
    val candidatePockets: List<CandidatePocket> = emptyList(),
    val proteinLength: Int = 0,
    val usedFallback: Boolean = false,
    val logMessage: String = ""
)

data class OffTargetSiteData(
    val siteId: Int = 0,
    val chromosomePosition: String,
    val targetSequence: String,
    val mismatchCount: Int,
    val riskScore: Float, // 0.0 - 1.0 (higher = riskier)
    val cfdScore: Float = 0f,
    val pam: String = "NGG",
    val strand: String = "+"
)

data class CrisprSafetyResult(
    val geneSymbol: String,
    val grnaSequence: String,
    val pamType: String = "NGG",
    val safetyScore: Float, // 0.0 - 1.0 (higher = safer, 1 - max(risk))
    val offTargetCount: Int,
    val totalSitesScanned: Int = 0,
    val flaggedSites: List<OffTargetSiteData> = emptyList(),
    val geneSequenceLength: Int = 0,
    val gcContent: Double = 0.0,
    val usedFallback: Boolean = false,
    val logMessage: String = ""
)

data class IntegratedReport(
    val reportId: Long = 0,
    val runId: Long = 0,
    val diseaseName: String,
    val hubResult: HubResult,
    val druggabilityResult: DruggabilityResult,
    val crisprResult: CrisprSafetyResult?,
    val verdict: String,
    val generatedAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
)

enum class ModuleExecutionState {
    IDLE,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}

data class PipelineExecutionProgress(
    val currentStep: String = "Ready",
    val module1State: ModuleExecutionState = ModuleExecutionState.IDLE,
    val module2State: ModuleExecutionState = ModuleExecutionState.IDLE,
    val module3State: ModuleExecutionState = ModuleExecutionState.IDLE,
    val overallProgress: Float = 0.0f,
    val logs: List<String> = emptyList()
)
