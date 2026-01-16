package com.fitnessbuddy.ui.permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.healthconnect.HealthConnectManager
import com.fitnessbuddy.domain.repository.TrainingPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val trainingPlanRepository: TrainingPlanRepository
) : ViewModel() {

    var hasPermissions by mutableStateOf(false)
        private set
    
    var hasCompletedOnboarding by mutableStateOf<Boolean?>(null)
        private set

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            try {
                hasPermissions = healthConnectManager.hasAllPermissions()
            } catch (e: Exception) {
                // If Health Connect is unavailable or throws, treat as no permissions
                hasPermissions = false
            }
        }
    }
    
    fun checkOnboardingStatus() {
        viewModelScope.launch {
            hasCompletedOnboarding = trainingPlanRepository.hasTrainingPlan()
        }
    }

    fun onPermissionsResult() {
        checkPermissions()
    }
}
