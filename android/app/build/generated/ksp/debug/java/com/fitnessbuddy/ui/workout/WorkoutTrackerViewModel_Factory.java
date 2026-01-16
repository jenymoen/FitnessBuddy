package com.fitnessbuddy.ui.workout;

import com.fitnessbuddy.data.location.LocationTracker;
import com.fitnessbuddy.data.sensor.BluetoothHeartRateManager;
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
public final class WorkoutTrackerViewModel_Factory implements Factory<WorkoutTrackerViewModel> {
  private final Provider<TrainingPlanRepository> trainingPlanRepositoryProvider;

  private final Provider<LocationTracker> locationTrackerProvider;

  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<BluetoothHeartRateManager> bluetoothHeartRateManagerProvider;

  private WorkoutTrackerViewModel_Factory(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<BluetoothHeartRateManager> bluetoothHeartRateManagerProvider) {
    this.trainingPlanRepositoryProvider = trainingPlanRepositoryProvider;
    this.locationTrackerProvider = locationTrackerProvider;
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.bluetoothHeartRateManagerProvider = bluetoothHeartRateManagerProvider;
  }

  @Override
  public WorkoutTrackerViewModel get() {
    return newInstance(trainingPlanRepositoryProvider.get(), locationTrackerProvider.get(), geminiRepositoryProvider.get(), bluetoothHeartRateManagerProvider.get());
  }

  public static WorkoutTrackerViewModel_Factory create(
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<BluetoothHeartRateManager> bluetoothHeartRateManagerProvider) {
    return new WorkoutTrackerViewModel_Factory(trainingPlanRepositoryProvider, locationTrackerProvider, geminiRepositoryProvider, bluetoothHeartRateManagerProvider);
  }

  public static WorkoutTrackerViewModel newInstance(TrainingPlanRepository trainingPlanRepository,
      LocationTracker locationTracker, GeminiRepository geminiRepository,
      BluetoothHeartRateManager bluetoothHeartRateManager) {
    return new WorkoutTrackerViewModel(trainingPlanRepository, locationTracker, geminiRepository, bluetoothHeartRateManager);
  }
}
