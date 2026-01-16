package com.fitnessbuddy.data.healthconnect;

import androidx.health.connect.client.HealthConnectClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class HealthConnectManager_Factory implements Factory<HealthConnectManager> {
  private final Provider<HealthConnectClient> healthConnectClientProvider;

  private HealthConnectManager_Factory(Provider<HealthConnectClient> healthConnectClientProvider) {
    this.healthConnectClientProvider = healthConnectClientProvider;
  }

  @Override
  public HealthConnectManager get() {
    return newInstance(healthConnectClientProvider.get());
  }

  public static HealthConnectManager_Factory create(
      Provider<HealthConnectClient> healthConnectClientProvider) {
    return new HealthConnectManager_Factory(healthConnectClientProvider);
  }

  public static HealthConnectManager newInstance(HealthConnectClient healthConnectClient) {
    return new HealthConnectManager(healthConnectClient);
  }
}
