package com.mahkxai.gactour.android.data.local

import androidx.media3.datasource.cache.CacheDataSource

interface PlayerCacheManager {
    fun initializeCache()

    fun createCacheDataSourceFactory(): CacheDataSource.Factory

    fun releaseCache()

}