package com.fitnessbuddy.ui.permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.data.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    var hasPermissions by mutableStateOf(false)
        private set

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            hasPermissions = healthConnectManager.hasAllPermissions()
        }
    }

    fun onPermissionsResult() {
        checkPermissions()
    }
}
