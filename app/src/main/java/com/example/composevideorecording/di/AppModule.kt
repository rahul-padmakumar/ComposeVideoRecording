package com.example.composevideorecording.di

import com.example.composevideorecording.utilities.file_checker.FileHelper
import com.example.composevideorecording.utilities.file_checker.FileHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AppModule {
    @Binds
    fun bindFileChecker(fileCheckerImpl: FileHelperImpl): FileHelper
}