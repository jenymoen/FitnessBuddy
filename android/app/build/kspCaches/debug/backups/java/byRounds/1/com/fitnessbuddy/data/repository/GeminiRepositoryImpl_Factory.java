package com.fitnessbuddy.data.repository;

import com.google.ai.client.generativeai.GenerativeModel;
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
public final class GeminiRepositoryImpl_Factory implements Factory<GeminiRepositoryImpl> {
  private final Provider<GenerativeModel> generativeModelProvider;

  private GeminiRepositoryImpl_Factory(Provider<GenerativeModel> generativeModelProvider) {
    this.generativeModelProvider = generativeModelProvider;
  }

  @Override
  public GeminiRepositoryImpl get() {
    return newInstance(generativeModelProvider.get());
  }

  public static GeminiRepositoryImpl_Factory create(
      Provider<GenerativeModel> generativeModelProvider) {
    return new GeminiRepositoryImpl_Factory(generativeModelProvider);
  }

  public static GeminiRepositoryImpl newInstance(GenerativeModel generativeModel) {
    return new GeminiRepositoryImpl(generativeModel);
  }
}
