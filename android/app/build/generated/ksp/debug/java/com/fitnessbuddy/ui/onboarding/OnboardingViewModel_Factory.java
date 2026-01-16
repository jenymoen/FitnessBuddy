package com.fitnessbuddy.ui.onboarding;

import com.fitnessbuddy.data.repository.OnboardingDataHolder;
import com.fitnessbuddy.domain.repository.GeminiRepository;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<OnboardingDataHolder> onboardingDataHolderProvider;

  private OnboardingViewModel_Factory(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<OnboardingDataHolder> onboardingDataHolderProvider) {
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.onboardingDataHolderProvider = onboardingDataHolderProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(geminiRepositoryProvider.get(), onboardingDataHolderProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<OnboardingDataHolder> onboardingDataHolderProvider) {
    return new OnboardingViewModel_Factory(geminiRepositoryProvider, onboardingDataHolderProvider);
  }

  public static OnboardingViewModel newInstance(GeminiRepository geminiRepository,
      OnboardingDataHolder onboardingDataHolder) {
    return new OnboardingViewModel(geminiRepository, onboardingDataHolder);
  }
}
