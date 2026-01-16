package com.fitnessbuddy.di;

import com.google.ai.client.generativeai.GenerativeModel;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class GeminiModule_ProvideGenerativeModelFactory implements Factory<GenerativeModel> {
  @Override
  public GenerativeModel get() {
    return provideGenerativeModel();
  }

  public static GeminiModule_ProvideGenerativeModelFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GenerativeModel provideGenerativeModel() {
    return Preconditions.checkNotNullFromProvides(GeminiModule.INSTANCE.provideGenerativeModel());
  }

  private static final class InstanceHolder {
    static final GeminiModule_ProvideGenerativeModelFactory INSTANCE = new GeminiModule_ProvideGenerativeModelFactory();
  }
}
