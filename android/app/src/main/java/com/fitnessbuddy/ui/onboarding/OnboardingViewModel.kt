package com.fitnessbuddy.ui.onboarding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.domain.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val onboardingDataHolder: com.fitnessbuddy.data.repository.OnboardingDataHolder
) : ViewModel() {

    // Current step (1-4)
    var currentStep by mutableIntStateOf(1)
        private set
    
    val totalSteps = 4
    
    // Step 2 has sub-steps: mission selection, then distance selection
    var step2SubStep by mutableIntStateOf(1) // 1 = mission, 2 = distance
        private set
    
    // Step 1: Personal Details
    var weight by mutableFloatStateOf(0f)
    var weightUnit by mutableStateOf(WeightUnit.KG)
    var height by mutableFloatStateOf(0f)
    var heightUnit by mutableStateOf(HeightUnit.CM)
    var age by mutableIntStateOf(0)
    
    // Step 2: Mission & Distance
    var selectedMission by mutableStateOf(Mission.MARATHON_TRAINING)
    var selectedEventType by mutableStateOf("") // Stores the selected event type ID
    var targetDate by mutableStateOf(LocalDate.now().plusMonths(6))
    
    // Step 3: Fitness Level
    var selectedFitnessLevel by mutableStateOf(FitnessLevel.BEGINNER)
    
    // Step 4: Training Availability
    var selectedTrainingDays by mutableStateOf(setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI))
    var maxWeeklyHours by mutableIntStateOf(12)
    
    // AI Plan Generation
    var isGeneratingPlan by mutableStateOf(false)
        private set
    var generatedPlan by mutableStateOf<String?>(null)
        private set
    var planGenerationError by mutableStateOf<String?>(null)
        private set
    
    // Navigation
    fun nextStep() {
        if (currentStep < totalSteps) {
            currentStep++
        }
    }
    
    fun previousStep() {
        if (currentStep > 1) {
            currentStep--
        }
    }
    
    fun nextStep2SubStep() {
        if (step2SubStep < 2) {
            step2SubStep++
        }
    }
    
    fun previousStep2SubStep() {
        if (step2SubStep > 1) {
            step2SubStep--
        }
    }
    
    fun isFirstStep() = currentStep == 1
    fun isLastStep() = currentStep == totalSteps
    
    // Progress calculation
    fun getProgressPercent(): Int {
        return when {
            currentStep == 1 -> 25
            currentStep == 2 && step2SubStep == 1 -> 40
            currentStep == 2 && step2SubStep == 2 -> 50
            currentStep == 3 -> 70
            currentStep == 4 -> 90
            else -> (currentStep.toFloat() / totalSteps * 100).toInt()
        }
    }
    
    // Collect all data for AI service
    fun collectOnboardingData(): OnboardingData {
        return OnboardingData(
            weight = weight,
            weightUnit = weightUnit,
            height = height,
            heightUnit = heightUnit,
            age = age,
            mission = selectedMission,
            eventType = selectedEventType,
            targetDate = targetDate,
            fitnessLevel = selectedFitnessLevel,
            trainingDays = selectedTrainingDays,
            maxWeeklyHours = maxWeeklyHours
        )
    }
    
    // Save onboarding data for use by other screens
    fun saveOnboardingData() {
        onboardingDataHolder.save(collectOnboardingData())
    }
    
    /**
     * Generates a personalized training plan using Gemini AI.
     * Call this when onboarding is complete.
     */
    fun generateTrainingPlan(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            isGeneratingPlan = true
            planGenerationError = null
            
            val onboardingData = collectOnboardingData()
            
            geminiRepository.generateTrainingPlan(onboardingData)
                .onSuccess { plan ->
                    generatedPlan = plan
                    Log.d("OnboardingViewModel", "Training plan generated successfully")
                    Log.d("OnboardingViewModel", "Plan: $plan")
                    onComplete(true)
                }
                .onFailure { error ->
                    planGenerationError = error.message ?: "Failed to generate plan"
                    Log.e("OnboardingViewModel", "Plan generation failed", error)
                    onComplete(false)
                }
            
            isGeneratingPlan = false
        }
    }
    
    // Unit conversions
    fun toggleWeightUnit() {
        if (weightUnit == WeightUnit.KG) {
            weight = weight * 2.20462f // kg to lbs
            weightUnit = WeightUnit.LBS
        } else {
            weight = weight / 2.20462f // lbs to kg
            weightUnit = WeightUnit.KG
        }
    }
    
    fun toggleHeightUnit() {
        if (heightUnit == HeightUnit.CM) {
            height = height / 30.48f // cm to ft
            heightUnit = HeightUnit.FT
        } else {
            height = height * 30.48f // ft to cm
            heightUnit = HeightUnit.CM
        }
    }
}
