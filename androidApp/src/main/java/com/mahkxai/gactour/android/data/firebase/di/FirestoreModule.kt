package com.mahkxai.gactour.android.data.firebase.di

import com.mahkxai.gactour.android.data.firebase.FirebaseStorageService
import com.mahkxai.gactour.android.data.firebase.FirestoreService
import com.mahkxai.gactour.android.data.firebase.impl.FirebaseStorageServiceImpl
import com.mahkxai.gactour.android.data.firebase.impl.FirestoreServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class FirestoreModule {
    @Binds
    abstract fun provideFirestoreService(impl: FirestoreServiceImpl): FirestoreService

    @Binds
    abstract fun provideFirebaseStorageService(impl: FirebaseStorageServiceImpl): FirebaseStorageService
}