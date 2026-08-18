package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.EditingAdjustments
import com.example.data.model.MediaType
import com.example.ui.theme.AiBlue
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiPink
import com.example.ui.theme.AiPurple
import com.example.ui.theme.AiPurpleGlow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun EditorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeMediaType by viewModel.activeMediaType.collectAsState()
    val activeSourceBitmap by viewModel.activeSourceBitmap.collectAsState()
    val editedResultBitmap by viewModel.editedResultBitmap.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val customBgBitmap by viewModel.customBgBitmap.collectAsState()
    val adjustments by viewModel.adjustments.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val processingProgress by viewModel.processingProgress.collectAsState()
    val processingStatus by viewModel.processingStatus.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showOriginal by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Backgrounds, 1: Adjustments

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier.testTag("editor_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Text(
                text = if (activeMediaType == MediaType.PHOTO) "Photo BG Editor" else "Video BG Editor",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Original -> Preview Toggle Button
            Surface(
                onClick = { showOriginal = !showOriginal },
                shape = RoundedCornerShape(20.dp),
                color = if (showOriginal) AiPurple else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.testTag("original_preview_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showOriginal) "Original" else "Preview",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Preview Canvas Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            val displayBitmap = if (showOriginal) {
                activeSourceBitmap
            } else {
                editedResultBitmap ?: activeSourceBitmap
            }

            if (displayBitmap != null) {
                Image(
                    bitmap = displayBitmap.asImageBitmap(),
                    contentDescription = "Editor Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "Upload a photo or video to begin background replacement",
                    color = TextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }

            // Processing Loading Overlay
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { processingProgress },
                            color = AiPurpleGlow,
                            trackColor = DarkSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = processingStatus,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { processingProgress },
                            color = AiCyan,
                            trackColor = DarkSurfaceVariant,
                            modifier = Modifier.width(200.dp).clip(CircleShape)
                        )
                    }
                }
            }

            // Error Overlay
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.88f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Processing Notice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiPink
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Note: No free edit credit was deducted.",
                            fontSize = 11.sp,
                            color = AiCyan,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.dismissError() },
                            colors = ButtonDefaults.buttonColors(containerColor = AiPurple)
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Controls Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(16.dp)
        ) {
            // Tab Header (0: Backgrounds, 1: Adjustments)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = AiPurpleGlow,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AiPurpleGlow
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Backgrounds", color = if (selectedTab == 0) TextPrimary else TextMuted, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Adjustments", color = if (selectedTab == 1) TextPrimary else TextMuted, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Background Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Upload Button
                    Surface(
                        onClick = {
                            // Generate sample custom uploaded image background
                            val dummyCustom = viewModel.aiEngine.presetBackgrounds.last()
                            viewModel.selectPreset(dummyCustom)
                            viewModel.showToast("Custom background loaded")
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .height(72.dp)
                            .width(84.dp)
                            .testTag("upload_custom_bg_btn")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = AiCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Upload BG", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // AI Prompt Generator Button
                    Surface(
                        onClick = { viewModel.navigateTo(AppScreen.AI_GENERATOR) },
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AiPink.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(72.dp)
                            .width(84.dp)
                            .testTag("ai_prompt_bg_btn")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiPink, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("AI Generator", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Preset List
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(viewModel.aiEngine.presetBackgrounds) { preset ->
                            val isSelected = selectedPreset?.id == preset.id
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) AiPurple.copy(alpha = 0.3f) else DarkSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) AiPurpleGlow else DarkBorder
                                ),
                                modifier = Modifier
                                    .height(72.dp)
                                    .width(84.dp)
                                    .clickable { viewModel.selectPreset(preset) }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(
                                            text = preset.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            textAlign = TextAlign.Center
                                        )
                                        if (preset.isPremium) {
                                            Text("PRO", fontSize = 9.sp, color = AiPink, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Adjustments Sliders Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AdjustmentSliderItem("Blur", adjustments.blur, 0f, 100f) {
                        viewModel.updateAdjustments(adjustments.copy(blur = it))
                    }
                    AdjustmentSliderItem("Brightness", adjustments.brightness, -100f, 100f) {
                        viewModel.updateAdjustments(adjustments.copy(brightness = it))
                    }
                    AdjustmentSliderItem("Contrast", adjustments.contrast, -100f, 100f) {
                        viewModel.updateAdjustments(adjustments.copy(contrast = it))
                    }
                    AdjustmentSliderItem("Shadow", adjustments.shadow, 0f, 100f) {
                        viewModel.updateAdjustments(adjustments.copy(shadow = it))
                    }
                    AdjustmentSliderItem("BG Intensity", adjustments.backgroundIntensity, 0f, 100f) {
                        viewModel.updateAdjustments(adjustments.copy(backgroundIntensity = it))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Buttons: Preview, Apply, Export
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Preview / Apply AI Change
                Button(
                    onClick = { viewModel.processAiBackgroundChange() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AiPurple, AiCyan)
                            )
                        )
                        .testTag("apply_ai_bg_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply AI Change", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Export / Save Button
                Button(
                    onClick = {
                        if (editedResultBitmap != null) {
                            viewModel.showToast("Exported to Gallery successfully!")
                            viewModel.navigateTo(AppScreen.PROJECTS)
                        } else {
                            viewModel.processAiBackgroundChange()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AiPurpleGlow),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("export_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = AiPurpleGlow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjustmentSliderItem(
    label: String,
    value: Float,
    valueRangeStart: Float,
    valueRangeEnd: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(90.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRangeStart..valueRangeEnd,
            colors = SliderDefaults.colors(
                thumbColor = AiPurpleGlow,
                activeTrackColor = AiPurple,
                inactiveTrackColor = DarkBorder
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${value.toInt()}",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.width(32.dp)
        )
    }
}
