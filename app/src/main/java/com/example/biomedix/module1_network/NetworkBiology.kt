package com.example.biomedix.module1_network

import com.example.biomedix.common.CentralityMethod
import com.example.biomedix.common.GeneInfo
import com.example.biomedix.common.HubResult
import com.example.biomedix.common.PipelineParameters
import com.example.biomedix.common.PpiEdge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.ArrayDeque
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

/**
 * Module 1: Network Biology
 * Implements Disease-Gene Ingestion, STRING DB PPI Network construction,
 * and Centrality Analysis (Betweenness, Degree, Closeness, Eigenvector).
 */

interface DiseaseGeneProvider {
    suspend fun getGenesForDisease(diseaseName: String, limit: Int = 15): Pair<String, List<GeneInfo>>
}

class MockGeneProvider : DiseaseGeneProvider {
    private val curatedData = mapOf(
        "alzheimer" to Pair(
            "C0002395",
            listOf(
                GeneInfo("APOE", "348", "P02649", "Apolipoprotein E - critical in lipid transport & amyloid beta clearance", 0.94f, "Curated DisGeNET"),
                GeneInfo("APP", "351", "P05067", "Amyloid beta precursor protein", 0.91f, "Curated DisGeNET"),
                GeneInfo("PSEN1", "5663", "P49768", "Presenilin 1 - catalytic subunit of gamma-secretase", 0.88f, "Curated DisGeNET"),
                GeneInfo("PSEN2", "5664", "P49810", "Presenilin 2", 0.82f, "Curated DisGeNET"),
                GeneInfo("MAPT", "4137", "P10636", "Microtubule associated protein tau - neurofibrillary tangles", 0.86f, "Curated DisGeNET"),
                GeneInfo("TREM2", "54209", "Q9NZC2", "Triggering receptor expressed on myeloid cells 2", 0.79f, "Curated DisGeNET"),
                GeneInfo("CLU", "1191", "P10909", "Clusterin (Apolipoprotein J)", 0.75f, "Curated DisGeNET"),
                GeneInfo("BIN1", "274", "O00499", "Bridging integrator 1", 0.72f, "Curated DisGeNET"),
                GeneInfo("CD33", "945", "P20138", "Myeloid cell surface antigen CD33", 0.68f, "Curated DisGeNET"),
                GeneInfo("ABCA7", "10347", "Q8IZY2", "ATP-binding cassette sub-family A member 7", 0.65f, "Curated DisGeNET"),
                GeneInfo("SORL1", "6653", "Q92673", "Sortilin related receptor 1", 0.62f, "Curated DisGeNET"),
                GeneInfo("CR1", "1378", "P17927", "Complement C3b/C4b receptor 1", 0.60f, "Curated DisGeNET")
            )
        ),
        "diabetes" to Pair(
            "C0011860",
            listOf(
                GeneInfo("INS", "3630", "P01308", "Insulin peptide hormone", 0.95f, "Curated DisGeNET"),
                GeneInfo("INSR", "3643", "P06213", "Insulin receptor tyrosine kinase", 0.89f, "Curated DisGeNET"),
                GeneInfo("PPARG", "5468", "P37231", "Peroxisome proliferator activated receptor gamma", 0.87f, "Curated DisGeNET"),
                GeneInfo("TCF7L2", "6934", "Q9NQB0", "Transcription factor 7 like 2", 0.84f, "Curated DisGeNET"),
                GeneInfo("KCNJ11", "3767", "Q14654", "Potassium inwardly rectifying channel subunit", 0.80f, "Curated DisGeNET"),
                GeneInfo("ABCC8", "6833", "Q09428", "ATP-binding cassette sub-family C member 8", 0.77f, "Curated DisGeNET"),
                GeneInfo("GCK", "2645", "P35557", "Glucokinase glucose sensor", 0.75f, "Curated DisGeNET"),
                GeneInfo("IRS1", "3667", "P35568", "Insulin receptor substrate 1", 0.73f, "Curated DisGeNET"),
                GeneInfo("SLC2A4", "6517", "P14672", "Solute carrier family 2 member 4 (GLUT4)", 0.71f, "Curated DisGeNET"),
                GeneInfo("AKT2", "208", "P31751", "AKT serine/threonine kinase 2", 0.69f, "Curated DisGeNET")
            )
        ),
        "breast cancer" to Pair(
            "C0006142",
            listOf(
                GeneInfo("BRCA1", "672", "P38398", "BRCA1 DNA repair associated", 0.96f, "Curated DisGeNET"),
                GeneInfo("BRCA2", "675", "P51587", "BRCA2 DNA repair associated", 0.93f, "Curated DisGeNET"),
                GeneInfo("TP53", "7157", "P04637", "Tumor protein p53 guardian of the genome", 0.90f, "Curated DisGeNET"),
                GeneInfo("ERBB2", "2064", "P04626", "Erb-B2 receptor tyrosine kinase 2 (HER2)", 0.88f, "Curated DisGeNET"),
                GeneInfo("ESR1", "2099", "P03372", "Estrogen receptor 1", 0.86f, "Curated DisGeNET"),
                GeneInfo("PIK3CA", "5290", "P42336", "Phosphatidylinositol-4,5-bisphosphate 3-kinase catalytic", 0.82f, "Curated DisGeNET"),
                GeneInfo("PTEN", "5728", "P60484", "Phosphatase and tensin homolog", 0.81f, "Curated DisGeNET"),
                GeneInfo("ATM", "472", "Q13315", "ATM serine/threonine kinase", 0.78f, "Curated DisGeNET"),
                GeneInfo("CHEK2", "11200", "O96017", "Checkpoint kinase 2", 0.74f, "Curated DisGeNET"),
                GeneInfo("CDH1", "999", "P12830", "Cadherin 1", 0.70f, "Curated DisGeNET")
            )
        ),
        "parkinson" to Pair(
            "C0030567",
            listOf(
                GeneInfo("SNCA", "6622", "P37840", "Synuclein alpha - Lewy body pathology", 0.95f, "Curated DisGeNET"),
                GeneInfo("PRKN", "5071", "O60260", "Parkin RBR E3 ubiquitin protein ligase", 0.91f, "Curated DisGeNET"),
                GeneInfo("LRRK2", "120892", "Q5S007", "Leucine rich repeat kinase 2", 0.89f, "Curated DisGeNET"),
                GeneInfo("PINK1", "65018", "Q9BXM7", "PTEN induced kinase 1", 0.85f, "Curated DisGeNET"),
                GeneInfo("GBA1", "2629", "P04062", "Glucosylceramidase beta 1", 0.82f, "Curated DisGeNET"),
                GeneInfo("DJ1", "11315", "Q99497", "Parkinsonism associated deglycase", 0.78f, "Curated DisGeNET"),
                GeneInfo("UCHL1", "7345", "P09936", "Ubiquitin C-terminal hydrolase L1", 0.73f, "Curated DisGeNET")
            )
        ),
        "hypertension" to Pair(
            "C0020538",
            listOf(
                GeneInfo("ACE", "1636", "P12821", "Angiotensin converting enzyme", 0.94f, "Curated DisGeNET"),
                GeneInfo("AGT", "183", "P01019", "Angiotensinogen", 0.91f, "Curated DisGeNET"),
                GeneInfo("AGTR1", "185", "P30556", "Angiotensin II receptor type 1", 0.88f, "Curated DisGeNET"),
                GeneInfo("NOS3", "4846", "P29474", "Nitric oxide synthase 3", 0.83f, "Curated DisGeNET"),
                GeneInfo("REN", "5972", "P00797", "Renin", 0.80f, "Curated DisGeNET"),
                GeneInfo("CYP11B2", "1585", "P19099", "Cytochrome P450 family 11 subfamily B member 2", 0.74f, "Curated DisGeNET")
            )
        )
    )

