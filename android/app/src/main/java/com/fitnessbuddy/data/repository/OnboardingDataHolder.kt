package com.fitnessbuddy.data.repository

import com.fitnessbuddy.ui.onboarding.OnboardingData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary holder for onboarding data to pass between screens.
 * In production, this should be persisted to a database.
 */
@Singleton
class OnboardingDataHolder @Inject constructor() {
    var onboardingData: OnboardingData? = null
    
    fun save(data: OnboardingData) {
        onboardingData = data
    }
    
    fun clear() {
        onboardingData = null
    }
}
