package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.CyberCardBorder
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
fun CipherToolsScreen(
    viewModel: DoriaAIViewModel,
    modifier: Modifier = Modifier
) {
    val cipherInput by viewModel.cipherInput.collectAsState()
    val cipherOutput by viewModel.cipherOutput.collectAsState()
    val cipherMode by viewModel.cipherMode.collectAsState()
    val directives by viewModel.directives.collectAsState()

    val context = LocalContext.current

    val modes = listOf("HEX MATRIX", "BINARY STREAM", "QUANTUM +7")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberSlate)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Cipher Tools Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CIPHER & DIRECTIVES",
                    color = NeonPurple,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "QUANTUM CRYPTOGRAPHY & GRID PROTOCOLS",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            NeonBadge(
                label = "CRYPT-V2",
                color = NeonPurple
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cryptographic Encoder Card
        CyberCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonPurple.copy(alpha = 0.5f),
            backgroundColor = CyberSurfaceVariant
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "NEURAL CRYPTO ENCODER",
                    color = NeonPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    modes.forEachIndexed { index, modeName ->
                        val isSelected = cipherMode == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) NeonPurple.copy(alpha = 0.25f)
                                    else CyberSurface
                                )
                                .clickable { viewModel.setCipherMode(index) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = modeName,
                                color = if (isSelected) NeonPurple else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Text Input
                OutlinedTextField(
                    value = cipherInput,
                    onValueChange = { viewModel.setCipherInput(it) },
                    label = {
                        Text(
                            text = "Plaintext Payload",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cipher_input_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Encoded Output
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberCardBorder,
                    backgroundColor = CyberSurface
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ENCRYPTED STREAM",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Cipher Output", cipherOutput))
                                    Toast.makeText(context, "Encrypted payload copied", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Cipher",
                                    tint = NeonPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = cipherOutput.ifEmpty { "[NO DATA]" },
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cyber Directives
        Text(
            text = "◈ SUB-ETHER DIRECTIVES",
            color = CyberTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Text(
            text = "Toggle autonomous defense & network routines",
            color = CyberTextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        directives.forEach { directive ->
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                borderColor = if (directive.isArmed) directive.accentColor.copy(alpha = 0.5f) else CyberCardBorder,
                backgroundColor = CyberSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeonBadge(
                                label = directive.tag,
                                color = directive.accentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = directive.title,
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = directive.description,
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = directive.isArmed,
                        onCheckedChange = { viewModel.toggleDirective(directive.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = directive.accentColor,
                            checkedTrackColor = directive.accentColor.copy(alpha = 0.3f),
                            uncheckedThumbColor = CyberTextMuted,
                            uncheckedTrackColor = CyberSlate
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