    override suspend fun getGenesForDisease(diseaseName: String, limit: Int): Pair<String, List<GeneInfo>> {
        val query = diseaseName.lowercase().trim()
        
        // 1. Check for nonsense/too short input
        if (query.length < 3 && !query.any { it.isDigit() }) {
             return Pair("C_NONE", emptyList())
        }

        val matchKey = curatedData.keys.firstOrNull { query.contains(it) || it.contains(query) }
        if (matchKey != null) {
            val pair = curatedData[matchKey]!!
            return Pair(pair.first, pair.second.take(limit))
        }

        // 2. If it's a number or very short, it's likely not a real disease name for this demo
        if (query.all { it.isDigit() } || query.length < 3) {
            return Pair("C_NONE", emptyList())
        }

        // Dynamic synthesis for novel query based on hash
        val seed = diseaseName.hashCode().toLong()
        val random = java.util.Random(seed)
        
        // Pool of biologically diverse genes to pick from
        val genePool = listOf(
            GeneInfo("TP53", "7157", "P04637", "Tumor protein p53", 0.95f, "Heuristic"),
            GeneInfo("TNF", "7124", "P01375", "Tumor necrosis factor", 0.92f, "Heuristic"),
            GeneInfo("EGFR", "1956", "P00533", "Epidermal growth factor receptor", 0.88f, "Heuristic"),
            GeneInfo("VEGFA", "7422", "P15692", "Vascular endothelial growth factor A", 0.85f, "Heuristic"),
            GeneInfo("APOE", "348", "P02649", "Apolipoprotein E", 0.82f, "Heuristic"),
            GeneInfo("IL6", "3569", "P05231", "Interleukin 6", 0.80f, "Heuristic"),
            GeneInfo("AKT1", "207", "P31749", "AKT serine/threonine kinase 1", 0.78f, "Heuristic"),
            GeneInfo("MYC", "4609", "P01106", "MYC proto-oncogene", 0.75f, "Heuristic"),
            GeneInfo("KRAS", "3845", "P01116", "KRAS proto-oncogene", 0.72f, "Heuristic"),
            GeneInfo("MTOR", "2475", "P42345", "Mechanistic target of rapamycin kinase", 0.70f, "Heuristic"),
            GeneInfo("STAT3", "6774", "P40763", "Signal transducer and activator of transcription 3", 0.68f, "Heuristic"),
            GeneInfo("BDNF", "627", "P23560", "Brain derived neurotrophic factor", 0.65f, "Heuristic"),
            GeneInfo("ESR1", "2099", "P03372", "Estrogen receptor 1", 0.62f, "Heuristic"),
            GeneInfo("CTNNB1", "1499", "P35222", "Catenin beta 1", 0.60f, "Heuristic")
        )
        
        val shuffled = genePool.shuffled(random)
        val synthesizedCui = "C" + (1000000 + (seed % 9000000).let { if (it < 0) -it else it }).toString()
        
        return Pair(synthesizedCui, shuffled.take(limit))
    }
}

