package com.example.composevideorecording.di

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Since ViewModelComponent can be treated as a child of Singleton component application is available in viewmodel
 */
@Module
@InstallIn(ViewModelComponent::class)
class PlayerModule {
    @Provides
    fun providePlayer(app: Application): Player = ExoPlayer.Builder(app).build()
}