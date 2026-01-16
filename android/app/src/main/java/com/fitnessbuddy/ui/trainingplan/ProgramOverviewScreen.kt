package com.fitnessbuddy.ui.trainingplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.domain.model.TrainingWeek

// Theme colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramOverviewScreen(
    viewModel: TrainingPlanViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onWeekClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val plan = uiState.trainingPlan
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = plan?.eventType ?: "Training Plan",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${plan?.daysToRace ?: 0} DAYS TO RACE",
                        color = AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Calendar */ }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = TextWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )
        
        // Loading state
        if (uiState.isGenerating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AccentGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Generating your personalized plan...",
                        color = TextWhite
                    )
                }
            }
            return@Column
        }
        
        // Error state
        uiState.error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
            return@Column
        }
        
        if (plan == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No training plan available", color = TextGray)
            }
            return@Column
        }
        
        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Program Overview",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Week ${plan.currentWeek} of ${plan.totalWeeks}",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔄", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Synced with Health Connect 2m ago",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Week Cards
            items(plan.weeks) { week ->
                WeekCard(
                    week = week,
                    onClick = { onWeekClick(week.weekNumber) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun WeekCard(
    week: TrainingWeek,
    onClick: () -> Unit
) {
    val isCurrentWeek = week.isCurrentWeek
    val isLocked = week.isLocked
    
    val cardBackground = if (isCurrentWeek) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1F2F00),
                CardBackground
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(CardBackground, CardBackground)
        )
    }
    
    val borderColor = if (isCurrentWeek) AccentGreen else Color.Transparent
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isCurrentWeek) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isLocked) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(cardBackground)
                .padding(16.dp)
        ) {
            Column {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isCurrentWeek) {
                            Text(text = "⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                        } else if (isLocked) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Text(text = "✓", fontSize = 14.sp, color = AccentGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "WEEK ${week.weekNumber}: ${week.phase}",
                            color = if (isCurrentWeek) AccentGreen else TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (isCurrentWeek) {
                        Surface(
                            color = AccentGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "WEEK ${String.format("%02d", week.weekNumber)}",
                                color = AccentGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Theme
                Text(
                    text = week.theme,
                    color = if (isLocked) TextGray else TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column {
                        Text(
                            text = "🏃 ${week.totalDistanceKm} km",
                            color = if (isLocked) TextGray else AccentGreen,
                            fontSize = 14.sp
                        )
                    }
                    Column {
                        Text(
                            text = "⚡ ${week.totalSessions} Sessions",
                            color = if (isLocked) TextGray else TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Current week extras
                if (isCurrentWeek) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "PROGRESS", color = TextGray, fontSize = 11.sp)
                        Text(
                            text = "${week.progressPercent}% COMPLETE",
                            color = AccentGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(week.progressPercent / 100f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(AccentGreen)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Continue Training Button
                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "CONTINUE TRAINING",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}
