package com.fitnessbuddy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitnessbuddy.ui.auth.LoginScreen
import com.fitnessbuddy.ui.dashboard.DashboardScreen
import com.fitnessbuddy.ui.home.HomeScreen
import com.fitnessbuddy.ui.onboarding.OnboardingScreen
import com.fitnessbuddy.ui.trainingplan.ProgramOverviewScreen
import com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel
import com.fitnessbuddy.ui.trainingplan.WeekDetailScreen
import com.fitnessbuddy.ui.trainingplan.WorkoutDetailScreen
import com.fitnessbuddy.ui.workout.WorkoutTrackerScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FitnessBuddyApp() {
    val navController = rememberNavController()
    
    // Check if user is already logged in
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) "permissions" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("permissions") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("permissions") {
            com.fitnessbuddy.ui.permissions.PermissionScreen(
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("permissions") { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    // After onboarding, go to generate route which creates new plan
                    navController.navigate("generate_plan") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
        )
        }
        
        // Generate Plan - creates new plan from onboarding data (only used after onboarding)
        composable("generate_plan") {
            val viewModel: TrainingPlanViewModel = hiltViewModel()
            
            LaunchedEffect(Unit) {
                if (viewModel.uiState.value.trainingPlan == null && !viewModel.uiState.value.isGenerating) {
                    viewModel.generatePlanFromSavedData()
                }
            }
            
            ProgramOverviewScreen(
                viewModel = viewModel,
                onBack = { 
                    navController.navigate("dashboard") {
                        popUpTo("generate_plan") { inclusive = true }
                    }
                },
                onWeekClick = { weekNumber ->
                    navController.navigate("week_detail/$weekNumber")
                }
            )
        }
        
        // Training Plan Overview - loads saved plan from Firestore (used from dashboard)
        composable("training_plan") {
            val viewModel: TrainingPlanViewModel = hiltViewModel()
            
            LaunchedEffect(Unit) {
                if (viewModel.uiState.value.trainingPlan == null && !viewModel.uiState.value.isGenerating) {
                    // Load saved plan from Firestore
                    viewModel.loadSavedPlan()
                }
            }
            
            ProgramOverviewScreen(
                viewModel = viewModel,
                onBack = { 
                    navController.navigate("dashboard") {
                        popUpTo("training_plan") { inclusive = true }
                    }
                },
                onWeekClick = { weekNumber ->
                    navController.navigate("week_detail/$weekNumber")
                }
            )
        }
        
        // Week Detail
        composable("week_detail/{weekNumber}") { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getString("weekNumber")?.toIntOrNull() ?: 1
            val viewModel: TrainingPlanViewModel = hiltViewModel()
            
            // Load saved plan if not loaded yet
            LaunchedEffect(Unit) {
                if (viewModel.uiState.value.trainingPlan == null && !viewModel.uiState.value.isGenerating) {
                    viewModel.loadSavedPlan()
                }
            }
            
            WeekDetailScreen(
                weekNumber = weekNumber,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewWorkout = { dayOfWeek ->
                    navController.navigate("workout_detail/$weekNumber/$dayOfWeek")
                },
                onStartWorkout = { dayOfWeek ->
                    navController.navigate("workout_tracker/$weekNumber/$dayOfWeek")
                }
            )
        }
        
        // Workout Detail
        composable("workout_detail/{weekNumber}/{dayOfWeek}") { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getString("weekNumber")?.toIntOrNull() ?: 1
            val dayOfWeek = backStackEntry.arguments?.getString("dayOfWeek") ?: ""
            val viewModel: TrainingPlanViewModel = hiltViewModel()
            
            // Load saved plan if not loaded yet
            LaunchedEffect(Unit) {
                if (viewModel.uiState.value.trainingPlan == null && !viewModel.uiState.value.isGenerating) {
                    viewModel.loadSavedPlan()
                }
            }
            
            WorkoutDetailScreen(
                weekNumber = weekNumber,
                dayOfWeek = dayOfWeek,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onStartWorkout = {
                    navController.navigate("workout_tracker/$weekNumber/$dayOfWeek")
                }
            )
        }
        
        // Workout Tracker - Active workout with GPS/timer
        composable("workout_tracker/{weekNumber}/{dayOfWeek}") { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getString("weekNumber")?.toIntOrNull() ?: 1
            val dayOfWeek = backStackEntry.arguments?.getString("dayOfWeek") ?: ""
            
            WorkoutTrackerScreen(
                weekNumber = weekNumber,
                dayOfWeek = dayOfWeek,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Dashboard - Main screen for returning users
        composable("dashboard") {
            DashboardScreen(
                onNavigateToPlan = { navController.navigate("training_plan") },
                onStartSession = { /* Start workout */ },
                onNavigateToInsights = { /* Insights screen */ },
                onNavigateToRace = { navController.navigate("training_plan") },
                onNavigateToProfile = { /* Profile screen */ }
            )
        }
        
        composable("home") {
            HomeScreen(
                onNavigateToPlan = { navController.navigate("training_plan") }
            )
        }
        composable("workout_plan") {
            com.fitnessbuddy.ui.workout.WorkoutPlanScreen(
                onWorkoutClick = { workoutId ->
                    navController.navigate("active_workout/$workoutId")
                }
            )
        }
        composable("active_workout/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
            com.fitnessbuddy.ui.workout.ActiveWorkoutScreen(
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
