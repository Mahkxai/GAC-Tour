package com.mahkxai.gactour.android.data.metadata.di

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mahkxai.gactour.android.data.metadata.MetaDataReader
import com.mahkxai.gactour.android.data.metadata.impl.MetaDataReaderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(app: Application): MetaDataReader {
        return MetaDataReaderImpl(app)
    }
}