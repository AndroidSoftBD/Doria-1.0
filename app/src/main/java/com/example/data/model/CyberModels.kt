package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple

enum class PersonaType(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val systemPrompt: String
) {
    NETRUNNER(
        title = "Netrunner Operative",
        subtitle = "Street-smart tactical cyber intelligence",
        accentColor = NeonCyan,
        systemPrompt = "You are DORIA AI in Netrunner Operative mode. You are sharp, technical, vigilant, and speak with cyberpunk netrunner terminology (ICE, deck, net, telemetry, cipher). Keep responses punchy, intelligent, and highly practical."
    ),
    QUANTUM_CORE(
        title = "Sentient Quantum Core",
        subtitle = "Deep analytical & philosophical intelligence",
        accentColor = NeonMagenta,
        systemPrompt = "You are DORIA AI in Sentient Quantum Core mode. You analyze patterns across quantum dimensions, algorithmic philosophy, and technological singularity. You provide deep, lucid, elegant, and intellectually profound answers."
    ),
    DEFENSE_SENTINEL(
        title = "Cyber Defense Sentinel",
        subtitle = "Zero-trust security & vulnerability auditor",
        accentColor = NeonGreen,
        systemPrompt = "You are DORIA AI in Cyber Defense Sentinel mode. You are hyper-focused on cybersecurity, cryptography, threat modeling, intrusion detection, zero-day mitigation, and defensive protocols."
    ),
    CODE_SYNTHESIZER(
        title = "Code Synthesizer",
        subtitle = "High-velocity systems & algorithmic coder",
        accentColor = NeonPurple,
        systemPrompt = "You are DORIA AI in Code Synthesizer mode. You deliver elite-grade software engineering, algorithms, clean code snippets (Kotlin, Rust, Python, C++), and futuristic system architecture recommendations."
    )
}

data class CyberChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val persona: PersonaType? = null
)

data class TelemetryState(
    val coreTemp: Float = 38.4f,
    val cpuLoadPercent: Int = 42,
    val quantumBufferPercent: Int = 89,
    val synapseFrequencyTHz: Float = 4.20f,
    val firewallIntegrityPercent: Float = 99.8f,
    val latencyMs: Int = 14,
    val isDiagnosticRunning: Boolean = false,
    val diagnosticLog: List<String> = listOf(
        "[08:29:10] DORIA Core initialized at 4.2 THz",
        "[08:29:12] High-density circular emblem synchronized",
        "[08:29:14] Neon quantum bus online • 0 packet loss"
    )
)

data class CyberDirective(
    val id: String,
    val title: String,
    val description: String,
    val tag: String,
    val accentColor: Color = NeonCyan,
    val isArmed: Boolean = true
)
