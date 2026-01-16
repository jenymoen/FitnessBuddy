package com.fitnessbuddy.ui.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<TrainingPlanRepository> trainingPlanRepositoryProvider;

  private DashboardViewModel_Factory(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider) {
    this.trainingPlanRepositoryProvider = trainingPlanRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(trainingPlanRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider) {
    return new DashboardViewModel_Factory(trainingPlanRepositoryProvider);
  }

  public static DashboardViewModel newInstance(TrainingPlanRepository trainingPlanRepository) {
    return new DashboardViewModel(trainingPlanRepository);
  }
}
