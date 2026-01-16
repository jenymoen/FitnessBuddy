package com.fitnessbuddy.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// Theme colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)
private val TodayCardGreen = Color(0xFF1F2F00)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToPlan: () -> Unit,
    onStartSession: () -> Unit,
    onNavigateToInsights: () -> Unit = {},
    onNavigateToRace: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavigationBar(
                onDashClick = { /* Already on dash */ },
                onInsightsClick = onNavigateToInsights,
                onAddClick = onStartSession,
                onRaceClick = onNavigateToPlan,
                onYouClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            ProfileHeader(
                userName = uiState.userName,
                planType = uiState.planType,
                isSynced = uiState.isSynced
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Countdown Card
            CountdownCard(
                daysToRace = uiState.daysToRace,
                eventName = uiState.trainingPlan?.eventType ?: "Race Day",
                currentWeek = uiState.currentWeek,
                totalWeeks = uiState.totalWeeks,
                planProgress = uiState.planProgress
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Today's Protocol Card
            uiState.todayWorkout?.let { workout ->
                TodayProtocolCard(
                    workoutType = workout.workoutType,
                    workoutTitle = workout.title,
                    duration = "${workout.durationMinutes} Minutes",
                    intensity = workout.intensity.ifEmpty { "Low Intensity" },
                    onStartSession = onStartSession
                )
            } ?: run {
                // Show rest day or placeholder
                TodayProtocolCard(
                    workoutType = "REST DAY",
                    workoutTitle = "Recovery",
                    duration = "Full Rest",
                    intensity = "Active Recovery",
                    onStartSession = {}
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Weekly Mileage Progress
            WeeklyMileageCard(
                currentKm = uiState.weeklyDistanceKm,
                goalKm = uiState.weeklyGoalKm,
                percentChange = "+12%"
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VO2MaxCard(
                    vo2Max = uiState.vo2Max.takeIf { it > 0 } ?: 54.2f,
                    rating = "Elite",
                    modifier = Modifier.weight(1f)
                )
                RecoveryCard(
                    recoveryPercent = uiState.recoveryPercent,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // AI Insight Card
            AIInsightCard(insight = uiState.aiInsight)
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    planType: String,
    isSynced: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A3728)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFFE8B88A),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = planType,
                    color = AccentGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Text(
                    text = userName,
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Sync indicator
        Surface(
            color = CardBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSynced) AccentGreen else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SYNCED",
                    color = if (isSynced) AccentGreen else TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CountdownCard(
    daysToRace: Int,
    eventName: String,
    currentWeek: Int,
    totalWeeks: Int,
    planProgress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COUNTDOWN",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$daysToRace",
                        color = TextWhite,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Days",
                        color = TextGray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }
                Text(
                    text = "to $eventName",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "WEEK $currentWeek / $totalWeeks",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            // Progress Ring
            Box(contentAlignment = Alignment.Center) {
                CircularProgressRing(
                    progress = planProgress / 100f,
                    modifier = Modifier.size(100.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${planProgress.toInt()}%",
                        color = AccentGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PLAN DONE",
                        color = TextGray,
                        fontSize = 8.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularProgressRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        
        // Background arc
        drawArc(
            color = Color(0xFF2A2A2A),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc
        drawArc(
            color = AccentGreen,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun TodayProtocolCard(
    workoutType: String,
    workoutTitle: String,
    duration: String,
    intensity: String,
    onStartSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TodayCardGreen),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S PROTOCOL",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏃", fontSize = 20.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = workoutTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$duration • $intensity",
                color = TextGray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onStartSession,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "START SESSION",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A2A))
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyMileageCard(
    currentKm: Float,
    goalKm: Float,
    percentChange: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "WEEKLY MILEAGE PROGRESS",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Text(
                text = "$percentChange vs LW",
                color = AccentGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${String.format("%.1f", currentKm)} / ${goalKm.toInt()} km",
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((currentKm / goalKm).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AccentGreen.copy(alpha = 0.7f), AccentGreen)
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Day indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, day ->
                val isActive = index == 5 // Saturday active
                Text(
                    text = day,
                    color = if (isActive) AccentGreen else TextGray,
                    fontSize = 12.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun VO2MaxCard(
    vo2Max: Float,
    rating: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "📈", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CURRENT VO2 MAX",
                color = TextGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.1f", vo2Max),
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = rating,
                    color = AccentGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TOP 5% FOR AGE",
                color = TextGray,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun RecoveryCard(
    recoveryPercent: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "💤", fontSize = 24.sp)
                Surface(
                    color = AccentGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "OPTIMAL",
                        color = AccentGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "RECOVERY STATUS",
                color = TextGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$recoveryPercent%",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "HRV: 72ms (STABLE)",
                color = TextGray,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AIInsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✨", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ENDURANCE AI INSIGHT",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight,
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onDashClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onAddClick: () -> Unit,
    onRaceClick: () -> Unit,
    onYouClick: () -> Unit
) {
    Surface(
        color = CardBackground,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Add padding for system navigation bar
                .padding(vertical = 12.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dash
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDashClick() }
            ) {
                Text(text = "⊞", fontSize = 20.sp, color = AccentGreen)
                Text(text = "DASH", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
            
            // Insights
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onInsightsClick() }
            ) {
                Text(text = "📊", fontSize = 20.sp)
                Text(text = "INSIGHTS", color = TextGray, fontSize = 10.sp)
            }
            
            // Add Button (Center)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AccentGreen)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Plan (Training Plan)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onRaceClick() }
            ) {
                Text(text = "📋", fontSize = 20.sp)
                Text(text = "PLAN", color = TextGray, fontSize = 10.sp)
            }
            
            // You
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onYouClick() }
            ) {
                Text(text = "👤", fontSize = 20.sp)
                Text(text = "YOU", color = TextGray, fontSize = 10.sp)
            }
        }
    }
}
