package com.fitnessbuddy.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    var stepsToday by mutableStateOf<Long>(0)
        private set
        
    var isLoading by mutableStateOf(false)
        private set

    fun fetchTodaySteps() {
        viewModelScope.launch {
            isLoading = true
            val now = Instant.now()
            val startOfDay = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
            
            // Health Connect uses Instant. In real app, consider User's TimeZone.
            // For MVP, we use UTC/System truncated.
            
            stepsToday = healthConnectManager.readSteps(startOfDay, now)
            isLoading = false
        }
    }
}
