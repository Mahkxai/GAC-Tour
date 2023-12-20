package com.mahkxai.gactour.android.data.local.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.mahkxai.gactour.android.data.local.PlayerCacheManager
import com.mahkxai.gactour.android.data.local.impl.PlayerCacheManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    // @UnstableApi
    // @Singleton
    // @Provides
    // fun provideCacheManager(@ApplicationContext context: Context): PlayerCacheManager {
    //     return PlayerCacheManagerImpl(context)
    // }

}