class DisGeNETProvider(
    private val apiKey: String = "",
    private val fallback: DiseaseGeneProvider = MockGeneProvider()
) : DiseaseGeneProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    override suspend fun getGenesForDisease(diseaseName: String, limit: Int): Pair<String, List<GeneInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isEmpty()) {
                    return@withContext fallback.getGenesForDisease(diseaseName, limit)
                }

                val url = "https://api.disgenet.com/api/v2.0/gda/disease/$diseaseName?limit=$limit"
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Accept", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext fallback.getGenesForDisease(diseaseName, limit)
                }

                val body = response.body?.string() ?: ""
                val jsonArray = JSONArray(body)
                val genes = mutableListOf<GeneInfo>()
                var cui = "C_UNKNOWN"

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val symbol = obj.optString("gene_symbol", "")
                    val geneId = obj.optString("geneid", "")
                    val score = obj.optDouble("score", 0.5).toFloat()
                    val desc = obj.optString("disease_name", "")
                    cui = obj.optString("diseaseid", cui)
                    if (symbol.isNotEmpty()) {
                        genes.add(
                            GeneInfo(
                                symbol = symbol,
                                ncbiGeneId = geneId,
                                associationScore = score,
                                description = desc,
                                source = "DisGeNET API v2.0"
                            )
                        )
                    }
                }

                if (genes.isEmpty()) {
                    fallback.getGenesForDisease(diseaseName, limit)
                } else {
                    Pair(cui, genes)
                }
            } catch (e: Exception) {
                fallback.getGenesForDisease(diseaseName, limit)
            }
        }
    }
}

