package com.fitnessbuddy.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.domain.model.DailyWorkout

@Composable
fun WorkoutPlanScreen(
    onWorkoutClick: (String) -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val plan = viewModel.workoutPlan
    val isLoading = viewModel.isLoading

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (plan != null) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Flatten weeks for simplicity or group them. Assuming 1 week mock.
                plan.weeks.forEach { week ->
                    item {
                        Text(
                            text = "Week ${week.weekNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(week.days) { workout ->
                        WorkoutTypeItem(workout = workout, onClick = { onWorkoutClick(workout.id) })
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No plan found. Please try again.")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simulation Button
        Button(
            onClick = { viewModel.adaptSchedule("w1_d1") }, // Simulating missing the first workout
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simulate Missed Workout (Monday)")
        }
    }
}

@Composable
fun WorkoutTypeItem(workout: DailyWorkout, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (workout.isRestDay) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workout.dayOfWeek,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${workout.durationMinutes} min",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = workout.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (workout.description.isNotBlank()) {
                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
