package com.fitnessbuddy.ui.onboarding

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Builds an AI prompt dynamically from the user's onboarding data
 * to generate a personalized training plan in JSON format.
 */
object TrainingPlanPromptBuilder {

    /**
     * Generates a comprehensive prompt for the AI to create a training plan
     * based on the user's onboarding inputs.
     *
     * @param data The collected onboarding data from the user
     * @return A formatted prompt string for the AI service
     */
    fun buildPrompt(data: OnboardingData): String {
        val weeksUntilEvent = calculateWeeksUntilEvent(data.targetDate)
        val eventTypeDisplay = formatEventType(data.mission, data.eventType)
        val targetDistanceKm = getTargetDistanceKm(data.eventType)
        
        return buildString {
            appendLine("You are an expert endurance coach. Create a personalized training plan.")
            appendLine()
            
            // User Profile Section
            appendLine("## Athlete Profile")
            data.weight?.let { appendLine("- Weight: $it ${data.weightUnit.suffix}") }
            data.height?.let { appendLine("- Height: $it ${data.heightUnit.suffix}") }
            data.age?.let { appendLine("- Age: $it years") }
            appendLine("- Fitness Level: ${data.fitnessLevel.displayName}")
            appendLine()
            
            // Training Goal Section
            appendLine("## Training Goal")
            appendLine("- Event: ${data.mission.displayName}")
            appendLine("- Distance: $eventTypeDisplay")
            appendLine("- Target Date: ${data.targetDate}")
            appendLine("- Training Duration: $weeksUntilEvent weeks")
            appendLine()
            
            // Training Availability
            appendLine("## Training Availability")
            appendLine("- Available Days: ${data.trainingDays.joinToString(", ") { it.fullName }}")
            appendLine("- Max Weekly Hours: ${data.maxWeeklyHours}")
            appendLine()
            
            // Requirements
            appendLine("## Plan Requirements")
            appendLine(getRequirements(data.mission, weeksUntilEvent, data.fitnessLevel))
            appendLine()
            
            // JSON Output Format
            appendLine("## CRITICAL: Output Format")
            appendLine("Return ONLY valid JSON (no markdown). Create a $weeksUntilEvent week plan.")
            appendLine()
            appendLine("IMPORTANT: Generate DETAILED days (with workouts) for weeks 1-8 only.")
            appendLine("For weeks 9-$weeksUntilEvent, include summary info (phase, theme, distance) but set days to empty array and isLocked=true.")
            appendLine()
            appendLine("Structure:")
            appendLine("""
{
  "eventType": "$eventTypeDisplay",
  "targetDistance": "${targetDistanceKm}km",
  "targetDate": "${data.targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
  "totalWeeks": $weeksUntilEvent,
  "currentWeek": 1,
  "weeks": [
    {
      "weekNumber": 1,
      "phase": "BASE PHASE",
      "theme": "Foundation Building",
      "totalDistanceKm": 25.0,
      "totalSessions": 4,
      "isCurrentWeek": true,
      "isLocked": false,
      "days": [{"dayOfWeek": "Monday", "workoutType": "Rest", "title": "Rest Day", "description": "", "distanceKm": 0, "durationMinutes": 0, "intensity": "", "isRestDay": true}]
    },
    {
      "weekNumber": 9,
      "phase": "BUILD PHASE",
      "theme": "Increasing Volume",
      "totalDistanceKm": 45.0,
      "totalSessions": 5,
      "isCurrentWeek": false,
      "isLocked": true,
      "days": []
    }
  ]
}
            """.trimIndent())
            appendLine()
            appendLine("Training days: ${data.trainingDays.joinToString(", ") { it.fullName }}. Other days are rest.")
            appendLine("Phases: BASE (weeks 1-${weeksUntilEvent/4}), BUILD (${weeksUntilEvent/4+1}-${weeksUntilEvent/2}), PEAK (${weeksUntilEvent/2+1}-${weeksUntilEvent-2}), TAPER (last 2 weeks).")
        }
    }


    private fun calculateWeeksUntilEvent(targetDate: LocalDate): Long {
        val today = LocalDate.now()
        return ChronoUnit.WEEKS.between(today, targetDate).coerceIn(4, 24)
    }

    private fun formatEventType(mission: Mission, eventType: String): String {
        return when (mission) {
            Mission.MARATHON_TRAINING, Mission.TRAIL_RUNNING -> {
                RunningDistance.entries.find { it.name == eventType }?.displayName ?: eventType
            }
            Mission.TRIATHLON -> {
                TriathlonDistance.entries.find { it.name == eventType }?.displayName ?: eventType
            }
            Mission.CYCLOCROSS -> {
                CyclingDistance.entries.find { it.name == eventType }?.displayName ?: eventType
            }
        }
    }
    
    private fun getTargetDistanceKm(eventType: String): Float {
        return when (eventType) {
            "FULL_MARATHON" -> 42.2f
            "HALF_MARATHON" -> 21.1f
            "TEN_KM" -> 10f
            "FIVE_KM" -> 5f
            "IRONMAN" -> 226f // Total triathlon distance
            "HALF_IRONMAN" -> 113f
            "OLYMPIC" -> 51.5f
            "SPRINT" -> 25.75f
            else -> 42.2f
        }
    }

    private fun getRequirements(mission: Mission, weeksUntilEvent: Long, fitnessLevel: FitnessLevel): String {
        val volumeMultiplier = when (fitnessLevel) {
            FitnessLevel.BEGINNER -> 0.6f
            FitnessLevel.INTERMEDIATE -> 0.8f
            FitnessLevel.ADVANCED -> 1.0f
            FitnessLevel.PRO -> 1.2f
        }
        
        val baseRequirements = buildString {
            appendLine("- Create a $weeksUntilEvent-week progressive plan for ${fitnessLevel.displayName} level")
            appendLine("- Volume multiplier: $volumeMultiplier (adjust distances accordingly)")
            appendLine("- Include recovery weeks every 3-4 weeks")
        }
        
        val missionSpecific = when (mission) {
            Mission.MARATHON_TRAINING -> """
                - Long run progression: start at 12-15km, peak at 32-35km
                - Include tempo runs, intervals, and easy runs
                - Weekly long run on weekend
            """.trimIndent()
            
            Mission.TRAIL_RUNNING -> """
                - Include vertical gain progression
                - Mix trail and road runs
                - Strength and mobility sessions
            """.trimIndent()
            
            Mission.TRIATHLON -> """
                - Balance swim, bike, run appropriately
                - Include brick workouts
                - Sport-specific strength training
            """.trimIndent()
            
            Mission.CYCLOCROSS -> """
                - High-intensity intervals
                - Technical skills practice
                - Off-bike running segments
            """.trimIndent()
        }
        
        return baseRequirements + missionSpecific
    }
}

