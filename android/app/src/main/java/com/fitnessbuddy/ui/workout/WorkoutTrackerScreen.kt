package com.fitnessbuddy.ui.workout

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.fitnessbuddy.data.sensor.HeartRateSensorState
import com.fitnessbuddy.data.healthconnect.ExerciseSessionInfo
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Theme colors (consistent across the app)
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTrackerScreen(
    weekNumber: Int,
    dayOfWeek: String,
    viewModel: WorkoutTrackerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Location permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                               permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            // Permission granted, prepare the workout
            viewModel.prepareWorkout()
        }
    }
    
    LaunchedEffect(weekNumber, dayOfWeek) {
        viewModel.loadWorkout(weekNumber, dayOfWeek)
    }


    val trainingDay = viewModel.trainingDay
    val workoutState = viewModel.workoutState
    val workoutMode = viewModel.workoutMode
    val feedbackState = viewModel.feedbackState
    val feedback = viewModel.feedback
    val feedbackError = viewModel.feedbackError
    val currentHeartRate = viewModel.currentHeartRate
    val heartRateSensorState = viewModel.heartRateSensorState
    val currentSpeedKmh = viewModel.getSpeedKmh()
    val hasGpsSignal = viewModel.hasGpsSignal
    
    // Health Connect import state
    val showImportDialog = viewModel.showImportDialog
    val healthConnectSessions = viewModel.healthConnectSessions
    val isLoadingHealthConnect = viewModel.isLoadingHealthConnect
    
    // Show import dialog
    if (showImportDialog) {
        ImportHealthConnectDialog(
            sessions = healthConnectSessions,
            isLoading = isLoadingHealthConnect,
            onSessionSelected = { viewModel.importSession(it) },
            onDismiss = { viewModel.closeImportDialog() }
        )
    }

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
                        text = "WORKOUT TRACKER",
                        color = AccentGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = trainingDay?.title ?: dayOfWeek,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentGreen)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Mode Selection (only before starting)
            if (workoutState == WorkoutState.NOT_STARTED) {
                ModeSelectionCard(
                    selectedMode = workoutMode,
                    onModeSelected = { viewModel.setMode(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Import from Health Connect button
                OutlinedButton(
                    onClick = { viewModel.openImportDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IMPORT FROM HEALTH CONNECT",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Timer Display
            TimerCard(
                time = viewModel.formatTime(),
                workoutState = workoutState
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Card (shown for all modes once workout starts)
            if (workoutState != WorkoutState.NOT_STARTED) {
                LiveStatsCard(
                    distanceKm = viewModel.distanceMeters / 1000f,
                    pace = viewModel.getPace(),
                    speedKmh = currentSpeedKmh,
                    heartRate = currentHeartRate,
                    heartRateSensorState = heartRateSensorState,
                    isOutdoor = workoutMode == WorkoutMode.OUTDOOR
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Map View (outdoor only)
            if (workoutMode == WorkoutMode.OUTDOOR && workoutState != WorkoutState.NOT_STARTED) {
                RouteMapCard(
                    routePoints = viewModel.routePoints.map { 
                        LatLng(it.latitude, it.longitude) 
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Workout Info
            trainingDay?.let { day ->
                WorkoutInfoCard(day)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Feedback Card (shown when workout is completed)
            if (workoutState == WorkoutState.COMPLETED) {
                FeedbackCard(
                    feedbackState = feedbackState,
                    feedback = feedback,
                    feedbackError = feedbackError,
                    onDismiss = onNavigateBack
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Control Buttons
            WorkoutControls(
                workoutState = workoutState,
                workoutMode = workoutMode,
                hasGpsSignal = hasGpsSignal,
                onPrepare = { 
                    if (hasLocationPermission) {
                        viewModel.prepareWorkout() 
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onStart = { viewModel.startWorkout() },
                onPause = { viewModel.pauseWorkout() },
                onResume = { viewModel.resumeWorkout() },
                onComplete = { viewModel.completeWorkout() },
                onDismiss = onNavigateBack
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ModeSelectionCard(
    selectedMode: WorkoutMode,
    onModeSelected: (WorkoutMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "WORKOUT MODE",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeButton(
                    label = "INDOOR",
                    icon = "🏠",
                    isSelected = selectedMode == WorkoutMode.INDOOR,
                    onClick = { onModeSelected(WorkoutMode.INDOOR) },
                    modifier = Modifier.weight(1f)
                )
                ModeButton(
                    label = "OUTDOOR",
                    icon = "🏃",
                    isSelected = selectedMode == WorkoutMode.OUTDOOR,
                    onClick = { onModeSelected(WorkoutMode.OUTDOOR) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (selectedMode == WorkoutMode.OUTDOOR) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GPS tracking enabled",
                        color = AccentGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentGreen.copy(alpha = 0.2f) else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isSelected) AccentGreen else TextGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = if (isSelected) AccentGreen else TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimerCard(
    time: String,
    workoutState: WorkoutState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ELAPSED TIME",
                color = TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = time,
                color = TextWhite,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            if (workoutState == WorkoutState.PAUSED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "PAUSED",
                        color = Color(0xFFFF9800),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveStatsCard(
    distanceKm: Float,
    pace: String,
    speedKmh: Float,
    heartRate: Int?,
    heartRateSensorState: HeartRateSensorState,
    isOutdoor: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Heart Rate Row (always shown)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Heart icon with pulse animation if connected
                Text(
                    text = "❤️",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "HEART RATE",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = heartRate?.toString() ?: "--",
                            color = if (heartRate != null) Color(0xFFFF5252) else TextGray,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "bpm",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                // Sensor status indicator
                Surface(
                    color = when (heartRateSensorState) {
                        HeartRateSensorState.CONNECTED -> AccentGreen.copy(alpha = 0.2f)
                        HeartRateSensorState.CONNECTING -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        HeartRateSensorState.SCANNING -> Color(0xFF2196F3).copy(alpha = 0.2f)
                        HeartRateSensorState.DISCONNECTED -> TextGray.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (heartRateSensorState) {
                            HeartRateSensorState.CONNECTED -> "CONNECTED"
                            HeartRateSensorState.CONNECTING -> "CONNECTING"
                            HeartRateSensorState.SCANNING -> "SCANNING"
                            HeartRateSensorState.DISCONNECTED -> "NO SENSOR"
                        },
                        color = when (heartRateSensorState) {
                            HeartRateSensorState.CONNECTED -> AccentGreen
                            HeartRateSensorState.CONNECTING -> Color(0xFFFF9800)
                            HeartRateSensorState.SCANNING -> Color(0xFF2196F3)
                            HeartRateSensorState.DISCONNECTED -> TextGray
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Only show outdoor stats if in outdoor mode
            if (isOutdoor) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(TextGray.copy(alpha = 0.2f))
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Speed, Distance, Pace row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "SPEED",
                        value = String.format("%.1f km/h", speedKmh)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(TextGray.copy(alpha = 0.3f))
                    )
                    StatItem(
                        label = "DISTANCE",
                        value = String.format("%.2f km", distanceKm)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(TextGray.copy(alpha = 0.3f))
                    )
                    StatItem(
                        label = "PACE",
                        value = "$pace /km"
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
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
            color = AccentGreen,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RouteMapCard(routePoints: List<LatLng>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (routePoints.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Waiting for GPS signal...",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(routePoints.last(), 16f)
            }

            // Update camera when route changes
            LaunchedEffect(routePoints.lastOrNull()) {
                routePoints.lastOrNull()?.let { lastPoint ->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(lastPoint, 16f)
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                properties = MapProperties()
            ) {
                // Route polyline
                if (routePoints.size >= 2) {
                    Polyline(
                        points = routePoints,
                        color = AccentGreen,
                        width = 12f
                    )
                }

                // Start marker
                routePoints.firstOrNull()?.let { start ->
                    Marker(
                        state = MarkerState(position = start),
                        title = "Start"
                    )
                }

                // Current position marker
                routePoints.lastOrNull()?.let { current ->
                    Circle(
                        center = current,
                        radius = 8.0,
                        fillColor = AccentGreen,
                        strokeColor = TextWhite,
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutInfoCard(day: com.fitnessbuddy.domain.model.TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "WORKOUT DETAILS",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (day.warmup.isNotEmpty()) {
                WorkoutPhaseRow("Warmup", day.warmup)
            }
            if (day.mainSet.isNotEmpty()) {
                WorkoutPhaseRow("Main Set", day.mainSet)
            }
            if (day.cooldown.isNotEmpty()) {
                WorkoutPhaseRow("Cooldown", day.cooldown)
            }
            if (day.description.isNotEmpty()) {
                WorkoutPhaseRow("Notes", day.description)
            }
        }
    }
}

@Composable
private fun WorkoutPhaseRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = AccentGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            color = TextWhite,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FeedbackCard(
    feedbackState: FeedbackState,
    feedback: String?,
    feedbackError: String?,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI COACH FEEDBACK",
                    color = AccentGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            when (feedbackState) {
                FeedbackState.LOADING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentGreen,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Analyzing your workout...",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
                FeedbackState.SUCCESS -> {
                    Text(
                        text = feedback ?: "Great workout!",
                        color = TextWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
                FeedbackState.ERROR -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedbackError ?: "Could not get feedback",
                            color = Color(0xFFFF9800),
                            fontSize = 14.sp
                        )
                    }
                }
                FeedbackState.IDLE -> {
                    // Nothing to show
                }
            }
        }
    }
}

@Composable
private fun WorkoutControls(
    workoutState: WorkoutState,
    workoutMode: WorkoutMode = WorkoutMode.INDOOR,
    hasGpsSignal: Boolean = false,
    onPrepare: () -> Unit = {},
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (workoutState) {
            WorkoutState.NOT_STARTED -> {
                // Show "PREPARE" button for outdoor mode, "START" for indoor
                if (workoutMode == WorkoutMode.OUTDOOR) {
                    Button(
                        onClick = onPrepare,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PREPARE (ACQUIRE GPS)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Indoor mode - start immediately
                    Button(
                        onClick = onStart,
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
                            text = "START",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            WorkoutState.PREPARING -> {
                // GPS acquisition phase
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // GPS Status indicator
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (hasGpsSignal) 
                                AccentGreen.copy(alpha = 0.15f) 
                            else 
                                CardBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (hasGpsSignal) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GPS SIGNAL ACQUIRED",
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AccentGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "WAITING FOR GPS SIGNAL...",
                                    color = TextGray,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Start button (enabled when GPS acquired, or can start anyway)
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasGpsSignal) AccentGreen else AccentGreen.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasGpsSignal) "START WORKOUT" else "START ANYWAY",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            WorkoutState.RUNNING -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PAUSE", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FINISH",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            WorkoutState.PAUSED -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onResume,
                        modifier = Modifier
                            .weight(1f)
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
                            text = "RESUME",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = onComplete,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FINISH", fontWeight = FontWeight.Bold)
                    }
                }
            }
            WorkoutState.COMPLETED -> {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DONE",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportHealthConnectDialog(
    sessions: List<ExerciseSessionInfo>,
    isLoading: Boolean,
    onSessionSelected: (ExerciseSessionInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(
                text = "Import from Health Connect",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                Text(
                    text = "Select a workout from today:",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentGreen)
                        }
                    }
                    sessions.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TextGray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No workouts found today",
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sessions.forEach { session ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSessionSelected(session) },
                                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = session.title,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${session.startTime.atZone(ZoneId.systemDefault()).format(timeFormatter)} • ${session.durationMinutes} min",
                                                color = TextGray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = session.sourceApp.substringAfterLast("."),
                                                color = AccentGreen.copy(alpha = 0.7f),
                                                fontSize = 10.sp
                                            )
                                        }
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = "Import",
                                            tint = AccentGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        }
    )
}
