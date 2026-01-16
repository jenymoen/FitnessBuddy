package com.fitnessbuddy;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.HealthConnectClient;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.fitnessbuddy.data.healthconnect.HealthConnectManager;
import com.fitnessbuddy.data.location.LocationTracker;
import com.fitnessbuddy.data.repository.AuthRepositoryImpl;
import com.fitnessbuddy.data.repository.GeminiRepositoryImpl;
import com.fitnessbuddy.data.repository.OnboardingDataHolder;
import com.fitnessbuddy.data.repository.TrainingPlanRepositoryImpl;
import com.fitnessbuddy.data.repository.WorkoutRepositoryImpl;
import com.fitnessbuddy.di.FirebaseModule_ProvideFirebaseAuthFactory;
import com.fitnessbuddy.di.FirebaseModule_ProvideFirebaseFirestoreFactory;
import com.fitnessbuddy.di.GeminiModule_ProvideGenerativeModelFactory;
import com.fitnessbuddy.di.HealthConnectModule;
import com.fitnessbuddy.ui.auth.LoginViewModel;
import com.fitnessbuddy.ui.auth.LoginViewModel_HiltModules;
import com.fitnessbuddy.ui.auth.LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.auth.LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.dashboard.DashboardViewModel;
import com.fitnessbuddy.ui.dashboard.DashboardViewModel_HiltModules;
import com.fitnessbuddy.ui.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.home.HomeViewModel;
import com.fitnessbuddy.ui.home.HomeViewModel_HiltModules;
import com.fitnessbuddy.ui.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.onboarding.OnboardingViewModel;
import com.fitnessbuddy.ui.onboarding.OnboardingViewModel_HiltModules;
import com.fitnessbuddy.ui.onboarding.OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.onboarding.OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.permissions.PermissionViewModel;
import com.fitnessbuddy.ui.permissions.PermissionViewModel_HiltModules;
import com.fitnessbuddy.ui.permissions.PermissionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.permissions.PermissionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel;
import com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel_HiltModules;
import com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.workout.WorkoutTrackerViewModel;
import com.fitnessbuddy.ui.workout.WorkoutTrackerViewModel_HiltModules;
import com.fitnessbuddy.ui.workout.WorkoutTrackerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.workout.WorkoutTrackerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.fitnessbuddy.ui.workout.WorkoutViewModel;
import com.fitnessbuddy.ui.workout.WorkoutViewModel_HiltModules;
import com.fitnessbuddy.ui.workout.WorkoutViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.fitnessbuddy.ui.workout.WorkoutViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerFitnessBuddyApplication_HiltComponents_SingletonC {
  private DaggerFitnessBuddyApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public FitnessBuddyApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements FitnessBuddyApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements FitnessBuddyApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements FitnessBuddyApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements FitnessBuddyApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements FitnessBuddyApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements FitnessBuddyApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements FitnessBuddyApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public FitnessBuddyApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends FitnessBuddyApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends FitnessBuddyApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends FitnessBuddyApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends FitnessBuddyApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(8).put(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide()).put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide()).put(LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LoginViewModel_HiltModules.KeyModule.provide()).put(OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, OnboardingViewModel_HiltModules.KeyModule.provide()).put(PermissionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PermissionViewModel_HiltModules.KeyModule.provide()).put(TrainingPlanViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TrainingPlanViewModel_HiltModules.KeyModule.provide()).put(WorkoutTrackerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WorkoutTrackerViewModel_HiltModules.KeyModule.provide()).put(WorkoutViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WorkoutViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends FitnessBuddyApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<DashboardViewModel> dashboardViewModelProvider;

    Provider<HomeViewModel> homeViewModelProvider;

    Provider<LoginViewModel> loginViewModelProvider;

    Provider<OnboardingViewModel> onboardingViewModelProvider;

    Provider<PermissionViewModel> permissionViewModelProvider;

    Provider<TrainingPlanViewModel> trainingPlanViewModelProvider;

    Provider<WorkoutTrackerViewModel> workoutTrackerViewModelProvider;

    Provider<WorkoutViewModel> workoutViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.permissionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.trainingPlanViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.workoutTrackerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.workoutViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(8).put(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (dashboardViewModelProvider))).put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (homeViewModelProvider))).put(LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (loginViewModelProvider))).put(OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (onboardingViewModelProvider))).put(PermissionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (permissionViewModelProvider))).put(TrainingPlanViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (trainingPlanViewModelProvider))).put(WorkoutTrackerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (workoutTrackerViewModelProvider))).put(WorkoutViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (workoutViewModelProvider))).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.fitnessbuddy.ui.dashboard.DashboardViewModel
          return (T) new DashboardViewModel(singletonCImpl.trainingPlanRepositoryImplProvider.get());

          case 1: // com.fitnessbuddy.ui.home.HomeViewModel
          return (T) new HomeViewModel(singletonCImpl.healthConnectManagerProvider.get());

          case 2: // com.fitnessbuddy.ui.auth.LoginViewModel
          return (T) new LoginViewModel(singletonCImpl.authRepositoryImplProvider.get());

          case 3: // com.fitnessbuddy.ui.onboarding.OnboardingViewModel
          return (T) new OnboardingViewModel(singletonCImpl.geminiRepositoryImplProvider.get(), singletonCImpl.onboardingDataHolderProvider.get());

          case 4: // com.fitnessbuddy.ui.permissions.PermissionViewModel
          return (T) new PermissionViewModel(singletonCImpl.healthConnectManagerProvider.get(), singletonCImpl.trainingPlanRepositoryImplProvider.get());

          case 5: // com.fitnessbuddy.ui.trainingplan.TrainingPlanViewModel
          return (T) new TrainingPlanViewModel(singletonCImpl.geminiRepositoryImplProvider.get(), singletonCImpl.trainingPlanRepositoryImplProvider.get(), singletonCImpl.onboardingDataHolderProvider.get());

          case 6: // com.fitnessbuddy.ui.workout.WorkoutTrackerViewModel
          return (T) new WorkoutTrackerViewModel(singletonCImpl.trainingPlanRepositoryImplProvider.get(), singletonCImpl.locationTrackerProvider.get());

          case 7: // com.fitnessbuddy.ui.workout.WorkoutViewModel
          return (T) new WorkoutViewModel(singletonCImpl.workoutRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends FitnessBuddyApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends FitnessBuddyApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends FitnessBuddyApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<FirebaseFirestore> provideFirebaseFirestoreProvider;

    Provider<FirebaseAuth> provideFirebaseAuthProvider;

    Provider<TrainingPlanRepositoryImpl> trainingPlanRepositoryImplProvider;

    Provider<HealthConnectClient> provideHealthConnectClientProvider;

    Provider<HealthConnectManager> healthConnectManagerProvider;

    Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    Provider<GenerativeModel> provideGenerativeModelProvider;

    Provider<GeminiRepositoryImpl> geminiRepositoryImplProvider;

    Provider<OnboardingDataHolder> onboardingDataHolderProvider;

    Provider<LocationTracker> locationTrackerProvider;

    Provider<WorkoutRepositoryImpl> workoutRepositoryImplProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideFirebaseFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 1));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 2));
      this.trainingPlanRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<TrainingPlanRepositoryImpl>(singletonCImpl, 0));
      this.provideHealthConnectClientProvider = DoubleCheck.provider(new SwitchingProvider<HealthConnectClient>(singletonCImpl, 4));
      this.healthConnectManagerProvider = DoubleCheck.provider(new SwitchingProvider<HealthConnectManager>(singletonCImpl, 3));
      this.authRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepositoryImpl>(singletonCImpl, 5));
      this.provideGenerativeModelProvider = DoubleCheck.provider(new SwitchingProvider<GenerativeModel>(singletonCImpl, 7));
      this.geminiRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<GeminiRepositoryImpl>(singletonCImpl, 6));
      this.onboardingDataHolderProvider = DoubleCheck.provider(new SwitchingProvider<OnboardingDataHolder>(singletonCImpl, 8));
      this.locationTrackerProvider = DoubleCheck.provider(new SwitchingProvider<LocationTracker>(singletonCImpl, 9));
      this.workoutRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkoutRepositoryImpl>(singletonCImpl, 10));
    }

    @Override
    public void injectFitnessBuddyApplication(FitnessBuddyApplication fitnessBuddyApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.fitnessbuddy.data.repository.TrainingPlanRepositoryImpl
          return (T) new TrainingPlanRepositoryImpl(singletonCImpl.provideFirebaseFirestoreProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get());

          case 1: // com.google.firebase.firestore.FirebaseFirestore
          return (T) FirebaseModule_ProvideFirebaseFirestoreFactory.provideFirebaseFirestore();

          case 2: // com.google.firebase.auth.FirebaseAuth
          return (T) FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 3: // com.fitnessbuddy.data.healthconnect.HealthConnectManager
          return (T) new HealthConnectManager(singletonCImpl.provideHealthConnectClientProvider.get());

          case 4: // androidx.health.connect.client.HealthConnectClient
          return (T) HealthConnectModule.INSTANCE.provideHealthConnectClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.fitnessbuddy.data.repository.AuthRepositoryImpl
          return (T) new AuthRepositoryImpl(singletonCImpl.provideFirebaseAuthProvider.get());

          case 6: // com.fitnessbuddy.data.repository.GeminiRepositoryImpl
          return (T) new GeminiRepositoryImpl(singletonCImpl.provideGenerativeModelProvider.get());

          case 7: // com.google.ai.client.generativeai.GenerativeModel
          return (T) GeminiModule_ProvideGenerativeModelFactory.provideGenerativeModel();

          case 8: // com.fitnessbuddy.data.repository.OnboardingDataHolder
          return (T) new OnboardingDataHolder();

          case 9: // com.fitnessbuddy.data.location.LocationTracker
          return (T) new LocationTracker(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.fitnessbuddy.data.repository.WorkoutRepositoryImpl
          return (T) new WorkoutRepositoryImpl();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
