package com.example.gactour.location;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class LocationManager_Factory implements Factory<LocationManager> {
  private final Provider<Context> contextProvider;

  public LocationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocationManager get() {
    return newInstance(contextProvider.get());
  }

  public static LocationManager_Factory create(Provider<Context> contextProvider) {
    return new LocationManager_Factory(contextProvider);
  }

  public static LocationManager newInstance(Context context) {
    return new LocationManager(context);
  }
}
