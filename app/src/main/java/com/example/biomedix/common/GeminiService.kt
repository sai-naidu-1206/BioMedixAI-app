package com.example.biomedix.common

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiBiologyService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun chat(
        query: String,
        history: List<Pair<Boolean, String>> = emptyList(),
        context: String = ""
    ): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        try {
            val systemInstruction = """
                You are the BioMedix AI Core, a specialist in computational biology and genomics.
                Your goal is to assist researchers with disease analysis, gene identification, and CRISPR safety.
                
                Current Context: $context
                
                RULES:
                1. Always stay grounded in biological science.
                2. If the user asks something completely unrelated to biology, science, or the current research, politely redirect them back to the BioMedix console.
                3. Be concise and professional.
                4. Use data from GenBank, UniProt, and PDB when discussing specific genes.
            """.trimIndent()

            val contents = org.json.JSONArray()
            
            // Add history - Filter to ensure it starts with a 'user' message as required by Gemini API
            val validHistory = history.dropWhile { !it.first }

            for (msg in validHistory) {
                contents.put(JSONObject().apply {
                    put("role", if (msg.first) "user" else "model")
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply { put("text", msg.second) })
                    })
                })
            }

            // Add current query
            contents.put(JSONObject().apply {
                put("role", "user")
                put("parts", org.json.JSONArray().apply {
                    put(JSONObject().apply { put("text", query) })
                })
            } )

            val json = JSONObject().apply {
                put("contents", contents)
                put("system_instruction", JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    })
                })
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val responseJson = JSONObject(body)
                val text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                return@withContext text.trim()
            } else {
                println("Gemini API Error: ${response.code} - ${response.message}")
                println("Response Body: ${response.body?.string()}")
            }
        } catch (e: Exception) {
            println("Gemini API Exception: ${e.message}")
            e.printStackTrace()
        }
        null
    }

    suspend fun getIntelligentVerdict(
        diseaseName: String,
        hubGene: String,
        druggabilityScore: Float,
        crisprSafetyScore: Float
    ): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        try {
            val prompt = """
                You are a senior computational biologist assistant. 
                
                The following analysis was performed by aggregating data from multiple authoritative databases:
                - GenBank (NCBI) for sequence validation
                - Open Targets for drug-target associations
                - GWAS Catalog for genetic risk mapping
                - DisGeNET for disease-gene networks
                - UniProt for functional protein context
                - RCSB PDB for 3D structural geometric analysis
                
                Evaluate if the provided "Disease" name is a real biological disease.
                If it is nonsense, respond ONLY with "INVALID_INPUT".
                
                Disease: $diseaseName
                Target Hub Gene: $hubGene
                Pocket Druggability Score: $druggabilityScore
                CRISPR Genomic Safety Score: $crisprSafetyScore
                
                Provide a highly professional verdict (max 3 sentences) synthesizing data from these sources.
            """.trimIndent()

            val json = JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val responseJson = JSONObject(body)
                val text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text").trim()
                
                if (text.contains("INVALID_INPUT", ignoreCase = true)) return@withContext "INVALID_DATA_ERROR"
                
                return@withContext text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}
