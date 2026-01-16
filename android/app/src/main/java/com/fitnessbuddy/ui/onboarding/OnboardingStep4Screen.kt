package com.fitnessbuddy.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Theme colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep4Screen(
    viewModel: OnboardingViewModel,
    onGeneratePlan: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ONBOARDING",
                color = AccentGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            Text(
                text = "STEP 4 OF 4",
                color = AccentGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(AccentGreen)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title
        Text(
            text = "TRAINING",
            color = TextWhite,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "AVAILABILITY",
            color = AccentGreen,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Which days do you prefer to train?",
            color = TextGray,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Weekly Schedule Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WEEKLY SCHEDULE",
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "SELECT DAYS",
                color = TextGray,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day Selection Grid - All 7 days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.entries.forEach { day ->
                DayCard(
                    day = day,
                    isSelected = viewModel.selectedTrainingDays.contains(day),
                    onClick = {
                        viewModel.selectedTrainingDays = if (viewModel.selectedTrainingDays.contains(day)) {
                            viewModel.selectedTrainingDays - day
                        } else {
                            viewModel.selectedTrainingDays + day
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Max Weekly Volume Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MAX WEEKLY VOLUME",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${viewModel.maxWeeklyHours}",
                        color = AccentGreen,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HRS",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Volume Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Minus Button
                    IconButton(
                        onClick = { 
                            if (viewModel.maxWeeklyHours > 2) {
                                viewModel.maxWeeklyHours -= 1
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = TextWhite
                        )
                    }
                    
                    // Slider
                    Slider(
                        value = viewModel.maxWeeklyHours.toFloat(),
                        onValueChange = { viewModel.maxWeeklyHours = it.toInt() },
                        valueRange = 2f..40f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = AccentGreen,
                            activeTrackColor = AccentGreen,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    
                    // Plus Button
                    IconButton(
                        onClick = { 
                            if (viewModel.maxWeeklyHours < 40) {
                                viewModel.maxWeeklyHours += 1
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = TextWhite
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Min/Max labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MIN 2H",
                        color = TextGray,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "MAX 40H",
                        color = TextGray,
                        fontSize = 10.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Health Connect info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "FitnessBuddy will sync your schedule",
                    color = TextWhite,
                    fontSize = 12.sp
                )
                Row {
                    Text(
                        text = "with ",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Health Connect",
                        color = AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " to auto-adjust for",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "recovery needs.",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Generate Plan Button
        Button(
            onClick = onGeneratePlan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            shape = RoundedCornerShape(28.dp),
            enabled = !viewModel.isGeneratingPlan
        ) {
            if (viewModel.isGeneratingPlan) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Generating...",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "GENERATE PLAN",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DayCard(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AccentGreen else Color.Transparent
    val textColor = if (isSelected) AccentGreen else TextGray
    
    Card(
        modifier = modifier
            .aspectRatio(0.7f)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.shortName,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

