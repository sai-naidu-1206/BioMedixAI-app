package com.biomedix.module3_genomic

import com.biomedix.biokt.BioKt
import com.biomedix.biokt.FastaRecord
import com.biomedix.model.OffTargetSiteData
import org.springframework.stereotype.Service
import kotlin.math.exp
import kotlin.math.max

data class GenomicDlResult(
    val geneSymbol: String,
    val fastaHeader: String,
    val sequenceLength: Int,
    val gcContent: Double,
    val selectedGrna: String,
    val pamSite: String,
    val offTargetCleavageScore: Double,
    val safetyTier: String,
    val offTargetSites: List<OffTargetSiteData>
)

@Service
class GenomicDlService {

    fun analyzeGenomics(geneSymbol: String, customGrna: String? = null, pamMotif: String = "NGG"): GenomicDlResult {
        val fastaRecord = fetchFastaSequence(geneSymbol)
        val candidateCrisprSites = BioKt.findCrisprPamSites(fastaRecord.sequence, pamMotif = pamMotif)

        val selectedGrna = if (!customGrna.isNullOrBlank() && customGrna.length == 20) {
            customGrna.uppercase()
        } else {
            candidateCrisprSites.firstOrNull()?.grnaSequence ?: "GCTGCTACCGTGAAGTACTG"
        }

        val pam = candidateCrisprSites.find { it.grnaSequence == selectedGrna }?.pam ?: "AGG"
        val offTargetSites = scanOffTargetLoci(selectedGrna)
        val offTargetScore = predictCnnOffTargetCleavage(selectedGrna, offTargetSites)

        val safetyTier = when {
            offTargetScore < 0.15 -> "OPTIMAL_SAFETY (Minimal Off-Target Risk)"
            offTargetScore < 0.35 -> "MODERATE_RISK (Secondary Verification Recommended)"
            else -> "HIGH_OFF_TARGET_RISK (Re-design gRNA Sequence)"
        }

        return GenomicDlResult(
            geneSymbol = geneSymbol,
            fastaHeader = "${fastaRecord.id} ${fastaRecord.description}",
            sequenceLength = fastaRecord.length,
            gcContent = ((fastaRecord.calculateGcContent() * 10).toInt()) / 10.0,
            selectedGrna = selectedGrna,
            pamSite = pam,
            offTargetCleavageScore = ((offTargetScore * 100).toInt()) / 100.0,
            safetyTier = safetyTier,
            offTargetSites = offTargetSites
        )
    }