class DataIngestor(
    private val geneProvider: DiseaseGeneProvider = MockGeneProvider()
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun fetchDiseaseGenes(diseaseName: String, limit: Int = 15): Pair<String, List<GeneInfo>> {
        return geneProvider.getGenesForDisease(diseaseName, limit)
    }

    suspend fun fetchStringInteractions(
        genes: List<GeneInfo>,
        minScore: Int = 400
    ): Pair<List<PpiEdge>, Boolean> {
        return withContext(Dispatchers.IO) {
            val symbols = genes.map { it.symbol }
            try {
                val identifiers = symbols.joinToString("%0d")
                val url = "https://string-db.org/api/json/network?identifiers=$identifiers&species=9606&required_score=$minScore"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val arr = JSONArray(body)
                    val edges = mutableListOf<PpiEdge>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val preferredNameA = obj.optString("preferredName_A", "")
                        val preferredNameB = obj.optString("preferredName_B", "")
                        val score = (obj.optDouble("score", 0.0) * 1000).toInt()
                        if (preferredNameA.isNotEmpty() && preferredNameB.isNotEmpty() && score >= minScore) {
                            edges.add(PpiEdge(preferredNameA, preferredNameB, score))
                        }
                    }
                    if (edges.isNotEmpty()) {
                        return@withContext Pair(edges, false)
                    }
                }
                // If empty or failed, generate deterministic fallback
                Pair(generateFallbackPpi(symbols, minScore), true)
            } catch (e: Exception) {
                Pair(generateFallbackPpi(symbols, minScore), true)
            }
        }
    }

    private fun generateFallbackPpi(symbols: List<String>, minScore: Int): List<PpiEdge> {
        val edges = mutableListOf<PpiEdge>()
        if (symbols.isEmpty()) return edges

        // Build a deterministic biological ring + hub backbone network
        for (i in symbols.indices) {
            val next = (i + 1) % symbols.size
            val score = 700 + ((i * 37) % 250)
            if (score >= minScore) {
                edges.add(PpiEdge(symbols[i], symbols[next], score))
            }
        }

        // Add hub connections (top genes connect to more partners)
        val hubCandidate1 = symbols[0]
        val hubCandidate2 = if (symbols.size > 1) symbols[1] else symbols[0]

        for (i in 2 until symbols.size) {
            val score1 = 650 + ((i * 43) % 300)
            if (score1 >= minScore) {
                edges.add(PpiEdge(hubCandidate1, symbols[i], score1))
            }
            if (i % 2 == 0) {
                val score2 = 600 + ((i * 29) % 280)
                if (score2 >= minScore) {
                    edges.add(PpiEdge(hubCandidate2, symbols[i], score2))
                }
            }
        }

        return edges.distinctBy {
            val sorted = listOf(it.source, it.target).sorted()
            "${sorted[0]}-${sorted[1]}"
        }
    }
}

class NetworkAnalyzer {

    fun buildAdjacency(nodes: List<String>, edges: List<PpiEdge>): Map<String, MutableMap<String, Float>> {
        val adj = mutableMapOf<String, MutableMap<String, Float>>()
        nodes.forEach { adj[it] = mutableMapOf() }
        edges.forEach { edge ->
            val w = edge.combinedScore / 1000.0f
            adj.getOrPut(edge.source) { mutableMapOf() }[edge.target] = w
            adj.getOrPut(edge.target) { mutableMapOf() }[edge.source] = w
        }
        return adj
    }

    fun computeCentrality(
        nodes: List<String>,
        edges: List<PpiEdge>,
        method: CentralityMethod = CentralityMethod.BETWEENNESS
    ): Map<String, Float> {
        val adj = buildAdjacency(nodes, edges)

        return when (method) {
            CentralityMethod.BETWEENNESS -> computeBetweenness(nodes, adj)
            CentralityMethod.DEGREE -> computeDegree(nodes, adj)
            CentralityMethod.CLOSENESS -> computeCloseness(nodes, adj)
            CentralityMethod.EIGENVECTOR -> computeEigenvector(nodes, adj)
        }
    }

    private fun computeDegree(nodes: List<String>, adj: Map<String, Map<String, Float>>): Map<String, Float> {
        val maxPossible = if (nodes.size > 1) (nodes.size - 1).toFloat() else 1.0f
        return nodes.associateWith { node ->
            val degree = adj[node]?.size?.toFloat() ?: 0f
            degree / maxPossible
        }
    }

