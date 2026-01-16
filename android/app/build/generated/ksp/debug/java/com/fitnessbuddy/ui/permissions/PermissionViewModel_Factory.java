package com.fitnessbuddy.ui.permissions;

import com.fitnessbuddy.data.healthconnect.HealthConnectManager;
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
public final class PermissionViewModel_Factory implements Factory<PermissionViewModel> {
  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  private final Provider<TrainingPlanRepository> trainingPlanRepositoryProvider;

  private PermissionViewModel_Factory(Provider<HealthConnectManager> healthConnectManagerProvider,
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider) {
    this.healthConnectManagerProvider = healthConnectManagerProvider;
    this.trainingPlanRepositoryProvider = trainingPlanRepositoryProvider;
  }

  @Override
  public PermissionViewModel get() {
    return newInstance(healthConnectManagerProvider.get(), trainingPlanRepositoryProvider.get());
  }

  public static PermissionViewModel_Factory create(
      Provider<HealthConnectManager> healthConnectManagerProvider,
      Provider<TrainingPlanRepository> trainingPlanRepositoryProvider) {
    return new PermissionViewModel_Factory(healthConnectManagerProvider, trainingPlanRepositoryProvider);
  }

  public static PermissionViewModel newInstance(HealthConnectManager healthConnectManager,
      TrainingPlanRepository trainingPlanRepository) {
    return new PermissionViewModel(healthConnectManager, trainingPlanRepository);
  }
}
