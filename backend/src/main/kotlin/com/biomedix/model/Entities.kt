package com.biomedix.model

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "therapeutic_reports")
data class TherapeuticReportEntity(
    @Id
    @Column(length = 64)
    var id: String = "",

    @Column(name = "disease_name", nullable = false)
    var diseaseName: String = "",

    @Column(name = "hub_gene_symbol", nullable = false, length = 64)
    var hubGeneSymbol: String = "",

    @Column(name = "hub_gene_name", nullable = false)
    var hubGeneName: String = "",

    @Column(name = "uniprot_accession", nullable = false, length = 64)
    var uniprotAccession: String = "",

    @Column(name = "pdb_id", nullable = false, length = 32)
    var pdbId: String = "",

    @Column(name = "grna_sequence", nullable = false, length = 128)
    var grnaSequence: String = "",

    @Column(name = "pam_site", nullable = false, length = 16)
    var pamSite: String = "",

    @Column(name = "druggability_score", nullable = false)
    var druggabilityScore: Double = 0.0,

    @Column(name = "crispr_off_target_score", nullable = false)
    var crisprOffTargetScore: Double = 0.0,

    @Column(name = "clinical_verdict", nullable = false, length = 64)
    var clinicalVerdict: String = "",

    @Column(name = "synthesis_report", columnDefinition = "TEXT", nullable = false)
    var synthesisReport: String = "",

    @Type(JsonType::class)
    @Column(name = "pocket_features", columnDefinition = "jsonb", nullable = false)
    var pocketFeatures: PocketFeaturesData = PocketFeaturesData(),

    @Type(JsonType::class)
    @Column(name = "off_target_sites", columnDefinition = "jsonb", nullable = false)
    var offTargetSites: List<OffTargetSiteData> = emptyList(),

    @Column(name = "execution_time_ms", nullable = false)
    var executionTimeMs: Long = 0,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()
)

data class PocketFeaturesData(
    var pocketId: String = "POCKET_01",
    var volume: Double = 842.5,
    var hydrophobicity: Double = 0.68,
    var depth: Double = 14.2,
    var surfaceArea: Double = 512.0,
    var keyResidues: List<String> = listOf("ASP112", "GLU145", "HIS210", "TRP289")
) : Serializable

data class OffTargetSiteData(
    var locus: String = "chr3:12849102",
    var sequence: String = "",
    var mismatches: Int = 1,
    var cleavageScore: Double = 0.042,
    var isFlagged: Boolean = false
) : Serializable

data class PipelineExecutionRequest(
    val disease: String,
    val customGrna: String? = null,
    val pamMotif: String = "NGG",
    val prioritizeSmallMolecule: Boolean = true
)

data class PipelineExecutionResponse(
    val reportId: String,
    val status: String,
    val disease: String,
    val hubGene: HubGeneSummary,
    val structuralAnalysis: StructuralSummary,
    val genomicAnalysis: GenomicSummary,
    val therapeuticVerdict: String,
    val fullSynthesis: String,
    val executionDurationMs: Long
)

data class HubGeneSummary(
    val symbol: String,
    val name: String,
    val diseaseScore: Double,
    val degreeCentrality: Double,
    val candidateCount: Int
)

data class StructuralSummary(
    val uniprotAccession: String,
    val pdbId: String,
    val druggabilityScore: Double,
    val pocketVolume: Double,
    val hydrophobicity: Double,
    val keyBindingResidues: List<String>
)

data class GenomicSummary(
    val grnaSequence: String,
    val pamSite: String,
    val gcContent: Double,
    val offTargetCleavageScore: Double,
    val safetyTier: String,
    val flaggedLociCount: Int
)
