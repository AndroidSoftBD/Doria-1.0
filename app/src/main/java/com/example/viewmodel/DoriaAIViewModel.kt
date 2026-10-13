package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.DoriaEngine
import com.example.data.engine.VoiceCommandResult
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatHistoryRepository
import com.example.data.local.DoriaDatabase
import com.example.data.model.CyberDirective
import com.example.data.model.PersonaType
import com.example.data.model.TelemetryState
import com.example.speech.SpeechRecognitionHelper
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DoriaAIViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DoriaDatabase.getDatabase(application)
    private val repository = ChatHistoryRepository(database.chatDao())
    private val engine = DoriaEngine()
    val speechHelper = SpeechRecognitionHelper(application)

    // Observe Chat History directly from local SQLite / Room
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _activePersona = MutableStateFlow(PersonaType.NETRUNNER)
    val activePersona: StateFlow<PersonaType> = _activePersona.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _telemetry = MutableStateFlow(TelemetryState())
    val telemetry: StateFlow<TelemetryState> = _telemetry.asStateFlow()

    // 3D Hologram Inspection tilt state
    private val _tiltX = MutableStateFlow(0f)
    val tiltX: StateFlow<Float> = _tiltX.asStateFlow()

    private val _tiltY = MutableStateFlow(0f)
    val tiltY: StateFlow<Float> = _tiltY.asStateFlow()

    // Cipher Tool state
    private val _cipherInput = MutableStateFlow("DORIA 1.0 ENGINE ONLINE")
    val cipherInput: StateFlow<String> = _cipherInput.asStateFlow()

    private val _cipherMode = MutableStateFlow(0)
    val cipherMode: StateFlow<Int> = _cipherMode.asStateFlow()

    private val _cipherOutput = MutableStateFlow("")
    val cipherOutput: StateFlow<String> = _cipherOutput.asStateFlow()

    // Cyber Directives
    private val _directives = MutableStateFlow(
        listOf(
            CyberDirective("dir_1", "Neon Bastion ICE", "Quantum intrusion countermeasure defending neural nodes", "FIREWALL", NeonCyan, true),
            CyberDirective("dir_2", "Sub-Ether Overclock", "Overclocks quantum clock to 4.8 THz for zero-latency burst", "PERFORMANCE", NeonMagenta, false),
            CyberDirective("dir_3", "Ghost Grid Shrouding", "Anonymizes MAC and network fingerprint in cyber streams", "STEALTH", NeonPurple, true),
            CyberDirective("dir_4", "Synaptic Memory Flush", "Purges transient neural cache buffers every 60 seconds", "CACHE", NeonGreen, true),
            CyberDirective("dir_5", "Deep Zero-Day Sentry", "Continuous heuristic scanner for unknown vector probes", "SECURITY", NeonAmber, false)
        )
    )
    val directives: StateFlow<List<CyberDirective>> = _directives.asStateFlow()

    init {
        updateCipher()
        seedInitialWelcomeIfEmpty()
    }

    private fun seedInitialWelcomeIfEmpty() {
        viewModelScope.launch {
            delay(200)
            if (repository.getMessageCount() == 0) {
                repository.insertMessage(
                    text = "◈ [DORIA 1.0 ENGINE ONLINE]\nPackage: com.androidsoft.aidoria.bd\nLocal Room SQLite database initialized. Voice-to-Text ready. How may I assist you, Operative?",
                    isUser = false,
                    engineModel = "Doria 1.0",
                    isVoiceCommand = false
                )
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setPersona(persona: PersonaType) {
        _activePersona.value = persona
        viewModelScope.launch {
            repository.insertMessage(
                text = "◈ [PERSONA SWITCH] Doria 1.0 configured to: ${persona.title}. Directive: \"${persona.subtitle}\".",
                isUser = false,
                engineModel = "Doria 1.0"
            )
        }
    }

    fun triggerCorePulse() {
        val current = _telemetry.value
        _telemetry.value = current.copy(
            cpuLoadPercent = (current.cpuLoadPercent + 5).coerceAtMost(98),
            latencyMs = (current.latencyMs - 2).coerceAtLeast(4)
        )
    }

    fun updateTilt(deltaX: Float, deltaY: Float) {
        _tiltX.value = (_tiltX.value + deltaX).coerceIn(-35f, 35f)
        _tiltY.value = (_tiltY.value + deltaY).coerceIn(-35f, 35f)
    }

    fun resetTilt() {
        _tiltX.value = 0f
        _tiltY.value = 0f
    }

    fun sendMessage(text: String, isVoice: Boolean = false) {
        if (text.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            val userText = text.trim()
            // Check if voice command
            if (isVoice) {
                when (val command = engine.evaluateVoiceCommand(userText)) {
                    is VoiceCommandResult.ClearHistory -> {
                        repository.clearHistory()
                        repository.insertMessage(
                            text = command.feedback,
                            isUser = false,
                            engineModel = "Doria 1.0",
                            isVoiceCommand = true
                        )
                        return@launch
                    }
                    is VoiceCommandResult.RunDiagnostic -> {
                        repository.insertMessage(
                            text = userText,
                            isUser = true,
                            engineModel = "Doria 1.0",
                            isVoiceCommand = true
                        )
                        runDiagnosticSweep()
                        repository.insertMessage(
                            text = command.feedback,
                            isUser = false,
                            engineModel = "Doria 1.0",
                            isVoiceCommand = true
                        )
                        return@launch
                    }
                    is VoiceCommandResult.NormalChat -> {
                        // proceed to normal chat
                    }
                }
            }

            // Insert user message into SQLite / Room
            repository.insertMessage(
                text = userText,
                isUser = true,
                engineModel = "Doria 1.0",
                isVoiceCommand = isVoice
            )

            _isGenerating.value = true

            val history = chatMessages.value.map { it.text to it.isUser }
            val responseText = engine.generateResponse(
                prompt = userText,
                isVoiceInput = isVoice,
                conversationHistory = history
            )

            // Insert Doria 1.0 response into SQLite / Room
            repository.insertMessage(
                text = responseText,
                isUser = false,
                engineModel = "Doria 1.0",
                isVoiceCommand = isVoice
            )

            _isGenerating.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
            repository.insertMessage(
                text = "◈ [ROOM SQLITE CLEARED] Chat history purged. Doria 1.0 engine ready for new directives.",
                isUser = false,
                engineModel = "Doria 1.0"
            )
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    fun runDiagnosticSweep() {
        if (_telemetry.value.isDiagnosticRunning) return

        viewModelScope.launch {
            _telemetry.value = _telemetry.value.copy(
                isDiagnosticRunning = true,
                diagnosticLog = _telemetry.value.diagnosticLog + "[SYS] Initiating comprehensive Doria 1.0 sweep..."
            )

            delay(500)
            _telemetry.value = _telemetry.value.copy(
                cpuLoadPercent = 74,
                diagnosticLog = _telemetry.value.diagnosticLog + "[ROOM] SQLite database schema verified: Zero integrity faults"
            )

            delay(600)
            _telemetry.value = _telemetry.value.copy(
                firewallIntegrityPercent = 100.0f,
                diagnosticLog = _telemetry.value.diagnosticLog + "[VOICE] Audio recognition pipeline: Operational"
            )

            delay(500)
            _telemetry.value = _telemetry.value.copy(
                cpuLoadPercent = 36,
                latencyMs = 6,
                isDiagnosticRunning = false,
                diagnosticLog = _telemetry.value.diagnosticLog + "[SYS] Doria 1.0 status: 100% Optimal Synchronization"
            )
        }
    }

    fun setCipherInput(input: String) {
        _cipherInput.value = input
        updateCipher()
    }

    fun setCipherMode(mode: Int) {
        _cipherMode.value = mode
        updateCipher()
    }

    private fun updateCipher() {
        val input = _cipherInput.value
        val encoded = when (_cipherMode.value) {
            0 -> input.toByteArray().joinToString(" ") { String.format("%02X", it) }
            1 -> input.toByteArray().joinToString(" ") {
                String.format("%8s", Integer.toBinaryString(it.toInt() and 0xFF)).replace(' ', '0')
            }
            else -> input.map { char ->
                if (char.isLetter()) {
                    val base = if (char.isUpperCase()) 'A' else 'a'
                    val shifted = (char - base + 7) % 26
                    (base + shifted)
                } else char
            }.joinToString("")
        }
        _cipherOutput.value = encoded
    }

    fun toggleDirective(id: String) {
        _directives.value = _directives.value.map {
            if (it.id == id) it.copy(isArmed = !it.isArmed) else it
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.stopListening()
    }
}
