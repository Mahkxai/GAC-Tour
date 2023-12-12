package com.mahkxai.gactour.android.data.location.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.mahkxai.gactour.android.data.location.LocationService
import com.mahkxai.gactour.android.data.location.impl.LocationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
   /* @Singleton
    @Provides
    fun provideFusedLocationManager(
        @ApplicationContext context: Context
    ): LocationServiceImpl = LocationServiceImpl(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
*/
    @Singleton
    @Provides
    fun provideLocationServiceClient(
        @ApplicationContext context: Context
    ): LocationService = LocationServiceImpl(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}