package com.fitnessbuddy.domain.repository

import com.fitnessbuddy.domain.model.TrainingPlan
import com.fitnessbuddy.ui.onboarding.OnboardingData

/**
 * Repository interface for AI-powered training plan generation using Gemini.
 */
interface GeminiRepository {
    
    /**
     * Generates a personalized training plan based on the user's onboarding data.
     * 
     * @param onboardingData The collected data from user onboarding
     * @return A Result containing the generated training plan as a string, or an error
     */
    suspend fun generateTrainingPlan(onboardingData: OnboardingData): Result<String>
    
    /**
     * Generates and parses a training plan from onboarding data.
     * 
     * @param onboardingData The collected data from user onboarding
     * @return A Result containing the parsed TrainingPlan, or an error
     */
    suspend fun generateAndParseTrainingPlan(onboardingData: OnboardingData): Result<TrainingPlan>
}

