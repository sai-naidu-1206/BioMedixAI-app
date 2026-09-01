package com.example.biomedix.biokt

import kotlin.math.abs

/**
 * BioKt - Core Kotlin Bioinformatics Library for sequence handling,
 * FASTA parsing, genomic computations, and CRISPR motif operations.
 */
enum class SequenceType {
    DNA,
    RNA,
    PROTEIN
}

data class FastaRecord(
    val id: String,
    val description: String,
    val sequence: String,
    val type: SequenceType = SequenceType.DNA
) {
    val length: Int get() = sequence.length

    fun calculateGcContent(): Double {
        if (sequence.isEmpty()) return 0.0
        val gcCount = sequence.count { it == 'G' || it == 'C' || it == 'g' || it == 'c' }
        return (gcCount.toDouble() / sequence.length) * 100.0
    }

    fun nucleotideCounts(): Map<Char, Int> {
        val counts = mutableMapOf('A' to 0, 'T' to 0, 'C' to 0, 'G' to 0, 'N' to 0)
        for (char in sequence.uppercase()) {
            counts[char] = (counts[char] ?: 0) + 1
        }
        return counts
    }
}

object BioKt {

    /**
     * Validates if a nucleotide string contains only standard bases.
     */
    fun isValidDna(sequence: String): Boolean {
        val valid = setOf('A', 'C', 'G', 'T', 'N', 'a', 'c', 'g', 't', 'n')
        return sequence.isNotEmpty() && sequence.all { it in valid }
    }

    fun isValidRna(sequence: String): Boolean {
        val valid = setOf('A', 'C', 'G', 'U', 'N', 'a', 'c', 'g', 'u', 'n')
        return sequence.isNotEmpty() && sequence.all { it in valid }
    }

    /**
     * Parses standard FASTA string into structured FastaRecord objects.
     */
    fun parseFasta(fastaContent: String): List<FastaRecord> {
        val records = mutableListOf<FastaRecord>()
        var currentHeader = ""
        var currentDescription = ""
        val currentSeq = StringBuilder()

        for (rawLine in fastaContent.lines()) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            if (line.startsWith(">")) {
                if (currentHeader.isNotEmpty()) {
                    records.add(
                        FastaRecord(
                            id = currentHeader,
                            description = currentDescription,
                            sequence = currentSeq.toString().uppercase(),
                            type = SequenceType.DNA
                        )
                    )
                    currentSeq.clear()
                }
                val headerParts = line.substring(1).trim().split(" ", limit = 2)
                currentHeader = headerParts.firstOrNull() ?: "seq"
                currentDescription = if (headerParts.size > 1) headerParts[1] else ""
            } else {
                currentSeq.append(line)
            }
        }

        if (currentHeader.isNotEmpty() && currentSeq.isNotEmpty()) {
            records.add(
                FastaRecord(
                    id = currentHeader,
                    description = currentDescription,
                    sequence = currentSeq.toString().uppercase(),
                    type = SequenceType.DNA
                )
            )
        }

