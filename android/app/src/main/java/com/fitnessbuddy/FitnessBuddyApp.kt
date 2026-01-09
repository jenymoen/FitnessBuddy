package com.fitnessbuddy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitnessbuddy.ui.auth.LoginScreen
import com.fitnessbuddy.ui.home.HomeScreen

@Composable
fun FitnessBuddyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
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
                onPermissionsGranted = {
                    navController.navigate("home") {
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToPlan = { navController.navigate("workout_plan") }
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
