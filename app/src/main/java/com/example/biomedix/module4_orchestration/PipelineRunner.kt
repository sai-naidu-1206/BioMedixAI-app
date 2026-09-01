package com.example.biomedix.module4_orchestration

import com.example.biomedix.common.CrisprSafetyResult
import com.example.biomedix.common.DruggabilityResult
import com.example.biomedix.common.GeminiBiologyService
import com.example.biomedix.common.HubResult
import com.example.biomedix.common.IntegratedReport
import com.example.biomedix.common.ModuleExecutionState
import com.example.biomedix.common.PipelineExecutionProgress
import com.example.biomedix.common.PipelineParameters
import com.example.biomedix.module1_network.TargetDiscoveryPipeline
import com.example.biomedix.module2_structural.StructuralMLPipeline
import com.example.biomedix.module3_genomic.CrisprSafetyEngine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object VerdictGenerator {
    fun generate(druggability: DruggabilityResult, crispr: CrisprSafetyResult?): String {
        val dScore = druggability.druggabilityScore
        val cScore = crispr?.safetyScore ?: 0.0f

        return when {
            crispr == null -> {
                if (dScore >= 0.70f) "Strong small-molecule candidate: High pocket druggability ($dScore)"
                else "Moderate/Low small-molecule druggability ($dScore)"
            }
            dScore >= 0.70f && cScore >= 0.70f ->
                "Strong dual candidate: High pocket druggability ($dScore) & low off-target CRISPR cleavage risk (Safety: $cScore)."
            dScore >= 0.70f && cScore < 0.70f ->
                "Promising small-molecule target ($dScore); gene-editing pathway carries elevated off-target cleavage risk (Safety: $cScore)."
            dScore < 0.70f && cScore >= 0.70f ->
                "Low small-molecule druggability ($dScore); gene-editing CRISPR route appears significantly safer (Safety: $cScore)."
            else ->
                "Neither pathway exhibits high therapeutic promise for target ${druggability.geneSymbol} (Druggability: $dScore, CRISPR Safety: $cScore)."
        }
    }
}

class PipelineRunner(
    private val module1: TargetDiscoveryPipeline = TargetDiscoveryPipeline(),
    private val module2: StructuralMLPipeline = StructuralMLPipeline(),
    private val module3: CrisprSafetyEngine = CrisprSafetyEngine()
) {
    suspend fun runFullPipeline(
        diseaseName: String,
        grnaSequence: String,
        params: PipelineParameters = PipelineParameters(),
        onProgress: (PipelineExecutionProgress) -> Unit = {}
    ): IntegratedReport = coroutineScope {
        val logs = mutableListOf<String>()
        fun emit(step: String, m1: ModuleExecutionState, m2: ModuleExecutionState, m3: ModuleExecutionState, prog: Float, log: String? = null) {
            if (log != null) logs.add(log)
            onProgress(PipelineExecutionProgress(step, m1, m2, m3, prog, logs.toList()))
        }

        emit("Initializing Discovery Pipeline...", ModuleExecutionState.RUNNING, ModuleExecutionState.IDLE, ModuleExecutionState.IDLE, 0.1f, "Started pipeline run for: $diseaseName")

        // Step 1: Network Biology
        emit("Module 1: Querying PPI Network & Computing Centrality...", ModuleExecutionState.RUNNING, ModuleExecutionState.IDLE, ModuleExecutionState.IDLE, 0.3f, "Fetching DisGeNET associations and STRING DB interactions...")
        val hubResult = module1.run(diseaseName, params)
        emit("Module 1 Complete: Hub Gene = ${hubResult.hubGeneSymbol}", ModuleExecutionState.COMPLETED, ModuleExecutionState.IDLE, ModuleExecutionState.IDLE, 0.45f, hubResult.logMessage)

        // Step 2 & 3: Concurrent Fork Execution
        emit("Forking Concurrent Execution: Structural ML & Genomic DL...", ModuleExecutionState.COMPLETED, ModuleExecutionState.RUNNING, ModuleExecutionState.RUNNING, 0.55f, "Forking Module 2 (3D Structure & Pocket ML) + Module 3 (CRISPR Off-Target DL)...")

        val drugDeferred = async { module2.run(hubResult.hubGeneSymbol) }
        val crisprDeferred = async {
            if (grnaSequence.isNotBlank()) {
                module3.evaluate(hubResult.hubGeneSymbol, grnaSequence, params.pamType, params.offTargetRiskThreshold)
            } else null
        }

        val druggabilityResult = drugDeferred.await()
        emit("Module 2 Complete: Druggability = ${druggabilityResult.druggabilityScore}", ModuleExecutionState.COMPLETED, ModuleExecutionState.COMPLETED, ModuleExecutionState.RUNNING, 0.80f, druggabilityResult.logMessage)

        val crisprResult = crisprDeferred.await()
        if (crisprResult != null) {
            emit("Module 3 Complete: CRISPR Safety = ${crisprResult.safetyScore}", ModuleExecutionState.COMPLETED, ModuleExecutionState.COMPLETED, ModuleExecutionState.COMPLETED, 0.92f, crisprResult.logMessage)
        } else {
            emit("Module 3 Skipped (no gRNA provided)", ModuleExecutionState.COMPLETED, ModuleExecutionState.COMPLETED, ModuleExecutionState.SKIPPED, 0.92f, "No gRNA sequence supplied; skipped CRISPR off-target module.")
        }

        // Step 4: Join and synthesize report
        val aiVerdict = GeminiBiologyService.getIntelligentVerdict(
            diseaseName = diseaseName,
            hubGene = hubResult.hubGeneSymbol,
            druggabilityScore = druggabilityResult.druggabilityScore,
            crisprSafetyScore = crisprResult?.safetyScore ?: 0.5f
        )
        val verdict = aiVerdict ?: VerdictGenerator.generate(druggabilityResult, crisprResult)

        emit("Pipeline Execution Finished.", ModuleExecutionState.COMPLETED, ModuleExecutionState.COMPLETED, if (crisprResult != null) ModuleExecutionState.COMPLETED else ModuleExecutionState.SKIPPED, 1.0f, "Generated verdict: $verdict")

        IntegratedReport(
            diseaseName = diseaseName,
            hubResult = hubResult,
            druggabilityResult = druggabilityResult,
            crisprResult = crisprResult,
            verdict = verdict
        )
    }
}
