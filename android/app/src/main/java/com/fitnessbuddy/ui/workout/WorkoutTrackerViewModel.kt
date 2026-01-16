package com.fitnessbuddy.ui.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.location.LocationPoint
import com.fitnessbuddy.data.location.LocationTracker
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
    RUNNING,
    PAUSED,
    COMPLETED
}

@HiltViewModel
class WorkoutTrackerViewModel @Inject constructor(
    private val trainingPlanRepository: TrainingPlanRepository,
    private val locationTracker: LocationTracker
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

    private var timerJob: Job? = null
    private var locationJob: Job? = null

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

    fun startWorkout() {
        workoutState = WorkoutState.RUNNING
        startTimer()
        if (workoutMode == WorkoutMode.OUTDOOR) {
            startLocationTracking()
        }
    }

    fun pauseWorkout() {
        workoutState = WorkoutState.PAUSED
        stopTimer()
        stopLocationTracking()
    }

    fun resumeWorkout() {
        workoutState = WorkoutState.RUNNING
        startTimer()
        if (workoutMode == WorkoutMode.OUTDOOR) {
            startLocationTracking()
        }
    }

    fun completeWorkout() {
        workoutState = WorkoutState.COMPLETED
        stopTimer()
        stopLocationTracking()
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
            }
            .launchIn(viewModelScope)
    }

    private fun stopLocationTracking() {
        locationJob?.cancel()
        locationJob = null
    }

    fun getPace(): String {
        return LocationTracker.calculatePace(distanceMeters, elapsedSeconds)
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
    }
}
