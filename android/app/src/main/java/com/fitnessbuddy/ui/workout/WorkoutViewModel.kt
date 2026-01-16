package com.fitnessbuddy.ui.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.domain.model.DailyWorkout
import com.fitnessbuddy.domain.model.WorkoutPlan
import com.fitnessbuddy.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    var workoutPlan by mutableStateOf<WorkoutPlan?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set
        
    init {
        fetchWorkoutPlan()
    }

    fun fetchWorkoutPlan() {
        viewModelScope.launch {
            isLoading = true
            val result = workoutRepository.getActiveWorkoutPlan()
            workoutPlan = result.getOrNull()
            isLoading = false
        }
    }

    var activeWorkout by mutableStateOf<DailyWorkout?>(null)
        private set

    fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            isLoading = true
            val result = workoutRepository.getWorkoutById(workoutId)
            activeWorkout = result.getOrNull()
            isLoading = false
        }
    }

    fun adaptSchedule(missedWorkoutId: String) {
        viewModelScope.launch {
            isLoading = true
            workoutRepository.shiftSchedule(missedWorkoutId)
            // Refresh plan
            fetchWorkoutPlan()
            isLoading = false
        }
    }
}
