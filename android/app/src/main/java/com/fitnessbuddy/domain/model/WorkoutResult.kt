package com.fitnessbuddy.domain.model

import com.fitnessbuddy.data.location.LocationPoint

/**
 * Represents the result of a completed workout session.
 * This data is sent to Gemini AI to generate personalized feedback.
 */
data class WorkoutResult(
    val elapsedSeconds: Long,
    val distanceMeters: Float,
    val workoutMode: String,           // "INDOOR" or "OUTDOOR"
    val averagePace: String,           // e.g., "5:30 min/km"
    val plannedWorkout: PlannedWorkoutInfo?,
    val routePoints: List<LocationPoint> = emptyList()
)

/**
 * Simplified workout info from the training plan for context in feedback.
 */
data class PlannedWorkoutInfo(
    val title: String,
    val workoutType: String,
    val targetDistanceKm: Float,
    val targetDurationMinutes: Int,
    val targetPace: String,
    val intensity: String,
    val warmup: String,
    val mainSet: String,
    val cooldown: String
)

/**
 * Extension function to convert TrainingDay to PlannedWorkoutInfo.
 */
fun TrainingDay.toPlannedWorkoutInfo(): PlannedWorkoutInfo {
    return PlannedWorkoutInfo(
        title = title,
        workoutType = workoutType,
        targetDistanceKm = distanceKm,
        targetDurationMinutes = durationMinutes,
        targetPace = pace,
        intensity = intensity,
        warmup = warmup,
        mainSet = mainSet,
        cooldown = cooldown
    )
}
