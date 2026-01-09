package com.fitnessbuddy.domain.model

data class WorkoutPlan(
    val id: String,
    val name: String,
    val description: String,
    val weeks: List<WeeklySchedule>
)

data class WeeklySchedule(
    val weekNumber: Int,
    val days: List<DailyWorkout>
)

data class DailyWorkout(
    val id: String,
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val intensity: String, // "Low", "Moderate", "High"
    val exercises: List<Exercise>,
    val isCompleted: Boolean = false,
    val isRestDay: Boolean = false
)

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val notes: String = ""
)
