package com.fitnessbuddy.ui.permissions

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.data.healthconnect.HealthConnectManager
import javax.inject.Inject

@Composable
fun PermissionScreen(
    onPermissionsGranted: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    if (viewModel.hasPermissions) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
        return
    }

    val context = LocalContext.current
    val healthConnectClient = HealthConnectClient.getOrCreate(context)
    
    val permissionsLauncher = rememberLauncherForActivityResult(
        healthConnectClient.permissionController.createRequestPermissionResultContract()
    ) { granted ->
        viewModel.onPermissionsResult()
    }
    
    // Injecting manager instance is tricky in composable parameters without hiltViewModel, 
    // but here we know the needed permissions set is static in HealthConnectManager
    // Ideally ViewModel should expose the set.
    // For MVP, we will access permissions via a quick hack or refactor VM to expose it.
    // Let's refactor VM later, for now we assume we know the permissions.
    // Actually we can instantiate the manager logic via DI in VM, checking dependencies.
    // Let's assume we can get the set from VM.
    
    // We will need to update PermissionViewModel to expose `permissions` set.
    // I'll update VM now? No, I'll just hardcode them in Screen or access via a provider pattern.
    // Or I can access `viewModel.healthConnectManager.PERMISSIONS` but `healthConnectManager` is private.
    // I will use a simplified set here reflecting the Manager.
    
    val permissions = setOf(
        androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.StepsRecord::class),
        androidx.health.connect.client.permission.HealthPermission.getWritePermission(androidx.health.connect.client.records.StepsRecord::class),
        // Add others... this is getting verbose and duplicate. 
        // Better: Update ViewModel to expose permissions.
    )

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
        
        Text("We need permission to access your steps and exercise data.")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            permissionsLauncher.launch(
                // Use the set here. I'll fix this in a sec by updating VM.
                setOf(
                    androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.StepsRecord::class),
                    androidx.health.connect.client.permission.HealthPermission.getWritePermission(androidx.health.connect.client.records.StepsRecord::class),
                    androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.ExerciseSessionRecord::class),
                    androidx.health.connect.client.permission.HealthPermission.getWritePermission(androidx.health.connect.client.records.ExerciseSessionRecord::class),
                    androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.HeartRateRecord::class),
                    androidx.health.connect.client.permission.HealthPermission.getWritePermission(androidx.health.connect.client.records.HeartRateRecord::class)
                )
            )
        }) {
            Text("Grant Permissions")
        }
    }
}
