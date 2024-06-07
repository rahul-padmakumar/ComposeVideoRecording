package com.example.composevideorecording.utilities.file_checker

import android.app.Application
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileCheckerImpl @Inject constructor(@ApplicationContext private val context: Context): FileChecker {
    override fun getFileUri(fileName: String): Uri =
        Uri.fromFile(File(context.filesDir, fileName))

    override fun doFileExists(fileName: String): Boolean =
        File(context.filesDir, fileName).exists()
}