    private fun computeBetweenness(nodes: List<String>, adj: Map<String, Map<String, Float>>): Map<String, Float> {
        // Brandes betweenness centrality algorithm
        val cb = nodes.associateWith { 0.0f }.toMutableMap()

        for (s in nodes) {
            val stack = ArrayDeque<String>()
            val predecessors = nodes.associateWith { mutableListOf<String>() }
            val sigma = nodes.associateWith { 0.0f }.toMutableMap()
            sigma[s] = 1.0f
            val dist = nodes.associateWith { -1 }.toMutableMap()
            dist[s] = 0

            val queue = ArrayDeque<String>()
            queue.add(s)

            while (queue.isNotEmpty()) {
                val v = queue.poll()!!
                stack.push(v)
                val neighbors = adj[v]?.keys ?: emptySet()
                for (w in neighbors) {
                    if (dist[w] == -1) {
                        dist[w] = dist[v]!! + 1
                        queue.add(w)
                    }
                    if (dist[w] == dist[v]!! + 1) {
                        sigma[w] = sigma[w]!! + sigma[v]!!
                        predecessors[w]?.add(v)
                    }
                }
            }

            val delta = nodes.associateWith { 0.0f }.toMutableMap()
            while (stack.isNotEmpty()) {
                val w = stack.pop()
                val preds = predecessors[w] ?: emptyList()
                for (v in preds) {
                    val c = (sigma[v]!! / sigma[w]!!) * (1.0f + delta[w]!!)
                    delta[v] = delta[v]!! + c
                }
                if (w != s) {
                    cb[w] = cb[w]!! + delta[w]!!
                }
            }
        }

        // Normalize betweenness
        val n = nodes.size
        val scale = if (n > 2) 2.0f / ((n - 1) * (n - 2)) else 1.0f
        val normalized = cb.mapValues { it.value * scale }
        val maxVal = normalized.values.maxOrNull() ?: 1.0f
        return if (maxVal > 0f) {
            normalized.mapValues { it.value / maxVal }
        } else {
            normalized
        }
    }

    private fun computeCloseness(nodes: List<String>, adj: Map<String, Map<String, Float>>): Map<String, Float> {
        val n = nodes.size
        return nodes.associateWith { s ->
            val dist = mutableMapOf<String, Int>()
            val queue = ArrayDeque<String>()
            queue.add(s)
            dist[s] = 0

            while (queue.isNotEmpty()) {
                val u = queue.poll()!!
                for (v in adj[u]?.keys ?: emptySet()) {
                    if (!dist.containsKey(v)) {
                        dist[v] = dist[u]!! + 1
                        queue.add(v)
                    }
                }
            }

            val reachable = dist.size
            val sumDist = dist.values.sum()
            if (sumDist > 0 && n > 1) {
                ((reachable - 1).toFloat() / sumDist.toFloat()) * ((reachable - 1).toFloat() / (n - 1).toFloat())
            } else {
                0.0f
            }
        }
    }

    private fun computeEigenvector(
        nodes: List<String>,
        adj: Map<String, Map<String, Float>>,
        maxIter: Int = 100
    ): Map<String, Float> {
        if (nodes.isEmpty()) return emptyMap()
        var vector = nodes.associateWith { 1.0f / sqrt(nodes.size.toDouble()).toFloat() }

        for (iter in 0 until maxIter) {
            val next = mutableMapOf<String, Float>()
            for (node in nodes) {
                var sum = 0.0f
                for ((neighbor, weight) in adj[node] ?: emptyMap()) {
                    sum += weight * (vector[neighbor] ?: 0f)
                }
                next[node] = sum
            }
            val norm = sqrt(next.values.sumOf { (it * it).toDouble() }).toFloat()
            if (norm > 0f) {
                vector = next.mapValues { it.value / norm }
            } else {
                break
            }
        }

        val maxVal = vector.values.maxOrNull() ?: 1.0f
        return if (maxVal > 0f) vector.mapValues { it.value / maxVal } else vector
    }
}

class OpenTargetsProvider : DiseaseGeneProvider {
    private val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build()

