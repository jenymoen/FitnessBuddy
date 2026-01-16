package com.fitnessbuddy.ui.trainingplan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.repository.OnboardingDataHolder
import com.fitnessbuddy.domain.model.TrainingPlan
import com.fitnessbuddy.domain.model.TrainingPlanUiState
import com.fitnessbuddy.domain.model.TrainingWeek
import com.fitnessbuddy.domain.repository.GeminiRepository
import com.fitnessbuddy.domain.repository.TrainingPlanRepository
import com.fitnessbuddy.ui.onboarding.OnboardingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingPlanViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val trainingPlanRepository: TrainingPlanRepository,
    private val onboardingDataHolder: OnboardingDataHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingPlanUiState())
    val uiState: StateFlow<TrainingPlanUiState> = _uiState.asStateFlow()
    
    /**
     * Generates a new training plan from saved onboarding data.
     */
    fun generatePlanFromSavedData() {
        val savedData = onboardingDataHolder.onboardingData
        if (savedData != null) {
            generatePlan(savedData)
        } else {
            Log.w("TrainingPlanViewModel", "No saved onboarding data found")
            _uiState.update { it.copy(error = "No onboarding data available. Please complete onboarding.") }
        }
    }
    
    /**
     * Loads an existing training plan from Firestore.
     */
    fun loadSavedPlan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            trainingPlanRepository.getActiveTrainingPlan()
                .onSuccess { plan ->
                    if (plan != null) {
                        Log.d("TrainingPlanViewModel", "Loaded plan: ${plan.weeks.size} weeks")
                        _uiState.update { 
                            it.copy(isGenerating = false, trainingPlan = markCurrentWeek(plan)) 
                        }
                    } else {
                        _uiState.update { it.copy(isGenerating = false) }
                    }
                }
                .onFailure { error ->
                    Log.e("TrainingPlanViewModel", "Failed to load plan", error)
                    _uiState.update { it.copy(isGenerating = false, error = error.message) }
                }
        }
    }
    
    /**
     * Generates a new training plan from onboarding data.
     */
    fun generatePlan(onboardingData: OnboardingData) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            
            geminiRepository.generateAndParseTrainingPlan(onboardingData)
                .onSuccess { plan ->
                    Log.d("TrainingPlanViewModel", "Plan generated: ${plan.weeks.size} weeks")
                    val markedPlan = markCurrentWeek(plan)
                    _uiState.update { 
                        it.copy(isGenerating = false, trainingPlan = markedPlan) 
                    }
                    // Auto-save to Firestore
                    savePlanToFirestore(markedPlan)
                }
                .onFailure { error ->
                    Log.e("TrainingPlanViewModel", "Failed to generate plan", error)
                    _uiState.update { 
                        it.copy(
                            isGenerating = false, 
                            error = error.message ?: "Failed to generate plan"
                        ) 
                    }
                }
        }
    }
    
    private fun savePlanToFirestore(plan: TrainingPlan) {
        viewModelScope.launch {
            trainingPlanRepository.saveTrainingPlan(plan)
                .onSuccess { Log.d("TrainingPlanViewModel", "Plan saved to Firestore") }
                .onFailure { Log.e("TrainingPlanViewModel", "Failed to save plan", it) }
        }
    }
    
    /**
     * Selects a week for detailed view.
     */
    fun selectWeek(week: TrainingWeek) {
        _uiState.update { it.copy(selectedWeek = week) }
    }
    
    /**
     * Clears the selected week.
     */
    fun clearSelectedWeek() {
        _uiState.update { it.copy(selectedWeek = null) }
    }
    
    /**
     * Marks the current week in the plan based on today's date.
     */
    private fun markCurrentWeek(plan: TrainingPlan): TrainingPlan {
        val currentWeekNum = plan.currentWeek.coerceIn(1, plan.totalWeeks)
        val updatedWeeks = plan.weeks.mapIndexed { index, week ->
            week.copy(
                isCurrentWeek = (index + 1) == currentWeekNum,
                isLocked = (index + 1) > currentWeekNum
            )
        }
        return plan.copy(weeks = updatedWeeks, currentWeek = currentWeekNum)
    }
    
    /**
     * Sets a mock training plan for testing UI.
     */
    fun setMockPlan() {
        val mockPlan = createMockPlan()
        _uiState.update { it.copy(trainingPlan = mockPlan) }
    }
    
    private fun createMockPlan(): TrainingPlan {
        // Create a sample plan for UI testing
        return TrainingPlan(
            id = "mock-plan",
            eventType = "Full Marathon",
            targetDistance = "42.2km",
            targetDate = "2026-07-01",
            totalWeeks = 16,
            currentWeek = 5,
            weeks = (1..16).map { weekNum ->
                TrainingWeek(
                    weekNumber = weekNum,
                    phase = when {
                        weekNum <= 4 -> "BASE PHASE"
                        weekNum <= 8 -> "BUILD PHASE"
                        weekNum <= 13 -> "PEAK PHASE"
                        else -> "TAPER PHASE"
                    },
                    theme = when (weekNum) {
                        1 -> "Foundation Building"
                        5 -> "Increasing Volume"
                        6 -> "Building Long Runs"
                        7 -> "Interval Mastery"
                        else -> "Progressive Training"
                    },
                    totalDistanceKm = (25f + (weekNum * 3f)).coerceAtMost(70f),
                    totalSessions = if (weekNum > 13) 3 else 5,
                    isCurrentWeek = weekNum == 5,
                    isLocked = weekNum > 5,
                    days = emptyList() // Will be populated on week selection
                )
            }
        )
    }
}
