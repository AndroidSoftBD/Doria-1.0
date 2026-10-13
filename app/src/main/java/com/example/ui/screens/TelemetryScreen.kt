package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.CyberSlate
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.viewmodel.DoriaAIViewModel

@Composable
fun TelemetryScreen(
    viewModel: DoriaAIViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.telemetry.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberSlate)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Telemetry Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYSTEM TELEMETRY",
                    color = NeonMagenta,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "QUANTUM SENSOR GRID // REAL-TIME MONITOR",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            NeonBadge(
                label = if (telemetry.isDiagnosticRunning) "SCANNING..." else "NORMAL",
                color = if (telemetry.isDiagnosticRunning) NeonAmber else NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic Metrics Cards
        TelemetryMetricCard(
            title = "SYNAPTIC CPU LOAD",
            valueText = "${telemetry.cpuLoadPercent}%",
            progress = telemetry.cpuLoadPercent / 100f,
            accentColor = NeonCyan,
            statusLabel = if (telemetry.cpuLoadPercent > 80) "HIGH INTENSITY" else "NOMINAL"
        )

        Spacer(modifier = Modifier.height(10.dp))

        TelemetryMetricCard(
            title = "QUANTUM BUFFER ALLOCATION",
            valueText = "${telemetry.quantumBufferPercent}%",
            progress = telemetry.quantumBufferPercent / 100f,
            accentColor = NeonPurple,
            statusLabel = "ALLOCATED"
        )

        Spacer(modifier = Modifier.height(10.dp))

        TelemetryMetricCard(
            title = "ZERO-TRUST FIREWALL INTEGRITY",
            valueText = "${telemetry.firewallIntegrityPercent}%",
            progress = telemetry.firewallIntegrityPercent / 100f,
            accentColor = NeonGreen,
            statusLabel = "ARMED"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBox(
                modifier = Modifier.weight(1f),
                title = "CORE FREQ",
                value = "${telemetry.synapseFrequencyTHz} THz",
                accentColor = NeonCyan
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "CORE TEMP",
                value = "${telemetry.coreTemp}°C",
                accentColor = NeonAmber
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "PING LATENCY",
                value = "${telemetry.latencyMs} ms",
                accentColor = NeonMagenta
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trigger diagnostic sweep button
        CyberButton(
            text = if (telemetry.isDiagnosticRunning) "[ DIAGNOSTIC SWEEP IN PROGRESS ]" else "[ INITIATE QUANTUM SWEEP ]",
            onClick = { viewModel.runDiagnosticSweep() },
            accentColor = NeonMagenta,
            enabled = !telemetry.isDiagnosticRunning,
            modifier = Modifier.fillMaxWidth(),
            testTag = "quantum_sweep_button"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terminal Diagnostic Log
        Text(
            text = "◈ SUB-ETHER EXECUTION TRACE",
            color = CyberTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            backgroundColor = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                telemetry.diagnosticLog.takeLast(8).forEach { logLine ->
                    Text(
                        text = logLine,
                        color = when {
                            logLine.contains("complete") -> NeonGreen
                            logLine.contains("Initiating") -> NeonAmber
                            logLine.contains("Probing") -> NeonCyan
                            else -> CyberTextSecondary
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TelemetryMetricCard(
    title: String,
    valueText: String,
    progress: Float,
    accentColor: Color,
    statusLabel: String
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = 0.4f),
        backgroundColor = CyberSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = statusLabel,
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueText,
                    color = CyberTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = CyberSlate
            )
        }
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    CyberCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.4f),
        backgroundColor = CyberSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = CyberTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
