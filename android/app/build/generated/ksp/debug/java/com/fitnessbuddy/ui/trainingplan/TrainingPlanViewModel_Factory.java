package com.fitnessbuddy.ui.trainingplan;

import com.fitnessbuddy.data.repository.OnboardingDataHolder;
import com.fitnessbuddy.domain.repository.GeminiRepository;
import com.fitnessbuddy.domain.repository.TrainingPlanRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class TrainingPlanViewModel_Factory implements Factory<TrainingPlanViewModel> {
  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<TrainingPlanRepository> trainingPlanRepositoryProvider;

  private final Provider<OnboardingDataHolder> onboardingDataHolderProvider;

  private TrainingPlanViewModel_Factory(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<OnboardingDataHolder> onboardingDataHolderProvider) {
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.trainingPlanRepositoryProvider = trainingPlanRepositoryProvider;
    this.onboardingDataHolderProvider = onboardingDataHolderProvider;
  }

  @Override
  public TrainingPlanViewModel get() {
    return newInstance(geminiRepositoryProvider.get(), trainingPlanRepositoryProvider.get(), onboardingDataHolderProvider.get());
  }

  public static TrainingPlanViewModel_Factory create(
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<OnboardingDataHolder> onboardingDataHolderProvider) {
    return new TrainingPlanViewModel_Factory(geminiRepositoryProvider, trainingPlanRepositoryProvider, onboardingDataHolderProvider);
  }

  public static TrainingPlanViewModel newInstance(GeminiRepository geminiRepository,
      TrainingPlanRepository trainingPlanRepository, OnboardingDataHolder onboardingDataHolder) {
    return new TrainingPlanViewModel(geminiRepository, trainingPlanRepository, onboardingDataHolder);
  }
}
