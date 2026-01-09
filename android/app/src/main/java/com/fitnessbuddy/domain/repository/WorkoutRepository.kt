package com.fitnessbuddy.domain.repository

import com.fitnessbuddy.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun getActiveWorkoutPlan(): Result<WorkoutPlan>
    suspend fun getWorkoutById(workoutId: String): Result<DailyWorkout?>
    suspend fun markWorkoutComplete(workoutId: String): Result<Unit>
    suspend fun shiftSchedule(fromDayId: String): Result<Unit>
}
