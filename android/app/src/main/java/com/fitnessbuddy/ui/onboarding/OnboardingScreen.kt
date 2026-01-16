package com.fitnessbuddy.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    when (viewModel.currentStep) {
        1 -> OnboardingStep1Screen(
            viewModel = viewModel,
            onBack = onBack,
            onNext = { /* Step incremented in screen */ }
        )
        2 -> OnboardingStep2Screen(
            viewModel = viewModel,
            onBack = { viewModel.previousStep() },
            onNext = { /* Step incremented in Step2Screen */ }
        )
        3 -> OnboardingStep3Screen(
            viewModel = viewModel,
            onBack = { viewModel.previousStep() },
            onNext = { /* Step incremented in Step3Screen */ }
        )
        4 -> OnboardingStep4Screen(
            viewModel = viewModel,
            onGeneratePlan = {
                // Save onboarding data and navigate to plan screen
                viewModel.saveOnboardingData()
                onComplete()
            }
        )
        else -> {
            // Fallback - complete onboarding
            onComplete()
        }
    }
}

