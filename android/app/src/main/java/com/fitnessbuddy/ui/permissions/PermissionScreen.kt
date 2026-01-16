package com.fitnessbuddy.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PermissionScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Check Health Connect availability
    val sdkStatus = HealthConnectClient.getSdkStatus(context)
    val isHealthConnectAvailable = sdkStatus == HealthConnectClient.SDK_AVAILABLE
    
    // Check onboarding status when permissions are granted
    LaunchedEffect(viewModel.hasPermissions) {
        if (viewModel.hasPermissions) {
            viewModel.checkOnboardingStatus()
        }
    }
    
    // Navigate based on onboarding completion status
    LaunchedEffect(viewModel.hasCompletedOnboarding) {
        viewModel.hasCompletedOnboarding?.let { hasCompleted ->
            if (hasCompleted) {
                onNavigateToDashboard()
            } else {
                onNavigateToOnboarding()
            }
        }
    }
    
    // If permissions already granted, just wait for onboarding check
    if (viewModel.hasPermissions) {
        // Show loading while checking onboarding status
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
        return
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class)
    )
    
    val permissionsLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions: Set<String> ->
        viewModel.onPermissionsResult()
    }
    
    // Helper function to handle permission skip/continue
    fun handleSkipPermissions() {
        viewModel.checkOnboardingStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Health Connect Permissions",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isHealthConnectAvailable) {
            Text(
                text = "We need permission to access your steps and exercise data.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                permissionsLauncher.launch(permissions)
            }) {
                Text("Grant Permissions")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(onClick = { handleSkipPermissions() }) {
                Text("Skip for now")
            }
        } else {
            Text(
                text = "Health Connect is not available on this device.\n\nYou can still use the app, but fitness tracking features will be limited.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { handleSkipPermissions() }) {
                Text("Continue without Health Connect")
            }
        }
    }
}