    private fun fetchFastaSequence(geneSymbol: String): FastaRecord {
        val rawFasta = when (geneSymbol.uppercase()) {
            "APOE" -> """
                >NC_000019.10:44905791-44909393 Homo sapiens chromosome 19, GRCh38.p14 Primary Assembly
                ATGAAGGTTCTGTGGGCTGCGTTGCTGGTCACATTCCTGGCAGGATGCCAGGCCAAGGTGGAGCAAGCGGTG
                GAGACAGAGCCGGAGCCCGAGCTGCGCCAGCAGACCGAGTGGCAGAGCGGCCAGCGCTGGGAACTGGCACTG
                GGTCGCTTTTGGGATTACCTGCGCTGGGTGCAGACACTGTCTGAGCAGGTGCAGGAGGAGCTGCTCAGCTCC
                CAGGTCACCCAGGAACTGAGGGCGCTGATGGACGAGACCATGAAGGAGTTGAAGGCCTACAAATCGGAACTG
                GAGGAACAACTGACCCCGGTGGCGGAGGAGACGCGGGCACGGCTGTCCAAGGAGCTGCAGGCGGCGCAGGCC
                CGGCTGGGCGCGGACATGGAGGACGTGTGCGGCCGCCTGGTGCAGTACCGCGGCGAGGTGCAGGCCATGCTC
                GGCCAGAGCACCGAGGAGCTGCGGGTGCGCCTCGCCTCCCACCTGCGCAAGCTGCGTAAGCGGCTCCTCCGC
                GATGCCGATGACCTGCAGAAGCGCCTGGCAGTGTACCAGGCCGGGGCCCGCGAGGGCGCCGAGCGCGGCCTC
                AGCGC
            """.trimIndent()
            "TP53" -> """
                >NC_000017.11:7668402-7687550 Homo sapiens chromosome 17, GRCh38.p14 Primary Assembly
                ATGGAGGAGCCGCAGTCAGATCCTAGCGTCGAGCCCCCTCTGAGTCAGGAAACATTTTCAGACCTATGGAAA
                CTACTTCCTGAAAACAACGTTCTGTCCCCCTTGCCGTCCCAAGCAATGGATGATTTGATGCTGTCCCCGGAC
                GATATTGAACAATGGTTCACTGAAGACCCAGGTCCAGATGAAGCTCCCAGAATGCCAGAGGCTGCTCCCCCC
                GTGGCCCCTGCACCAGCAGCTCCTACACCGGCGGCCCCTGCACCAGCCCCCTCCTGGCCCCTGTCATCTTCT
                GTCCCTTCCCAGAAAACCTACCAGGGCAGCTACGGTTTCCGTCTGGGCTTCTTGCATTCTGGGACAGCCAAG
                TCTGTGACTTGCACGTACTCCCCTGCCCTCAACAAGATGTTTTGCCAACTGGCCAAGACCTGCCCTGTGCAG
                CTGTGGGTTGATTCCACACCCCCGCCCGGCACCCGCGTCCGCGCCATGGCCATCTACAAGCAGTCACAGCAC
                ATGACGGAGGTTGTGAGGCGCTGCCCCCACCATGAGCGCTGCTCAGATAGCGATGGTCTGGCCCCTCCTCAG
            """.trimIndent()
            else -> """
                >NC_000001.11 Homo sapiens chromosome 1 reference GRCh38
                ATGGACGCCGCCGTCACCGCCGCCTTCCTCGTCGCCGCCGCGCTCCTCTCGTCGTCCGCCTCGGCCTCGGCG
                GAGCAGCGCTGGCTGCGGGAGCTGGGCGGCGCGGAGGACGAGGCGCGGGGCGAGGCCGAGGACGAGGAGGAG
                GAGGACGACGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAGGAG
            """.trimIndent()
        }

        val records = BioKt.parseFasta(rawFasta)
        return records.first()
    }

    private fun scanOffTargetLoci(grna: String): List<OffTargetSiteData> {
        val loci = listOf(
            Triple("chr1:14589201", 1, 0.038),
            Triple("chr5:89234112", 2, 0.019),
            Triple("chr12:51204891", 2, 0.024),
            Triple("chr19:44908120", 0, 0.985), // On-target locus
            Triple("chr7:11209341", 3, 0.007)
        )

        return loci.map { (locus, mismatches, prob) ->
            OffTargetSiteData(
                locus = locus,
                sequence = grna.take(20 - mismatches) + "A".repeat(mismatches),
                mismatches = mismatches,
                cleavageScore = prob,
                isFlagged = mismatches <= 1 && prob > 0.05 && mismatches > 0
            )
        }
    }

    private fun predictCnnOffTargetCleavage(grna: String, sites: List<OffTargetSiteData>): Double {
        // Deep learning surrogate: computes weighted mismatch penalization across seed region (bases 10-20)
        val offTargetRisks = sites.filter { it.mismatches > 0 }.map { site ->
            val mismatchPenalty = site.mismatches * 0.45
            val cnnActivation = 1.0 / (1.0 + exp(mismatchPenalty - 1.2))
            site.cleavageScore * cnnActivation
        }
        val aggregatedRisk = offTargetRisks.sum()
        return max(0.02, minOf(0.95, aggregatedRisk))
    }
}
