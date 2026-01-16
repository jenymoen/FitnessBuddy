package com.fitnessbuddy.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class OnboardingDataHolder_Factory implements Factory<OnboardingDataHolder> {
  @Override
  public OnboardingDataHolder get() {
    return newInstance();
  }

  public static OnboardingDataHolder_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OnboardingDataHolder newInstance() {
    return new OnboardingDataHolder();
  }

  private static final class InstanceHolder {
    static final OnboardingDataHolder_Factory INSTANCE = new OnboardingDataHolder_Factory();
  }
}
