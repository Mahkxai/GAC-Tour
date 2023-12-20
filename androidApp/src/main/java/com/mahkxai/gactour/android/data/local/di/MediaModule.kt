package com.mahkxai.gactour.android.data.local.di

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.mahkxai.gactour.android.data.local.MetaDataReader
import com.mahkxai.gactour.android.data.local.PlayerCacheManager
import com.mahkxai.gactour.android.data.local.impl.MetaDataReaderImpl
import com.mahkxai.gactour.android.data.local.impl.PlayerCacheManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MediaModule {

    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): Player {
        return ExoPlayer.Builder(app)
            .build()
    }

    @OptIn(UnstableApi::class)
    @Provides
    @ViewModelScoped
    fun provideCacheManager(app: Application): PlayerCacheManager {
        return PlayerCacheManagerImpl(app)
    }

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(app: Application): MetaDataReader {
        return MetaDataReaderImpl(app)
    }
}