    override suspend fun getGenesForDisease(diseaseName: String, limit: Int): Pair<String, List<GeneInfo>> {
        // Simulation of Open Targets GraphQL API logic
        // In a real production app, this would execute a POST to https://api.platform.opentargets.org/api/v4/graphql
        val query = diseaseName.lowercase().trim()
        val genes = when {
            query.contains("leukemia") -> listOf(
                GeneInfo("BCR", "613", "P11274", "Breakpoint cluster region protein", 0.98f, "Open Targets"),
                GeneInfo("ABL1", "25", "P00519", "ABL proto-oncogene 1", 0.97f, "Open Targets"),
                GeneInfo("RUNX1", "861", "Q01196", "RUNX family transcription factor 1", 0.94f, "Open Targets")
            )
            query.contains("obesity") -> listOf(
                GeneInfo("LEP", "3952", "P41159", "Leptin hormone", 0.96f, "Open Targets"),
                GeneInfo("LEPR", "3953", "P48357", "Leptin receptor", 0.93f, "Open Targets"),
                GeneInfo("MC4R", "3975", "P32245", "Melanocortin 4 receptor", 0.91f, "Open Targets")
            )
            else -> emptyList()
        }
        return Pair("OT_${query.take(5)}", genes)
    }
}

class GwasCatalogProvider : DiseaseGeneProvider {
    override suspend fun getGenesForDisease(diseaseName: String, limit: Int): Pair<String, List<GeneInfo>> {
        val query = diseaseName.lowercase().trim()
        val genes = when {
            query.contains("diabetes") -> listOf(
                GeneInfo("TCF7L2", "6934", "Q9NQB0", "Transcription factor 7 like 2", 0.99f, "GWAS Catalog"),
                GeneInfo("SLC30A8", "154467", "Q8IWU4", "Zinc transporter 8", 0.88f, "GWAS Catalog")
            )
            else -> emptyList()
        }
        return Pair("GWAS_${query.take(5)}", genes)
    }
}

class MultiSourceAggregator(
    private val providers: List<DiseaseGeneProvider>
) : DiseaseGeneProvider {
    override suspend fun getGenesForDisease(diseaseName: String, limit: Int): Pair<String, List<GeneInfo>> {
        val allGenes = mutableListOf<GeneInfo>()
        var primaryCui = "C_UNKNOWN"
        
        for (provider in providers) {
            val (cui, genes) = provider.getGenesForDisease(diseaseName, limit)
            if (primaryCui == "C_UNKNOWN" && cui != "C_NONE") primaryCui = cui
            allGenes.addAll(genes)
        }
        
        // Deduplicate and rank by combined score
        val uniqueGenes = allGenes.groupBy { it.symbol }
            .map { (symbol, group) ->
                val best = group.maxByOrNull { it.associationScore }!!
                best.copy(source = group.joinToString("+") { it.source })
            }
            .sortedByDescending { it.associationScore }
            .take(limit)

        return Pair(primaryCui, uniqueGenes)
    }
}

class TargetDiscoveryPipeline(
    private val ingestor: DataIngestor = DataIngestor(
        MultiSourceAggregator(listOf(
            DisGeNETProvider(),
            OpenTargetsProvider(),
            GwasCatalogProvider(),
            MockGeneProvider()
        ))
    ),
    private val analyzer: NetworkAnalyzer = NetworkAnalyzer()
) {
    suspend fun run(diseaseName: String, params: PipelineParameters = PipelineParameters()): HubResult {
        val (cui, genes) = ingestor.fetchDiseaseGenes(diseaseName, params.maxCandidateGenes)
        val (edges, usedPpiFallback) = ingestor.fetchStringInteractions(genes, params.stringConfidenceThreshold)

        val nodeSymbols = genes.map { it.symbol }
        val scores = analyzer.computeCentrality(nodeSymbols, edges, params.centralityMethod)

        // Select top hub gene by centrality score
        val sorted = scores.entries.sortedByDescending { it.value }
        val hubSymbol = sorted.firstOrNull()?.key ?: (nodeSymbols.firstOrNull() ?: "APOE")

        return HubResult(
            diseaseName = diseaseName,
            hubGeneSymbol = hubSymbol,
            umlsCui = cui,
            centralityScores = scores,
            graphSummary = mapOf("nodes" to nodeSymbols.size, "edges" to edges.size),
            candidateGenes = genes,
            ppiEdges = edges,
            usedFallback = usedPpiFallback,
            logMessage = "Constructed PPI graph with ${nodeSymbols.size} nodes and ${edges.size} interactions. Selected $hubSymbol via ${params.centralityMethod.displayName}."
        )
    }
}
