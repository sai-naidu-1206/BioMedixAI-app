package com.example

import com.example.biomedix.biokt.BioKt
import com.example.biomedix.common.CentralityMethod
import com.example.biomedix.common.PocketFeatureVector
import com.example.biomedix.module1_network.NetworkAnalyzer
import com.example.biomedix.module2_structural.DruggabilityEngine
import com.example.biomedix.module3_genomic.OffTargetCNN
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBioKtFastaParser() {
        val fasta = ">APOE Apolipoprotein E\nATGAAGGTTCTGTGGGCTGCGTTGCTGGTCACATTC"
        val records = BioKt.parseFasta(fasta)
        assertEquals(1, records.size)
        assertEquals("APOE", records[0].id)
        assertTrue(records[0].calculateGcContent() > 0)
    }

    @Test
    fun testBioKtPamScanning() {
        val dna = "ATGAAGGTTCTGTGGGCTGCGTTGCTGGTCACATTC"
        val matches = BioKt.scanPamSites(dna, "NGG", 20)
        assertTrue(matches.isNotEmpty())
    }

    @Test
    fun testReverseComplementAndTranslation() {
        val dna = "ATGGCCCTG"
        val rc = BioKt.reverseComplement(dna)
        assertEquals("CAGGGCCAT", rc)
        val protein = BioKt.translate(dna)
        assertEquals("MAL", protein)
    }

    @Test
    fun testNetworkCentralityCalculation() {
        val analyzer = NetworkAnalyzer()
        val nodes = listOf("APOE", "APP", "PSEN1")
        val scores = analyzer.computeCentrality(nodes, emptyList(), CentralityMethod.DEGREE)
        assertEquals(3, scores.size)
    }

    @Test
    fun testDruggabilityEngine() {
        val engine = DruggabilityEngine()
        val features = PocketFeatureVector(
            pocketVolume = 1100f,
            hydrophobicityRatio = 0.55f,
            depth = 14f,
            polarityScore = 0.8f,
            liningResidueCount = 18
        )
        val score = engine.predictDruggability(features)
        assertTrue(score in 0.0f..1.0f)
    }

    @Test
    fun testOffTargetCNN() {
        val cnn = OffTargetCNN()
        val grna = "GAGTCCGAGCAGAAGAAGAA"
        val target = "GAGTCCGAGCAGAAGAAGAA"
        val onTargetRisk = cnn.predictSiteRisk(grna, target)
        assertEquals(0.99f, onTargetRisk, 0.01f)
    }
}
