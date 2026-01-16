package com.fitnessbuddy;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = FitnessBuddyApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
@Generated("dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator")
public interface FitnessBuddyApplication_GeneratedInjector {
  void injectFitnessBuddyApplication(FitnessBuddyApplication fitnessBuddyApplication);
}
