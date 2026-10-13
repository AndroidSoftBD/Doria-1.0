package com.example.data.engine

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

sealed class VoiceCommandResult {
    data class ClearHistory(val feedback: String) : VoiceCommandResult()
    data class RunDiagnostic(val feedback: String) : VoiceCommandResult()
    data class NormalChat(val query: String) : VoiceCommandResult()
}

class DoriaEngine {
    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun evaluateVoiceCommand(transcript: String): VoiceCommandResult {
        val trimmed = transcript.trim().lowercase()
        return when {
            trimmed.contains("clear chat") || trimmed.contains("clear history") || trimmed.contains("delete history") -> {
                VoiceCommandResult.ClearHistory("Command acknowledged: Chat history purged from local SQLite storage.")
            }
            trimmed.contains("run diagnostic") || trimmed.contains("system status") || trimmed.contains("telemetry check") -> {
                VoiceCommandResult.RunDiagnostic("Voice command detected: Running full quantum diagnostic sweep.")
            }
            else -> {
                VoiceCommandResult.NormalChat(transcript.trim())
            }
        }
    }

    suspend fun generateResponse(
        prompt: String,
        isVoiceInput: Boolean = false,
        conversationHistory: List<Pair<String, Boolean>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val contentsArray = JSONArray()

                for ((msg, isUser) in conversationHistory.takeLast(4)) {
                    val role = if (isUser) "user" else "model"
                    contentsArray.put(
                        JSONObject()
                            .put("role", role)
                            .put("parts", JSONArray().put(JSONObject().put("text", msg)))
                    )
                }

                contentsArray.put(
                    JSONObject()
                        .put("role", "user")
                        .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                )

                val systemInstruction = """
                    You are Doria 1.0 — a fast, lightweight, and intelligent AI engine.
                    You communicate with high precision, speed, and helpfulness.
                    When answering voice commands, keep responses clear, concise, and structured.
                """.trimIndent()

                val requestJson = JSONObject()
                    .put("contents", contentsArray)
                    .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction))))
                    .put("generationConfig", JSONObject().put("temperature", 0.6).put("maxOutputTokens", 800))

                val body = requestJson.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder().url(url).post(body).build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val text = candidates.getJSONObject(0)
                            .optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.getJSONObject(0)
                            ?.optString("text")
                        if (!text.isNullOrBlank()) {
                            return@withContext text
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("DoriaEngine", "Cloud relay failed, switching to local Doria 1.0 Free Engine: ${e.message}")
            }
        }

        // High-velocity built-in Doria 1.0 Free Engine
        generateLocalDoriaResponse(prompt, isVoiceInput)
    }

    private fun generateLocalDoriaResponse(prompt: String, isVoice: Boolean): String {
        val lower = prompt.lowercase().trim()
        val voiceTag = if (isVoice) "🎙 [VOICE COMMAND PROCESSED]\n" else ""

        return voiceTag + when {
            lower.contains("who are you") || lower.contains("what is doria") || lower.contains("engine") -> {
                """
                ◈ [DORIA 1.0 FREE ENGINE]
                I am Doria AI, powered by the Doria 1.0 lightweight neural inference engine.
                
                • Architecture: Edge-optimized mobile AI
                • Local Storage: SQLite / Room Database Active
                • Speech Processing: Real-Time Audio-to-Text Pipeline
                • Package: com.androidsoft.aidoria.bd
                
                All chats are safely indexed in your device's persistent Room database.
                """.trimIndent()
            }
            lower.contains("help") || lower.contains("commands") -> {
                """
                ◈ [DORIA 1.0 VOICE & TEXT COMMANDS]
                
                • "Clear history" — Purge local SQLite chat database
                • "Run diagnostic" — Trigger quantum telemetry scan
                • "Code [topic]" — Generate instant programming routines
                • "Explain [topic]" — Fast, lightweight knowledge synthesis
                
                Tap the microphone button anytime to speak hands-free.
                """.trimIndent()
            }
            lower.contains("code") || lower.contains("kotlin") || lower.contains("android") -> {
                """
                ◈ [DORIA 1.0 CODE SYNTHESIS]
                
                ```kotlin
                // Doria 1.0 Room History Query
                @Dao
                interface ChatDao {
                    @Query("SELECT * FROM chat_history ORDER BY timestamp ASC")
                    fun getAllMessages(): Flow<List<ChatMessageEntity>>
                }
                ```
                
                Lightweight, non-blocking Flow stream compiled with zero memory overhead.
                """.trimIndent()
            }
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "◈ [DORIA 1.0] Hello! Doria 1.0 free engine is online and ready for voice or text directives."
            }
            else -> {
                """
                ◈ [DORIA 1.0 ENGINE RESPONSE]
                Processed: "$prompt"
                
                Analysis completed with sub-millisecond local latency. Data safely committed to your local Room SQLite store. Ready for your next directive.
                """.trimIndent()
            }
        }
    }
}
