package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonaType
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.HologramEmblemInspector
import com.example.ui.components.NeonBadge
import com.example.ui.theme.CyberSlate
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
fun HomeScreen(
    viewModel: DoriaAIViewModel,
    modifier: Modifier = Modifier
) {
    val tiltX by viewModel.tiltX.collectAsState()
    val tiltY by viewModel.tiltY.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()

    var showPulseAlert by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberSlate)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top System Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DORIA AI",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "NEURAL COGNITION SYSTEM v4.8",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            NeonBadge(
                label = "CORE ONLINE",
                color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center 3D Interactive Emblem Inspector
        Text(
            text = "◈ INTERACTIVE 3D EMBLEM INSPECTOR",
            color = CyberTextMuted,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Drag to rotate in 3D • Tap to ping neural core",
            color = CyberTextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(contentAlignment = Alignment.Center) {
            HologramEmblemInspector(
                tiltX = tiltX,
                tiltY = tiltY,
                onDragDelta = { dx, dy -> viewModel.updateTilt(dx, dy) },
                onTapPing = {
                    viewModel.triggerCorePulse()
                    showPulseAlert = true
                },
                size = 230.dp
            )
        }

        // Tilt readout and reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TILT [X: ${tiltX.toInt()}°  Y: ${tiltY.toInt()}°]",
                color = NeonCyan,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            IconButton(
                onClick = { viewModel.resetTilt() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset 3D Tilt",
                    tint = CyberTextSecondary
                )
            }
        }

        // Neural Core Spec Card
        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            borderColor = activePersona.accentColor.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CORE TELEMETRY",
                        color = activePersona.accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "NODE #77-ALPHA",
                        color = CyberTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecItem(title = "FREQUENCY", value = "${telemetry.synapseFrequencyTHz} THz", color = NeonCyan)
                    SpecItem(title = "CORE TEMP", value = "${telemetry.coreTemp}°C", color = NeonAmber)
                    SpecItem(title = "FIREWALL", value = "${telemetry.firewallIntegrityPercent}%", color = NeonGreen)
                    SpecItem(title = "LATENCY", value = "${telemetry.latencyMs} ms", color = NeonMagenta)
                }
            }
        }

        // Active Persona Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "NEURAL PERSONA MATRIX",
                color = CyberTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersonaType.values().forEach { persona ->
                    val isSelected = persona == activePersona
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) persona.accentColor.copy(alpha = 0.2f)
                                else CyberSurfaceVariant
                            )
                            .clickable { viewModel.setPersona(persona) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(persona.accentColor)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = persona.title.split(" ").first(),
                                color = if (isSelected) persona.accentColor else CyberTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Navigation Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavTile(
                modifier = Modifier.weight(1f),
                title = "NEURAL CHAT",
                subtitle = "Engage Doria AI",
                icon = Icons.AutoMirrored.Filled.Chat,
                accentColor = NeonCyan,
                onClick = { viewModel.selectTab(1) }
            )
            QuickNavTile(
                modifier = Modifier.weight(1f),
                title = "TELEMETRY",
                subtitle = "System diagnostics",
                icon = Icons.Default.Speed,
                accentColor = NeonMagenta,
                onClick = { viewModel.selectTab(2) }
            )
            QuickNavTile(
                modifier = Modifier.weight(1f),
                title = "CIPHER TOOLS",
                subtitle = "Crypto & directives",
                icon = Icons.Default.Code,
                accentColor = NeonPurple,
                onClick = { viewModel.selectTab(3) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SpecItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = CyberTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun QuickNavTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = modifier.clickable(onClick = onClick),
        borderColor = accentColor.copy(alpha = 0.4f),
        backgroundColor = CyberSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = CyberTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = CyberTextMuted,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
