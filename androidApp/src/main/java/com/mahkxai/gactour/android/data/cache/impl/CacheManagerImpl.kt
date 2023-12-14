package com.mahkxai.gactour.android.data.cache.impl

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.mahkxai.gactour.android.data.cache.CacheManager
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class CacheManagerImpl @Inject constructor(private val context: Context) : CacheManager {
    private lateinit var cache: Cache

    override fun initializeCache() {
        val cacheDir = File(context.cacheDir, "video_cache")
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(500 * 1024 * 1024) // 500 MB
        cache = SimpleCache(cacheDir, cacheEvictor, databaseProvider)
    }

    override fun createCacheDataSourceFactory(): CacheDataSource.Factory {
        return CacheDataSource.Factory().apply {
            cache?.let { setCache(it) }
            setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }

    override fun releaseCache() {
        if (::cache.isInitialized) {
            cache.release()
        }
    }
}