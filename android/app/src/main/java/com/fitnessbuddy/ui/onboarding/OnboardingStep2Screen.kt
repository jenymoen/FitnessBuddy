package com.fitnessbuddy.ui.onboarding

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Theme colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@Composable
fun OnboardingStep2Screen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    when (viewModel.step2SubStep) {
        1 -> MissionSelectionScreen(
            viewModel = viewModel,
            onBack = onBack,
            onConfirm = { 
                // Reset event type when mission changes
                viewModel.selectedEventType = ""
                viewModel.nextStep2SubStep() 
            }
        )
        2 -> EventTypeSelectionScreen(
            viewModel = viewModel,
            onBack = { viewModel.previousStep2SubStep() },
            onGeneratePlan = {
                viewModel.nextStep()
                onNext()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MissionSelectionScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "STEP 02/04",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        
        // Progress
        ProgressHeader(progressPercent = viewModel.getProgressPercent())
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = "CHOOSE YOUR",
            color = TextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "MISSION",
            color = AccentGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Your training plan will be mathematically\noptimized for your specific endurance\ndiscipline.",
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Mission Grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MissionCard(
                    mission = Mission.MARATHON_TRAINING,
                    isSelected = viewModel.selectedMission == Mission.MARATHON_TRAINING,
                    onClick = { viewModel.selectedMission = Mission.MARATHON_TRAINING },
                    modifier = Modifier.weight(1f)
                )
                MissionCard(
                    mission = Mission.CYCLOCROSS,
                    isSelected = viewModel.selectedMission == Mission.CYCLOCROSS,
                    onClick = { viewModel.selectedMission = Mission.CYCLOCROSS },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MissionCard(
                    mission = Mission.TRIATHLON,
                    isSelected = viewModel.selectedMission == Mission.TRIATHLON,
                    onClick = { viewModel.selectedMission = Mission.TRIATHLON },
                    modifier = Modifier.weight(1f)
                )
                MissionCard(
                    mission = Mission.TRAIL_RUNNING,
                    isSelected = viewModel.selectedMission == Mission.TRAIL_RUNNING,
                    onClick = { viewModel.selectedMission = Mission.TRAIL_RUNNING },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Health Connect info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {},
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentGreen,
                    checkmarkColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "FitnessBuddy will automatically sync your past\nperformance data from Health Connect to\npersonalize your power zones.",
                color = TextGray,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Button
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "CONFIRM GOAL",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "YOU CAN CHANGE THIS FOCUS AT ANY TIME",
            color = TextGray,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventTypeSelectionScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onGeneratePlan: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Get event types for selected mission
    val eventTypes = remember(viewModel.selectedMission) {
        getDistancesForMission(viewModel.selectedMission)
    }
    
    // Auto-select first event type if none selected
    LaunchedEffect(eventTypes) {
        if (viewModel.selectedEventType.isEmpty() && eventTypes.isNotEmpty()) {
            viewModel.selectedEventType = eventTypes.first().id
        }
    }
    
    // Dynamic title based on mission
    val (title1, title2, subtitle) = remember(viewModel.selectedMission) {
        when (viewModel.selectedMission) {
            Mission.TRIATHLON -> Triple(
                "CHOOSE",
                "EVENT TYPE",
                "Select your triathlon distance to calibrate\nyour endurance progression algorithms."
            )
            Mission.CYCLOCROSS -> Triple(
                "SELECT YOUR",
                "RACE FORMAT",
                "Choose the specific format for your\ntraining cycle."
            )
            else -> Triple(
                "SELECT YOUR",
                "DISTANCE",
                "Choose the specific distance for your\ntraining cycle to optimize your aerobic\nthresholds."
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "STEP 02/04",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        
        // Progress - show MISSION STATUS for triathlon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewModel.selectedMission == Mission.TRIATHLON) "MISSION STATUS" else "PROGRESSION",
                color = TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${viewModel.getProgressPercent()}%",
                color = AccentGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(viewModel.getProgressPercent() / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(AccentGreen)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title1,
            color = TextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = title2,
            color = AccentGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Event Type Options - 2x2 grid for Triathlon, list for others
        if (viewModel.selectedMission == Mission.TRIATHLON) {
            // 2x2 Grid for triathlon
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                eventTypes.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { eventType ->
                            EventTypeGridCard(
                                eventType = eventType,
                                isSelected = viewModel.selectedEventType == eventType.id,
                                onClick = { viewModel.selectedEventType = eventType.id },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty space if odd number
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // List for other missions
            eventTypes.forEach { eventType ->
                EventTypeListCard(
                    eventType = eventType,
                    isSelected = viewModel.selectedEventType == eventType.id,
                    onClick = { viewModel.selectedEventType = eventType.id }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Target Date
        Text(
            text = "TARGET DATE",
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val picker = DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            viewModel.targetDate = LocalDate.of(year, month + 1, day)
                        },
                        viewModel.targetDate.year,
                        viewModel.targetDate.monthValue - 1,
                        viewModel.targetDate.dayOfMonth
                    )
                    picker.show()
                },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = viewModel.targetDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select date",
                    tint = TextGray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Info text
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
            Text(
                text = "AI will calculate 24 weeks of progressive loading\nbased on your Health Connect history.",
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Generate Plan Button
        Button(
            onClick = onGeneratePlan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Next",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "BUILD YOUR PERFORMANCE ENGINE",
            color = TextGray,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProgressHeader(progressPercent: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PROGRESSION",
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$progressPercent%",
            color = AccentGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progressPercent / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(AccentGreen)
        )
    }
}

@Composable
private fun MissionCard(
    mission: Mission,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AccentGreen else Color.Transparent
    val iconBgColor = if (isSelected) AccentGreen else Color(0xFF2A2A2A)
    
    Card(
        modifier = modifier
            .height(140.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getMissionEmoji(mission),
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = mission.displayName,
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(AccentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventTypeGridCard(
    eventType: EventType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AccentGreen else Color.Transparent
    val iconBgColor = if (isSelected) AccentGreen else Color(0xFF2A2A2A)
    
    Card(
        modifier = modifier
            .height(140.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getEventTypeIcon(eventType.id),
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = eventType.displayName,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(AccentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventTypeListCard(
    eventType: EventType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AccentGreen else Color.Transparent
    val iconBgColor = if (isSelected) AccentGreen else Color(0xFF2A2A2A)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getEventTypeIcon(eventType.id),
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eventType.displayName,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = eventType.subtitle,
                    color = if (isSelected) AccentGreen else TextGray,
                    fontSize = 12.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) AccentGreen else TextGray, CircleShape)
                    .background(if (isSelected) AccentGreen else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun getMissionEmoji(mission: Mission): String {
    return when (mission) {
        Mission.MARATHON_TRAINING -> "🏃"
        Mission.CYCLOCROSS -> "🚴"
        Mission.TRIATHLON -> "🏊"
        Mission.TRAIL_RUNNING -> "🥾"
    }
}

private fun getEventTypeIcon(eventId: String): String {
    return when (eventId) {
        // Running
        "FULL_MARATHON" -> "🏅"
        "HALF_MARATHON" -> "🏃"
        "TEN_KM" -> "⚡"
        "FIVE_KM" -> "🔵"
        // Triathlon
        "IRONMAN" -> "🥇"
        "HALF_IRONMAN" -> "🥈"
        "OLYMPIC" -> "🔘"
        "SPRINT" -> "⚡"
        // Cycling
        "RACE_SERIES" -> "🏆"
        "GRAN_FONDO" -> "🚴"
        "CENTURY" -> "💯"
        "METRIC_CENTURY" -> "🔷"
        else -> "🎯"
    }
}
