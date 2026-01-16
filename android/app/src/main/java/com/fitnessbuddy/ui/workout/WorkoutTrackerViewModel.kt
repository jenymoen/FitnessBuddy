package com.fitnessbuddy.ui.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.sensor.BluetoothHeartRateManager
import com.fitnessbuddy.data.sensor.HeartRateSensorState
import com.fitnessbuddy.data.location.LocationPoint
import com.fitnessbuddy.data.location.LocationTracker
import com.fitnessbuddy.domain.model.WorkoutResult
import com.fitnessbuddy.domain.model.toPlannedWorkoutInfo
import com.fitnessbuddy.domain.repository.GeminiRepository
import com.fitnessbuddy.domain.repository.TrainingPlanRepository
import com.fitnessbuddy.domain.model.TrainingDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WorkoutMode {
    INDOOR,
    OUTDOOR
}

enum class WorkoutState {
    NOT_STARTED,
    PREPARING,    // GPS acquisition phase - location tracking starts but timer doesn't
    RUNNING,
    PAUSED,
    COMPLETED
}

enum class FeedbackState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

@HiltViewModel
class WorkoutTrackerViewModel @Inject constructor(
    private val trainingPlanRepository: TrainingPlanRepository,
    private val locationTracker: LocationTracker,
    private val geminiRepository: GeminiRepository,
    private val bluetoothHeartRateManager: BluetoothHeartRateManager
) : ViewModel() {

    var trainingDay by mutableStateOf<TrainingDay?>(null)
        private set

    var workoutMode by mutableStateOf(WorkoutMode.INDOOR)
        private set

    var workoutState by mutableStateOf(WorkoutState.NOT_STARTED)
        private set

    var elapsedSeconds by mutableLongStateOf(0L)
        private set

    var distanceMeters by mutableFloatStateOf(0f)
        private set

    val routePoints = mutableStateListOf<LocationPoint>()

    var isLoading by mutableStateOf(false)
        private set

    // Heart rate state (from Bluetooth sensor)
    var currentHeartRate by mutableStateOf<Int?>(null)
        private set
    
    var heartRateSensorState by mutableStateOf(HeartRateSensorState.DISCONNECTED)
        private set
    
    // Current speed in m/s (from GPS)
    var currentSpeedMs by mutableFloatStateOf(0f)
        private set
    
    // GPS signal acquired (has at least one location point)
    val hasGpsSignal: Boolean
        get() = routePoints.isNotEmpty()

    // Feedback state
    var feedbackState by mutableStateOf(FeedbackState.IDLE)
        private set
    
    var feedback by mutableStateOf<String?>(null)
        private set
    
    var feedbackError by mutableStateOf<String?>(null)
        private set

    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var heartRateJob: Job? = null

    fun loadWorkout(weekNumber: Int, dayOfWeek: String) {
        viewModelScope.launch {
            isLoading = true
            trainingPlanRepository.getActiveTrainingPlan()
                .onSuccess { plan ->
                    val week = plan?.weeks?.find { it.weekNumber == weekNumber }
                    trainingDay = week?.days?.find { 
                        it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) 
                    }
                }
            isLoading = false
        }
    }

    fun setMode(mode: WorkoutMode) {
        if (workoutState == WorkoutState.NOT_STARTED) {
            workoutMode = mode
        }
    }

    /**
     * Prepare workout - starts GPS acquisition (outdoor) and heart rate monitoring
     * but doesn't start the timer yet.
     */
    fun prepareWorkout() {
        workoutState = WorkoutState.PREPARING
        startHeartRateMonitoring()
        if (workoutMode == WorkoutMode.OUTDOOR) {
            startLocationTracking()
        }
    }
    
    /**
     * Actually start the workout timer.
     * For outdoor mode, should be called after GPS signal is acquired.
     */
    fun startWorkout() {
        workoutState = WorkoutState.RUNNING
        startTimer()
        // If we weren't already preparing, start the tracking now
        if (workoutMode == WorkoutMode.OUTDOOR && locationJob == null) {
            startLocationTracking()
        }
        if (heartRateJob == null) {
            startHeartRateMonitoring()
        }
    }

    fun pauseWorkout() {
        workoutState = WorkoutState.PAUSED
        stopTimer()
        stopLocationTracking()
        stopHeartRateMonitoring()
    }

    fun resumeWorkout() {
        workoutState = WorkoutState.RUNNING
        startTimer()
        startHeartRateMonitoring()
        if (workoutMode == WorkoutMode.OUTDOOR) {
            startLocationTracking()
        }
    }

    fun completeWorkout() {
        workoutState = WorkoutState.COMPLETED
        stopTimer()
        stopLocationTracking()
        stopHeartRateMonitoring()
        
        // Request feedback from Gemini
        requestFeedback()
    }
    
    private fun requestFeedback() {
        feedbackState = FeedbackState.LOADING
        feedbackError = null
        
        viewModelScope.launch {
            val result = WorkoutResult(
                elapsedSeconds = elapsedSeconds,
                distanceMeters = distanceMeters,
                workoutMode = workoutMode.name,
                averagePace = getPace(),
                plannedWorkout = trainingDay?.toPlannedWorkoutInfo(),
                routePoints = routePoints.toList()
            )
            
            geminiRepository.generateWorkoutFeedback(result)
                .onSuccess { feedbackText ->
                    feedback = feedbackText
                    feedbackState = FeedbackState.SUCCESS
                }
                .onFailure { error ->
                    feedbackError = error.message ?: "Failed to get feedback"
                    feedbackState = FeedbackState.ERROR
                }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startLocationTracking() {
        locationJob?.cancel()
        locationJob = locationTracker.getLocationUpdates()
            .onEach { point ->
                routePoints.add(point)
                distanceMeters = LocationTracker.calculateDistance(routePoints)
                currentSpeedMs = point.speed // Update current speed from GPS
            }
            .launchIn(viewModelScope)
    }

    private fun stopLocationTracking() {
        locationJob?.cancel()
        locationJob = null
    }
    
    private fun startHeartRateMonitoring() {
        heartRateJob?.cancel()
        // Collect heart rate from Bluetooth sensor
        heartRateJob = viewModelScope.launch {
            launch {
                bluetoothHeartRateManager.currentHeartRate.collect { heartRate ->
                    currentHeartRate = heartRate
                }
            }
            launch {
                bluetoothHeartRateManager.sensorState.collect { state ->
                    heartRateSensorState = state
                }
            }
        }
    }
    
    private fun stopHeartRateMonitoring() {
        heartRateJob?.cancel()
        heartRateJob = null
    }

    fun getPace(): String {
        return LocationTracker.calculatePace(distanceMeters, elapsedSeconds)
    }
    
    /**
     * Get current speed in km/h
     */
    fun getSpeedKmh(): Float {
        return currentSpeedMs * 3.6f // Convert m/s to km/h
    }

    fun formatTime(): String {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        stopLocationTracking()
        stopHeartRateMonitoring()
    }
}


