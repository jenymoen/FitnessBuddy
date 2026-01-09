package com.fitnessbuddy.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitnessbuddy.domain.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    workoutId: String,
    onNavigateBack: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }
    
    val workout = viewModel.activeWorkout
    val isLoading = viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.title ?: "Workout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (workout != null) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = workout.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (workout.isRestDay) {
                        item {
                            Text(
                                text = "Rest Day - Enjoy your recovery!",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    } else {
                        items(workout.exercises) { exercise ->
                            ExerciseItem(exercise)
                        }
                        item {
                            Button(
                                onClick = { /* Mark complete logic */ onNavigateBack() },
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text("Complete Workout")
                            }
                        }
                    }
                }
            } else {
                Text("Workout not found", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${exercise.sets} sets x ${exercise.reps} reps")
                if (exercise.restSeconds > 0) {
                     Text("Rest: ${exercise.restSeconds}s")
                }
            }
            if (exercise.notes.isNotBlank()) {
                Text(
                    text = "Note: ${exercise.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
