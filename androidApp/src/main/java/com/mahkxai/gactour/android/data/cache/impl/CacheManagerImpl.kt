package com.mahkxai.gactour.android.data.cache.impl

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.mahkxai.gactour.android.data.cache.CacheManager
import com.mapbox.maps.logD
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class CacheManagerImpl @Inject constructor(private val context: Context) : CacheManager {
    private lateinit var cache: Cache

    override fun initializeCache() {
        val cacheDir = File(context.cacheDir, VIDEO_CACHE_DIR)
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(VIDEO_CACHE_SIZE)
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context) // SQLite

        cache = SimpleCache(cacheDir, cacheEvictor, databaseProvider)
    }

    override fun createCacheDataSourceFactory(): CacheDataSource.Factory {
        val cacheSink = CacheDataSink.Factory().setCache(cache)
        val upstreamFactory = DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory())
        val downStreamFactory = FileDataSource.Factory()

        return CacheDataSource.Factory()
            .setCache(cache)
            .setCacheWriteDataSinkFactory(cacheSink)
            .setCacheReadDataSourceFactory(downStreamFactory)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)


        // Previous Faulty Implementation
        // return CacheDataSource.Factory().apply {
        //     cache?.let { setCache(it) }
        //     setUpstreamDataSourceFactory(upstreamFactory)
        //     setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        // }
    }

    override fun releaseCache() {
        if (::cache.isInitialized) {
            cache.release()
        }
    }

    companion object {
        private const val VIDEO_CACHE_DIR = "video_cache"
        private const val VIDEO_CACHE_SIZE = 500L * (1024 * 1024) // 500 MB
    }

}