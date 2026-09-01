package com.biomedix.biokt

import kotlin.math.abs

/**
 * BioKt - Modern Kotlin Bioinformatics Core Engine
 * Sequence parsing, FASTA manipulation, and genomic computations.
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

    fun isValidDna(sequence: String): Boolean {
        val valid = setOf('A', 'C', 'G', 'T', 'N', 'a', 'c', 'g', 't', 'n')
        return sequence.isNotEmpty() && sequence.all { it in valid }
    }

    fun isValidRna(sequence: String): Boolean {
        val valid = setOf('A', 'C', 'G', 'U', 'N', 'a', 'c', 'g', 'u', 'n')
        return sequence.isNotEmpty() && sequence.all { it in valid }
    }

    fun parseFasta(fastaContent: String): List<FastaRecord> {
        val records = mutableListOf<FastaRecord>()
        var currentHeader = ""
        var currentDescription = ""
        val currentSeq = StringBuilder()

        fastaContent.lines().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.startsWith(">")) {
                if (currentHeader.isNotEmpty()) {
                    records.add(
                        FastaRecord(
                            id = currentHeader,
                            description = currentDescription,
                            sequence = currentSeq.toString().uppercase()
                        )
                    )
                    currentSeq.clear()
                }
                val headerTokens = line.substring(1).trim().split(" ", limit = 2)
                currentHeader = headerTokens[0]
                currentDescription = if (headerTokens.size > 1) headerTokens[1] else ""
            } else if (line.isNotEmpty()) {
                currentSeq.append(line)
            }
        }

        if (currentHeader.isNotEmpty()) {
            records.add(
                FastaRecord(
                    id = currentHeader,
                    description = currentDescription,
                    sequence = currentSeq.toString().uppercase()
                )
            )
        }

        return records
    }

    fun formatFasta(record: FastaRecord, lineLength: Int = 60): String {
        val sb = StringBuilder()
        sb.append(">").append(record.id)
        if (record.description.isNotEmpty()) {
            sb.append(" ").append(record.description)
        }
        sb.append("\n")
        record.sequence.chunked(lineLength).forEach { chunk ->
            sb.append(chunk).append("\n")
        }
        return sb.toString()
    }

    fun reverseComplement(dna: String): String {
        return dna.uppercase().reversed().map { char ->
            when (char) {
                'A' -> 'T'
                'T' -> 'A'
                'C' -> 'G'
                'G' -> 'C'
                'N' -> 'N'
                else -> char
            }
        }.joinToString("")
    }

    fun transcribe(dna: String): String {
        return dna.uppercase().replace('T', 'U')
    }

    fun findCrisprPamSites(sequence: String, pamMotif: String = "NGG", grnaLength: Int = 20): List<CrisprSite> {
        val results = mutableListOf<CrisprSite>()
        val upperSeq = sequence.uppercase()

        for (i in 0..(upperSeq.length - grnaLength - 3)) {
            val potentialGrna = upperSeq.substring(i, i + grnaLength)
            val potentialPam = upperSeq.substring(i + grnaLength, i + grnaLength + 3)

            val matchesPam = when (pamMotif) {
                "NGG" -> potentialPam.length == 3 && potentialPam[1] == 'G' && potentialPam[2] == 'G'
                "NAG" -> potentialPam.length == 3 && potentialPam[1] == 'A' && potentialPam[2] == 'G'
                "NGA" -> potentialPam.length == 3 && potentialPam[1] == 'G' && potentialPam[2] == 'A'
                else -> true
            }

            if (matchesPam) {
                val gc = (potentialGrna.count { it == 'G' || it == 'C' }.toDouble() / grnaLength) * 100.0
                results.add(
                    CrisprSite(
                        position = i + 1,
                        grnaSequence = potentialGrna,
                        pam = potentialPam,
                        gcContent = gc,
                        strand = "+"
                    )
                )
            }
        }
        return results
    }

    fun computeHammingDistance(s1: String, s2: String): Int {
        if (s1.length != s2.length) return -1
        return s1.zip(s2).count { (c1, c2) -> c1 != c2 }
    }
}

data class CrisprSite(
    val position: Int,
    val grnaSequence: String,
    val pam: String,
    val gcContent: Double,
    val strand: String
)
