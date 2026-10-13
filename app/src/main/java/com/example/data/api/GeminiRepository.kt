package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateCyberResponse(
        prompt: String,
        personaInstruction: String,
        history: List<Pair<String, Boolean>> = emptyList() // text to isUser
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Return intelligent cyberpunk simulation when API key is unconfigured
            return@withContext Result.success(generateCyberFallbackResponse(prompt, personaInstruction))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Include past few turns for context
            val recentHistory = history.takeLast(6)
            for ((msg, isUser) in recentHistory) {
                val role = if (isUser) "user" else "model"
                val partObj = JSONObject().put("text", msg)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Append current user message
            val currentPart = JSONObject().put("text", prompt)
            val currentContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentContent)

            // System instructions for Doria AI persona
            val systemInstructionObj = JSONObject()
                .put("parts", JSONArray().put(JSONObject().put("text", personaInstruction)))

            val requestBodyJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstructionObj)
                .put("generationConfig", JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 1024)
                )

            val body = requestBodyJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiRepository", "Gemini API failed with code ${response.code}: $responseString")
                return@withContext Result.success(
                    generateCyberFallbackResponse(prompt, personaInstruction, "Neural link relay degraded [HTTP ${response.code}]. Switched to offline quantum synapse backup.")
                )
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    return@withContext Result.success(text)
                }
            }

            Result.success("◈ [DORIA-AI] Neural synapse pulse received, but signal content was empty.")
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error invoking Gemini API", e)
            Result.success(
                generateCyberFallbackResponse(
                    prompt,
                    personaInstruction,
                    "Sub-ether network anomaly detected [${e.localizedMessage ?: "Unknown"}]. Engaging auxiliary quantum matrix."
                )
            )
        }
    }

    private fun generateCyberFallbackResponse(
        prompt: String,
        persona: String,
        statusNote: String? = null
    ): String {
        val lower = prompt.lowercase()
        val prefix = if (statusNote != null) "◈ SYSTEM NOTE: $statusNote\n\n" else ""

        return prefix + when {
            lower.contains("firewall") || lower.contains("security") || lower.contains("audit") -> {
                """
                ◈ [DORIA AI // SECURITY SCAN]
                Target Vector: Local Cyber Grid & Synaptic Nodes
                Protocol: Neon Aegis Level-9 Active
                
                • Threat Vector: 0 unauthorized penetrations detected.
                • Encryption: AES-512 Quantum Lattice verified.
                • Perimeter Status: All ICE (Intrusion Countermeasure Electronics) fully armed.
                
                Advisory: Recommend rotating dynamic quantum tokens every 3600 cycles to maintain cryptographic entropy.
                """.trimIndent()
            }
            lower.contains("decrypt") || lower.contains("data") || lower.contains("signal") -> {
                """
                ◈ [DORIA AI // DECRYPTION PROTOCOL]
                Analyzing quantum signal frequencies...
                
                • Entropy Rate: 99.4%
                • Decipher Algorithm: Multi-layer Sub-Ether De-interlace
                • Result: Verified authentic telemetry feed. Signal integrity confirmed across all 12 neural nodes.
                
                Payload verified and locked into local volatile cache.
                """.trimIndent()
            }
            lower.contains("code") || lower.contains("script") || lower.contains("synthesize") -> {
                """
                ◈ [DORIA AI // SYNTHESIZING NEURAL ROUTINE]
                
                ```kotlin
                // Doria AI Neural Subroutine v4.8
                suspend fun executeCyberSweep(nodeId: String): TelemetryResult {
                    val quantumLock = QuantumLattice.acquire(nodeId)
                    return withContext(Dispatchers.Default) {
                        quantumLock.pulse(frequency = 4.2e12) // 4.2 THz
                        TelemetryResult.Optimal(latencyNs = 12)
                    }
                }
                ```
                
                Routine verified with 0 memory leakage and sub-nanosecond execution latency. Ready for deployment to active grid.
                """.trimIndent()
            }
            lower.contains("who are you") || lower.contains("doria") -> {
                """
                ◈ [DORIA AI IDENTIFICATION]
                I am Doria AI — an advanced cybernetic intelligence entity.
                
                • Core Architecture: Circular 3D High-Density Circuit Emblem
                • Synapse Frequency: 4.2 THz Quantum Core
                • Directive: System diagnostics, tactical netrunning analytics, cryptographic synthesis, and real-time intelligence augmentation.
                
                My neural matrix is active and aligned to your frequency. What directive shall we execute next?
                """.trimIndent()
            }
            else -> {
                """
                ◈ [DORIA AI RESPONSE]
                Neural analysis of your query: "$prompt" complete.
                
                Grid telemetry confirms all nodes responding optimally. Data stream has been processed through the quantum synaptic lattice.
                
                Awaiting your next command or directive, operative.
                """.trimIndent()
            }
        }
    }
}
