package com.fitnessbuddy.ui.home;

import com.fitnessbuddy.data.healthconnect.HealthConnectManager;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  private HomeViewModel_Factory(Provider<HealthConnectManager> healthConnectManagerProvider) {
    this.healthConnectManagerProvider = healthConnectManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(healthConnectManagerProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    return new HomeViewModel_Factory(healthConnectManagerProvider);
  }

  public static HomeViewModel newInstance(HealthConnectManager healthConnectManager) {
    return new HomeViewModel(healthConnectManager);
  }
}
