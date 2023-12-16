package com.mahkxai.gactour.android.data.cache.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.mahkxai.gactour.android.data.cache.CacheManager
import com.mahkxai.gactour.android.data.cache.impl.CacheManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @UnstableApi
    @Singleton
    @Provides
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager {
        return CacheManagerImpl(context)
    }

}