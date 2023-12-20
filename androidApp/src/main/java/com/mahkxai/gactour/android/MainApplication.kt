package com.mahkxai.gactour.android

import android.app.Application
import com.mahkxai.gactour.android.data.local.PlayerCacheManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {}