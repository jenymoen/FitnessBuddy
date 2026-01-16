package com.fitnessbuddy.ui.workout;

import com.fitnessbuddy.data.location.LocationTracker;
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
public final class WorkoutTrackerViewModel_Factory implements Factory<WorkoutTrackerViewModel> {
  private final Provider<TrainingPlanRepository> trainingPlanRepositoryProvider;

  private final Provider<LocationTracker> locationTrackerProvider;

  private WorkoutTrackerViewModel_Factory(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider) {
    this.trainingPlanRepositoryProvider = trainingPlanRepositoryProvider;
    this.locationTrackerProvider = locationTrackerProvider;
  }

  @Override
  public WorkoutTrackerViewModel get() {
    return newInstance(trainingPlanRepositoryProvider.get(), locationTrackerProvider.get());
  }

  public static WorkoutTrackerViewModel_Factory create(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider) {
    return new WorkoutTrackerViewModel_Factory(trainingPlanRepositoryProvider, locationTrackerProvider);
  }

  public static WorkoutTrackerViewModel newInstance(TrainingPlanRepository trainingPlanRepository,
      LocationTracker locationTracker) {
    return new WorkoutTrackerViewModel(trainingPlanRepository, locationTracker);
  }
}