        return records
    }

    /**
     * Computes reverse complement for a DNA sequence.
     */
    fun reverseComplement(dna: String): String {
        return dna.reversed().map {
            when (it.uppercaseChar()) {
                'A' -> 'T'
                'T' -> 'A'
                'C' -> 'G'
                'G' -> 'C'
                'U' -> 'A'
                else -> 'N'
            }
        }.joinToString("")
    }

    /**
     * Transcribes DNA into RNA.
     */
    fun transcribe(dna: String): String {
        return dna.uppercase().replace('T', 'U')
    }

    /**
     * Translates nucleotide sequence into amino acid sequence.
     */
    fun translate(nucleotides: String): String {
        val codonTable = mapOf(
            "TTT" to "F", "TTC" to "F", "TTA" to "L", "TTG" to "L",
            "TCT" to "S", "TCC" to "S", "TCA" to "S", "TCG" to "S",
            "TAT" to "Y", "TAC" to "Y", "TAA" to "*", "TAG" to "*",
            "TGT" to "C", "TGC" to "C", "TGA" to "*", "TGG" to "W",
            "CTT" to "L", "CTC" to "L", "CTA" to "L", "CTG" to "L",
            "CCT" to "P", "CCC" to "P", "CCA" to "P", "CCG" to "P",
            "CAT" to "H", "CAC" to "H", "CAA" to "Q", "CAG" to "Q",
            "CGT" to "R", "CGC" to "R", "CGA" to "R", "CGG" to "R",
            "ATT" to "I", "ATC" to "I", "ATA" to "I", "ATG" to "M",
            "ACT" to "T", "ACC" to "T", "ACA" to "T", "ACG" to "T",
            "AAT" to "N", "AAC" to "N", "AAA" to "K", "AAG" to "K",
            "AGT" to "S", "AGC" to "S", "AGA" to "R", "AGG" to "R",
            "GTT" to "V", "GTC" to "V", "GTA" to "V", "GTG" to "V",
            "GCT" to "A", "GCC" to "A", "GCA" to "A", "GCG" to "A",
            "GAT" to "D", "GAC" to "D", "GAA" to "E", "GAG" to "E",
            "GGT" to "G", "GGC" to "G", "GGA" to "G", "GGG" to "G"
        )
        val cleanSeq = nucleotides.uppercase().replace("U", "T")
        val protein = StringBuilder()
        for (i in 0 until cleanSeq.length - 2 step 3) {
            val codon = cleanSeq.substring(i, i + 3)
            val aa = codonTable[codon] ?: "X"
            protein.append(aa)
        }
        return protein.toString()
    }

    /**
     * Calculates Hamming distance between two equal-length sequences.
     */
    fun hammingDistance(seq1: String, seq2: String): Int {
        val minLen = minOf(seq1.length, seq2.length)
        var dist = abs(seq1.length - seq2.length)
        for (i in 0 until minLen) {
            if (seq1[i].uppercaseChar() != seq2[i].uppercaseChar()) {
                dist++
            }
        }
        return dist
    }

    /**
     * Scans a target sequence for CRISPR PAM motifs (e.g. NGG, NAG)
     * and extracts 20bp protospacer candidate binding sites.
     */
    fun scanPamSites(sequence: String, pamType: String = "NGG", targetLength: Int = 20): List<PamSiteMatch> {
        val matches = mutableListOf<PamSiteMatch>()
        val upper = sequence.uppercase()
        val totalLength = targetLength + pamType.length

        for (i in 0..upper.length - totalLength) {
            val window = upper.substring(i, i + totalLength)
            val protospacer = window.substring(0, targetLength)
            val pam = window.substring(targetLength)

            val isPamMatch = matchesPam(pam, pamType)
            if (isPamMatch) {
                matches.add(
                    PamSiteMatch(
                        start = i,
                        end = i + totalLength,
                        protospacer = protospacer,
                        pam = pam,
                        strand = "+",
                        fullSequence = window
                    )
                )
            }
        }

        // Also scan reverse complement
        val rc = reverseComplement(upper)
        for (i in 0..rc.length - totalLength) {
            val window = rc.substring(i, i + totalLength)
            val protospacer = window.substring(0, targetLength)
            val pam = window.substring(targetLength)

            if (matchesPam(pam, pamType)) {
                matches.add(
                    PamSiteMatch(
                        start = upper.length - (i + totalLength),
                        end = upper.length - i,
                        protospacer = protospacer,
                        pam = pam,
                        strand = "-",
                        fullSequence = window
                    )
                )
            }
        }

        return matches
    }

    private fun matchesPam(candidate: String, pattern: String): Boolean {
        if (candidate.length != pattern.length) return false
        for (i in candidate.indices) {
            val p = pattern[i].uppercaseChar()
            val c = candidate[i].uppercaseChar()
            if (p != 'N' && p != c) {
                return false
            }
        }
        return true
    }

    /**
     * Generates a 2D matrix / one-hot representation of sequence for DL inputs.
     * Shape: (length, 4) where A=[1,0,0,0], C=[0,1,0,0], G=[0,0,1,0], T=[0,0,0,1]
     */
    fun oneHotEncode(sequence: String): Array<FloatArray> {
        val result = Array(sequence.length) { FloatArray(4) }
        for (i in sequence.indices) {
            when (sequence[i].uppercaseChar()) {
                'A' -> result[i][0] = 1.0f
                'C' -> result[i][1] = 1.0f
                'G' -> result[i][2] = 1.0f
                'T', 'U' -> result[i][3] = 1.0f
                else -> {
                    // N or unknown - 0.25 uniform
                    result[i][0] = 0.25f
                    result[i][1] = 0.25f
                    result[i][2] = 0.25f
                    result[i][3] = 0.25f
                }
            }
        }
        return result
    }
}

data class PamSiteMatch(
    val start: Int,
    val end: Int,
    val protospacer: String,
    val pam: String,
    val strand: String,
    val fullSequence: String
)
