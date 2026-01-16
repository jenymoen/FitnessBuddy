package com.fitnessbuddy.ui.workout

import com.fitnessbuddy.domain.model.WorkoutResult

/**
 * Builds an AI prompt from workout results to generate personalized coaching feedback.
 */
object WorkoutFeedbackPromptBuilder {

    /**
     * Generates a prompt for the AI to provide workout feedback
     * based on the completed workout results.
     *
     * @param result The completed workout data
     * @return A formatted prompt string for the AI service
     */
    fun buildPrompt(result: WorkoutResult): String {
        return buildString {
            appendLine("You are an expert endurance coach. Provide brief, encouraging feedback on this completed workout.")
            appendLine()
            
            // Workout Summary
            appendLine("## Completed Workout")
            appendLine("- Mode: ${result.workoutMode}")
            appendLine("- Duration: ${formatTime(result.elapsedSeconds)}")
            if (result.distanceMeters > 0) {
                appendLine("- Distance: ${String.format("%.2f", result.distanceMeters / 1000f)} km")
                appendLine("- Average Pace: ${result.averagePace}")
            }
            appendLine()
            
            // Planned vs Actual (if we have plan data)
            result.plannedWorkout?.let { planned ->
                appendLine("## Planned Workout")
                appendLine("- Title: ${planned.title}")
                appendLine("- Type: ${planned.workoutType}")
                if (planned.targetDistanceKm > 0) {
                    appendLine("- Target Distance: ${planned.targetDistanceKm} km")
                }
                if (planned.targetDurationMinutes > 0) {
                    appendLine("- Target Duration: ${planned.targetDurationMinutes} min")
                }
                if (planned.targetPace.isNotEmpty()) {
                    appendLine("- Target Pace: ${planned.targetPace}")
                }
                if (planned.intensity.isNotEmpty()) {
                    appendLine("- Intensity: ${planned.intensity}")
                }
                if (planned.warmup.isNotEmpty()) {
                    appendLine("- Warmup: ${planned.warmup}")
                }
                if (planned.mainSet.isNotEmpty()) {
                    appendLine("- Main Set: ${planned.mainSet}")
                }
                if (planned.cooldown.isNotEmpty()) {
                    appendLine("- Cooldown: ${planned.cooldown}")
                }
                appendLine()
            }
            
            // Instructions for the AI
            appendLine("## Instructions")
            appendLine("Provide a short, motivating feedback (2-3 sentences max) that:")
            appendLine("1. Acknowledges the effort")
            appendLine("2. If plan data is available, comments on how actual compared to planned")
            appendLine("3. Gives one specific tip for next time")
            appendLine()
            appendLine("Keep the tone friendly and encouraging. Respond in plain text only, no markdown.")
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
}
