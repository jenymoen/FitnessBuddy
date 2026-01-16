package com.fitnessbuddy.ui.trainingplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.domain.model.TrainingDay
import com.fitnessbuddy.domain.model.TrainingWeek

// Theme colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)
private val HighlightBorder = Color(0xFFCDFF00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDetailScreen(
    weekNumber: Int,
    viewModel: TrainingPlanViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onViewWorkout: (String) -> Unit,
    onStartWorkout: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val plan = uiState.trainingPlan
    val week = plan?.weeks?.find { it.weekNumber == weekNumber }
    
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
                        text = "TRAINING PLAN",
                        color = AccentGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Week ${String.format("%02d", weekNumber)}",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "SYNCED",
                        color = TextGray,
                        fontSize = 10.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Health Connect",
                            color = AccentGreen,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = AccentGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )
        
        if (week == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Week not found", color = TextGray)
            }
            return@Column
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Week Theme Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BUILD AEROBIC",
                    color = TextWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CAPACITY",
                    color = AccentGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Weekly Goal Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WEEKLY GOAL",
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${week.totalDistanceKm} / ${week.totalDistanceKm * 1.2f} km",
                                color = TextWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${week.progressPercent}% Complete",
                                color = AccentGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📅", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Day ${week.completedSessions + 1} of 7",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "✓", color = AccentGreen, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "On Track",
                                color = AccentGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Daily workouts with timeline
            val days = if (week.days.isEmpty()) getMockDays() else week.days
            
            itemsIndexed(days) { index, day ->
                DayWorkoutItem(
                    day = day,
                    isLast = index == days.lastIndex,
                    onViewWorkout = { onViewWorkout(day.dayOfWeek) },
                    onStartWorkout = { onStartWorkout(day.dayOfWeek) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DayWorkoutItem(
    day: TrainingDay,
    isLast: Boolean,
    onViewWorkout: () -> Unit,
    onStartWorkout: () -> Unit
) {
    val isToday = day.isToday
    val isCompleted = day.isCompleted
    val isRestDay = day.isRestDay
    
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> AccentGreen
                            isToday -> AccentGreen
                            else -> CardBackground
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isCompleted -> AccentGreen
                            isToday -> AccentGreen
                            else -> TextGray
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Text(text = "✓", color = Color.Black, fontSize = 10.sp)
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (isToday && !isRestDay) 200.dp else 80.dp)
                        .background(if (isCompleted) AccentGreen else CardBackground)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Day content
        Column(modifier = Modifier.weight(1f)) {
            if (isToday && !isRestDay) {
                // Highlighted today card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, AccentGreen, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${day.dayOfWeek.uppercase()}: ${day.workoutType.uppercase()}",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = AccentGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "TODAY",
                                    color = AccentGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = day.mainSet.ifEmpty { day.title },
                            color = AccentGreen,
                            fontSize = 12.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (day.warmup.isNotEmpty()) {
                                Column {
                                    Text(text = "WARMUP", color = TextGray, fontSize = 10.sp)
                                    Text(
                                        text = day.warmup,
                                        color = TextWhite,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Column {
                                Text(text = "WORK", color = TextGray, fontSize = 10.sp)
                                Text(
                                    text = if (day.distanceKm > 0) "${day.distanceKm}km ${day.intensity}" else day.mainSet,
                                    color = TextWhite,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onStartWorkout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "START WORKOUT",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Regular day card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${day.dayOfWeek}: ${day.title}",
                                color = if (isCompleted) TextGray else TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                text = day.description.ifEmpty { 
                                    if (isRestDay) "Full recovery" else "${day.zone} • ${day.pace}" 
                                },
                                color = if (day.description.contains("Endurance")) AccentGreen else TextGray,
                                fontSize = 12.sp
                            )
                        }
                        if (isCompleted) {
                            Surface(
                                color = AccentGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "DONE",
                                    color = AccentGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else if (!isRestDay) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onViewWorkout() }
                            ) {
                                Text(
                                    text = "VIEW",
                                    color = AccentGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun getMockDays(): List<TrainingDay> {
    return listOf(
        TrainingDay(
            dayOfWeek = "Monday",
            workoutType = "Rest",
            title = "Rest Day",
            description = "Full recovery & Neuromuscular reset",
            isRestDay = true,
            isCompleted = true
        ),
        TrainingDay(
            dayOfWeek = "Tuesday",
            workoutType = "Easy Run",
            title = "8km Easy Run",
            description = "Zone 2 • 6:45 min/km",
            distanceKm = 8f,
            zone = "Zone 2",
            pace = "6:45 min/km",
            isCompleted = true
        ),
        TrainingDay(
            dayOfWeek = "Wednesday",
            workoutType = "Intervals",
            title = "Interval Session",
            description = "4x1000m @ 5k Target Pace",
            mainSet = "4x1000m @ 5k Target Pace",
            warmup = "15 min Easy",
            distanceKm = 4f,
            intensity = "High Intensity",
            isToday = true
        ),
        TrainingDay(
            dayOfWeek = "Thursday",
            workoutType = "Strength & Mobility",
            title = "Strength & Mobility",
            description = "Core Stability • Hip Mobility"
        ),
        TrainingDay(
            dayOfWeek = "Friday",
            workoutType = "Recovery",
            title = "5km Recovery",
            description = "Light jog • HR < 130bpm",
            distanceKm = 5f
        ),
        TrainingDay(
            dayOfWeek = "Saturday",
            workoutType = "Long Run",
            title = "15km Long Run",
            description = "Weekly Endurance Peak",
            distanceKm = 15f
        ),
        TrainingDay(
            dayOfWeek = "Sunday",
            workoutType = "Active Recovery",
            title = "Active Recovery",
            description = "Optional Yoga or 30min Walk",
            isRestDay = true
        )
    )
}
