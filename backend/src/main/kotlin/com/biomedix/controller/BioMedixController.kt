package com.biomedix.controller

import com.biomedix.biokt.BioKt
import com.biomedix.biokt.FastaRecord
import com.biomedix.model.PipelineExecutionRequest
import com.biomedix.model.PipelineExecutionResponse
import com.biomedix.model.TherapeuticReportEntity
import com.biomedix.module1_network.NetworkBiologyService
import com.biomedix.module2_structural.StructuralMlService
import com.biomedix.module3_genomic.GenomicDlService
import com.biomedix.module4_orchestration.OrchestrationService
import com.biomedix.repository.TherapeuticReportRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"])
class BioMedixController(
    private val orchestrationService: OrchestrationService,
    private val networkBiologyService: NetworkBiologyService,
    private val structuralMlService: StructuralMlService,
    private val genomicDlService: GenomicDlService,
    private val repository: TherapeuticReportRepository
) {

    @PostMapping("/pipeline/run")
    fun runFullPipeline(@RequestBody request: PipelineExecutionRequest): ResponseEntity<PipelineExecutionResponse> {
        val result = orchestrationService.executePipeline(request)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/network/{disease}")
    fun analyzeNetwork(@PathVariable disease: String) =
        ResponseEntity.ok(networkBiologyService.identifyHubGene(disease))

    @GetMapping("/structural/{geneSymbol}")
    fun analyzeStructure(@PathVariable geneSymbol: String) =
        ResponseEntity.ok(structuralMlService.analyzeStructure(geneSymbol))

    @GetMapping("/genomic/{geneSymbol}")
    fun analyzeGenomics(
        @PathVariable geneSymbol: String,
        @RequestParam(required = false) customGrna: String?,
        @RequestParam(defaultValue = "NGG") pamMotif: String
    ) = ResponseEntity.ok(genomicDlService.analyzeGenomics(geneSymbol, customGrna, pamMotif))

    @PostMapping("/biokt/fasta/parse")
    fun parseFastaWithBioKt(@RequestBody rawFasta: String): ResponseEntity<List<FastaRecord>> {
        val parsed = BioKt.parseFasta(rawFasta)
        return ResponseEntity.ok(parsed)
    }

    @PostMapping("/biokt/crispr/scan")
    fun scanCrisprPam(@RequestBody sequence: String, @RequestParam(defaultValue = "NGG") pam: String) =
        ResponseEntity.ok(BioKt.findCrisprPamSites(sequence, pamMotif = pam))

    @GetMapping("/reports")
    fun getAllReports(): ResponseEntity<List<TherapeuticReportEntity>> =
        ResponseEntity.ok(repository.findAll())

    @GetMapping("/reports/{id}")
    fun getReportById(@PathVariable id: String): ResponseEntity<TherapeuticReportEntity> {
        val report = repository.findById(id)
        return if (report.isPresent) ResponseEntity.ok(report.get()) else ResponseEntity.notFound().build()
    }
}
