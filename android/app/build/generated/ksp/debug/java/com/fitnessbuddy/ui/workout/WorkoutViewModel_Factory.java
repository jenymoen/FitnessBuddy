package com.fitnessbuddy.ui.workout;

import com.fitnessbuddy.domain.repository.WorkoutRepository;
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
public final class WorkoutViewModel_Factory implements Factory<WorkoutViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private WorkoutViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
  }

  @Override
  public WorkoutViewModel get() {
    return newInstance(workoutRepositoryProvider.get());
  }

  public static WorkoutViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider) {
    return new WorkoutViewModel_Factory(workoutRepositoryProvider);
  }

  public static WorkoutViewModel newInstance(WorkoutRepository workoutRepository) {
    return new WorkoutViewModel(workoutRepository);
  }
}
