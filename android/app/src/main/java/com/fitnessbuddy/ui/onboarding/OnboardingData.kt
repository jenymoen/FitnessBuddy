package com.fitnessbuddy.ui.onboarding

import java.time.LocalDate

/**
 * Data class to hold all onboarding inputs from the user.
 * This will be sent to the AI service to create a personalized training plan.
 */
data class OnboardingData(
    // Step 1: Personal Details
    val weight: Float? = null,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val height: Float? = null,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val age: Int? = null,
    
    // Step 2: Mission & Distance
    val mission: Mission = Mission.MARATHON_TRAINING,
    val eventType: String = "",
    val targetDate: LocalDate = LocalDate.now().plusMonths(6),
    
    // Step 3: Fitness Level
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    
    // Step 4: Training Availability
    val trainingDays: Set<DayOfWeek> = setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI),
    val maxWeeklyHours: Int = 12
)

enum class DayOfWeek(val shortName: String, val fullName: String) {
    MON("MON", "Monday"),
    TUE("TUE", "Tuesday"),
    WED("WED", "Wednesday"),
    THU("THU", "Thursday"),
    FRI("FRI", "Friday"),
    SAT("SAT", "Saturday"),
    SUN("SUN", "Sunday")
}

enum class WeightUnit(val label: String, val suffix: String) {
    KG("kg", "kg"),
    LBS("lbs", "lbs")
}

enum class HeightUnit(val label: String, val suffix: String) {
    CM("cm", "cm"),
    FT("ft", "ft")
}

enum class Mission(val displayName: String, val icon: String) {
    MARATHON_TRAINING("MARATHON TRAINING", "running"),
    CYCLOCROSS("CYCLOCROSS", "cycling"),
    TRIATHLON("TRIATHLON", "swimming"),
    TRAIL_RUNNING("TRAIL RUNNING", "mountain")
}

enum class FitnessLevel(val displayName: String, val subtitle: String, val icon: String) {
    BEGINNER("Beginner", "JUST STARTING OUT", "🌱"),
    INTERMEDIATE("Intermediate", "RUNNING 10-20KM/WEEK", "🏃"),
    ADVANCED("Advanced", "RUNNING 30-50KM/WEEK", "🔥"),
    PRO("Pro", "HIGH-VOLUME ATHLETE", "⚡")
}

// Running distances (Marathon Training, Trail Running)
enum class RunningDistance(val displayName: String, val subtitle: String) {
    FULL_MARATHON("FULL MARATHON", "42.2K"),
    HALF_MARATHON("HALF MARATHON", "21.1K"),
    TEN_KM("10 KM", "THRESHOLD RUN"),
    FIVE_KM("5 KM", "SPEED RUN")
}

// Triathlon distances
enum class TriathlonDistance(val displayName: String, val subtitle: String) {
    IRONMAN("IRONMAN", "Full distance"),
    HALF_IRONMAN("70.3", "Half Ironman"),
    OLYMPIC("OLYMPIC", "Standard distance"),
    SPRINT("SPRINT", "Short distance")
}

// Cycling distances (Cyclocross)
enum class CyclingDistance(val displayName: String, val subtitle: String) {
    RACE_SERIES("RACE SERIES", "Competition ready"),
    GRAN_FONDO("GRAN FONDO", "Long distance"),
    CENTURY("CENTURY", "100 miles"),
    METRIC_CENTURY("METRIC CENTURY", "100 km")
}

// Helper function to get distances for a mission
fun getDistancesForMission(mission: Mission): List<EventType> {
    return when (mission) {
        Mission.MARATHON_TRAINING, Mission.TRAIL_RUNNING -> RunningDistance.entries.map { 
            EventType(it.name, it.displayName, it.subtitle) 
        }
        Mission.TRIATHLON -> TriathlonDistance.entries.map { 
            EventType(it.name, it.displayName, it.subtitle) 
        }
        Mission.CYCLOCROSS -> CyclingDistance.entries.map { 
            EventType(it.name, it.displayName, it.subtitle) 
        }
    }
}

// Generic event type for UI display
data class EventType(
    val id: String,
    val displayName: String,
    val subtitle: String
)



