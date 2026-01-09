package com.fitnessbuddy.data.repository

import com.fitnessbuddy.domain.model.DailyWorkout
import com.fitnessbuddy.domain.model.Exercise
import com.fitnessbuddy.domain.model.WeeklySchedule
import com.fitnessbuddy.domain.model.WorkoutPlan
import com.fitnessbuddy.domain.repository.WorkoutRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor() : WorkoutRepository {

    override suspend fun getActiveWorkoutPlan(): Result<WorkoutPlan> {
        delay(500) // Simulate network delay
        return Result.success(mockPlan)
    }

    override suspend fun markWorkoutComplete(workoutId: String): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }

    override suspend fun getWorkoutById(workoutId: String): Result<DailyWorkout?> {
        delay(100)
        val workout = mockPlan.weeks.flatMap { it.days }.find { it.id == workoutId }
        return Result.success(workout)
    }

    override suspend fun shiftSchedule(fromDayId: String): Result<Unit> {
        delay(500)
        val plan = mockPlan
        val updatedWeeks = plan.weeks.map { week ->
            week.copy(days = week.days.map { day ->
                if (day.id == fromDayId) {
                    day.copy(title = "${day.title} (Missed)", isCompleted = false)
                } else {
                    day
                }
            }.toMutableList())
        }
        mockPlan = plan.copy(weeks = updatedWeeks)
        return Result.success(Unit)
    }

    private var mockPlan = WorkoutPlan(
        id = "plan_1",
        name = "Couch to 5K & Strength",
        description = "A balanced plan to get you running and building muscle.",
        weeks = listOf(
            WeeklySchedule(
                weekNumber = 1,
                days = listOf(
                    DailyWorkout(
                        id = "w1_d1",
                        dayOfWeek = "Monday",
                        title = "Full Body Strength",
                        description = "Foundation strength movements",
                        durationMinutes = 45,
                        intensity = "Moderate",
                        exercises = listOf(
                            Exercise("ex_1", "Squats", 3, 12, 60),
                            Exercise("ex_2", "Push-ups", 3, 10, 60),
                            Exercise("ex_3", "Lunges", 3, 12, 60),
                            Exercise("ex_4", "Plank", 3, 1, 45, "Hold for 45-60 seconds")
                        )
                    ),
                    DailyWorkout(
                        id = "w1_d2",
                        dayOfWeek = "Tuesday",
                        title = "Easy Run",
                        description = "Light jogging to build aerobic base",
                        durationMinutes = 30,
                        intensity = "Low",
                        exercises = listOf(
                            Exercise("ex_5", "Jogging", 1, 1, 0, "Consistent pace")
                        )
                    ),
                    DailyWorkout(
                        id = "w1_d3",
                        dayOfWeek = "Wednesday",
                        title = "Rest Day",
                        description = "Active recovery",
                        durationMinutes = 0,
                        intensity = "Rest",
                        exercises = emptyList(),
                        isRestDay = true
                    ),
                     DailyWorkout(
                        id = "w1_d4",
                        dayOfWeek = "Thursday",
                        title = "Upper Body Focus",
                        description = "Push and pull movements",
                        durationMinutes = 40,
                        intensity = "Moderate",
                        exercises = listOf(
                             Exercise("ex_6", "Dumbbell Press", 3, 12, 60),
                             Exercise("ex_7", "Rows", 3, 12, 60)
                        )
                    ),
                     DailyWorkout(
                        id = "w1_d5",
                        dayOfWeek = "Friday",
                        title = "Interval Run",
                        description = "Run/Walk intervals",
                        durationMinutes = 35,
                        intensity = "High",
                        exercises = listOf(
                             Exercise("ex_8", "Intervals", 5, 1, 60, "1 min fast, 2 min walk")
                        )
                    ),
                     DailyWorkout(
                        id = "w1_d6",
                        dayOfWeek = "Saturday",
                        title = "Long Run",
                        description = "Slow comfortable pace",
                        durationMinutes = 60,
                        intensity = "Moderate",
                        exercises = listOf(
                             Exercise("ex_9", "Long Run", 1, 1, 0)
                        )
                    ),
                     DailyWorkout(
                        id = "w1_d7",
                        dayOfWeek = "Sunday",
                        title = "Rest Day",
                        description = "Relax",
                        durationMinutes = 0,
                        intensity = "Rest",
                        exercises = emptyList(),
                        isRestDay = true
                    )
                )
            )
        )
    )
}
