package com.fitnessbuddy.domain.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents an AI-generated training plan.
 */
@Serializable
data class TrainingPlan(
    val id: String = "",
    val eventType: String = "",           // e.g., "Full Marathon"
    val targetDistance: String = "",      // e.g., "42.2km"
    val targetDate: String = "",          // ISO date string
    val totalWeeks: Int = 0,
    val currentWeek: Int = 1,
    val weeks: List<TrainingWeek> = emptyList()
) {
    val daysToRace: Int
        get() {
            return try {
                val target = LocalDate.parse(targetDate)
                val today = LocalDate.now()
                java.time.temporal.ChronoUnit.DAYS.between(today, target).toInt().coerceAtLeast(0)
            } catch (e: Exception) {
                0
            }
        }
}

/**
 * Represents a single training week.
 */
@Serializable
data class TrainingWeek(
    val weekNumber: Int = 0,
    val phase: String = "",               // e.g., "BASE PHASE", "BUILD PHASE"
    val theme: String = "",               // e.g., "Foundation Building", "Increasing Volume"
    val totalDistanceKm: Float = 0f,
    val totalSessions: Int = 0,
    val isCurrentWeek: Boolean = false,
    val isLocked: Boolean = false,
    val days: List<TrainingDay> = emptyList()
) {
    val completedSessions: Int
        get() = days.count { it.isCompleted }
    
    val progressPercent: Int
        get() = if (totalSessions > 0) (completedSessions * 100) / totalSessions else 0
}

/**
 * Represents a single training day.
 */
@Serializable
data class TrainingDay(
    val dayOfWeek: String = "",           // e.g., "Monday", "Tuesday"
    val workoutType: String = "",         // e.g., "Rest", "Easy Run", "Intervals"
    val title: String = "",               // e.g., "8km Easy Run"
    val description: String = "",         // Detailed description
    val distanceKm: Float = 0f,
    val durationMinutes: Int = 0,
    val intensity: String = "",           // "Low", "Moderate", "High"
    val zone: String = "",                // e.g., "Zone 2", "Zone 4"
    val pace: String = "",                // e.g., "5:45 min/km"
    val isRestDay: Boolean = false,
    val isCompleted: Boolean = false,
    val isToday: Boolean = false,
    val warmup: String = "",              // e.g., "15 min Easy"
    val mainSet: String = "",             // e.g., "4x1000m @ 5k Target Pace"
    val cooldown: String = ""             // e.g., "10 min Easy"
)

/**
 * UI state for the training plan screens.
 */
data class TrainingPlanUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val trainingPlan: TrainingPlan? = null,
    val selectedWeek: TrainingWeek? = null
)
