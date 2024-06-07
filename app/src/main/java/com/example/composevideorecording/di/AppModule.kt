package com.example.composevideorecording.di

import com.example.composevideorecording.utilities.file_checker.FileChecker
import com.example.composevideorecording.utilities.file_checker.FileCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AppModule {
    @Binds
    fun bindFileChecker(fileCheckerImpl: FileCheckerImpl): FileChecker
}