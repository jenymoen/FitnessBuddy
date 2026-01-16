package com.fitnessbuddy.ui.trainingplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.domain.model.TrainingDay

// Theme colors (matching WeekDetailScreen)
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    weekNumber: Int,
    dayOfWeek: String,
    viewModel: TrainingPlanViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onStartWorkout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val plan = uiState.trainingPlan
    val week = plan?.weeks?.find { it.weekNumber == weekNumber }
    val day = week?.days?.find { it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) }

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
                        text = "WEEK ${String.format("%02d", weekNumber)}",
                        color = AccentGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = day?.dayOfWeek?.uppercase() ?: dayOfWeek.uppercase(),
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        if (day == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Workout not found", color = TextGray)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Workout Title
            Text(
                text = day.workoutType.uppercase(),
                color = AccentGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = day.title,
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Overview Card
            WorkoutOverviewCard(day)

            Spacer(modifier = Modifier.height(16.dp))

            // Workout Phases
            if (day.warmup.isNotEmpty() || day.mainSet.isNotEmpty() || day.cooldown.isNotEmpty()) {
                WorkoutPhasesCard(day)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Target Metrics
            if (day.zone.isNotEmpty() || day.pace.isNotEmpty() || day.intensity.isNotEmpty()) {
                TargetMetricsCard(day)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Description/Notes
            if (day.description.isNotEmpty()) {
                NotesCard(day.description)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Workout Button
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(28.dp)
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WorkoutOverviewCard(day: TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (day.durationMinutes > 0) {
                MetricItem(
                    label = "DURATION",
                    value = "${day.durationMinutes} min"
                )
            }
            if (day.distanceKm > 0) {
                MetricItem(
                    label = "DISTANCE",
                    value = "${day.distanceKm} km"
                )
            }
            if (day.intensity.isNotEmpty()) {
                MetricItem(
                    label = "INTENSITY",
                    value = day.intensity
                )
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = TextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WorkoutPhasesCard(day: TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "WORKOUT PHASES",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (day.warmup.isNotEmpty()) {
                PhaseItem(
                    phase = "WARMUP",
                    description = day.warmup,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (day.mainSet.isNotEmpty()) {
                PhaseItem(
                    phase = "MAIN SET",
                    description = day.mainSet,
                    color = AccentGreen
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (day.cooldown.isNotEmpty()) {
                PhaseItem(
                    phase = "COOLDOWN",
                    description = day.cooldown,
                    color = Color(0xFF03A9F4)
                )
            }
        }
    }
}

@Composable
private fun PhaseItem(phase: String, description: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = phase,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TextWhite,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TargetMetricsCard(day: TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TARGET METRICS",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (day.zone.isNotEmpty()) {
                    Column {
                        Text(text = "ZONE", color = TextGray, fontSize = 10.sp)
                        Text(
                            text = day.zone,
                            color = AccentGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (day.pace.isNotEmpty()) {
                    Column {
                        Text(text = "TARGET PACE", color = TextGray, fontSize = 10.sp)
                        Text(
                            text = day.pace,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "NOTES",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = TextWhite,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
