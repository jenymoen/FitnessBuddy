package com.fitnessbuddy.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.domain.model.TrainingDay
import com.fitnessbuddy.domain.model.TrainingPlan
import com.fitnessbuddy.domain.repository.TrainingPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "Runner",
    val planType: String = "ELITE PLAN",
    val isSynced: Boolean = true,
    val trainingPlan: TrainingPlan? = null,
    val daysToRace: Int = 0,
    val currentWeek: Int = 1,
    val totalWeeks: Int = 24,
    val planProgress: Float = 0f,
    val todayWorkout: TrainingDay? = null,
    val weeklyDistanceKm: Float = 0f,
    val weeklyGoalKm: Float = 65f,
    val weeklyProgress: Float = 0f,
    val vo2Max: Float = 0f,
    val recoveryPercent: Int = 88,
    val aiInsight: String = "",
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val trainingPlanRepository: TrainingPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            trainingPlanRepository.getActiveTrainingPlan()
                .onSuccess { plan ->
                    if (plan != null) {
                        Log.d("DashboardViewModel", "Loaded plan: ${plan.eventType}")
                        updateDashboardFromPlan(plan)
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "No training plan found") }
                    }
                }
                .onFailure { error ->
                    Log.e("DashboardViewModel", "Failed to load plan", error)
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun updateDashboardFromPlan(plan: TrainingPlan) {
        // Calculate days to race
        val daysToRace = if (plan.targetDate.isNotEmpty()) {
            try {
                val targetDate = LocalDate.parse(plan.targetDate)
                ChronoUnit.DAYS.between(LocalDate.now(), targetDate).toInt().coerceAtLeast(0)
            } catch (e: Exception) {
                plan.daysToRace
            }
        } else {
            plan.daysToRace
        }
        
        // Find current week
        val currentWeek = plan.weeks.firstOrNull { it.isCurrentWeek }
            ?: plan.weeks.getOrNull(plan.currentWeek - 1)
            ?: plan.weeks.firstOrNull()
        
        // Find today's workout
        val todayWorkout = currentWeek?.days?.firstOrNull { it.isToday }
            ?: currentWeek?.days?.firstOrNull { !it.isCompleted && !it.isRestDay }
        
        // Calculate progress
        val planProgress = if (plan.totalWeeks > 0) {
            (plan.currentWeek.toFloat() / plan.totalWeeks * 100).coerceIn(0f, 100f)
        } else 0f
        
        val weeklyDistanceKm = currentWeek?.days?.filter { it.isCompleted }?.sumOf { it.distanceKm.toDouble() }?.toFloat() ?: 0f
        val weeklyGoalKm = currentWeek?.totalDistanceKm ?: 65f
        
        _uiState.update {
            it.copy(
                isLoading = false,
                trainingPlan = plan,
                daysToRace = daysToRace,
                currentWeek = plan.currentWeek,
                totalWeeks = plan.totalWeeks,
                planProgress = planProgress,
                todayWorkout = todayWorkout,
                weeklyDistanceKm = weeklyDistanceKm,
                weeklyGoalKm = weeklyGoalKm,
                weeklyProgress = if (weeklyGoalKm > 0) (weeklyDistanceKm / weeklyGoalKm * 100) else 0f,
                aiInsight = generateAiInsight(plan, currentWeek?.phase ?: ""),
                error = null
            )
        }
    }

    private fun generateAiInsight(plan: TrainingPlan, phase: String): String {
        return when {
            phase.contains("BASE", ignoreCase = true) -> 
                "Your aerobic base is strengthening. The next 4 weeks will focus on increasing long-run duration by 10% weekly."
            phase.contains("BUILD", ignoreCase = true) -> 
                "Building endurance nicely. Focus on maintaining Zone 2 effort for long runs to maximize aerobic adaptation."
            phase.contains("PEAK", ignoreCase = true) -> 
                "Peak training phase - stay consistent with quality sessions. Recovery is as important as training now."
            phase.contains("TAPER", ignoreCase = true) -> 
                "Taper phase begins. Reduce volume while maintaining intensity. Trust your training!"
            else -> 
                "Stay consistent with your training plan. Each session builds toward your goal."
        }
    }

    fun refreshData() {
        loadDashboardData()
    }
}
