package com.mahkxai.gactour.android

import android.app.Application
import com.mahkxai.gactour.android.data.cache.CacheManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {
    @Inject
    lateinit var cacheManager: CacheManager

    override fun onCreate() {
        super.onCreate()
        cacheManager.initializeCache()
    }

    override fun onTerminate() {
        super.onTerminate()
        cacheManager.releaseCache()
    }
}