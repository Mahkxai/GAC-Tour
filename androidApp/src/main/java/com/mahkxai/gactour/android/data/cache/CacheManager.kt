package com.mahkxai.gactour.android.data.cache

import androidx.media3.datasource.cache.CacheDataSource
import java.io.File

interface CacheManager {
    fun initializeCache()

    fun createCacheDataSourceFactory(): CacheDataSource.Factory

    fun releaseCache()

}