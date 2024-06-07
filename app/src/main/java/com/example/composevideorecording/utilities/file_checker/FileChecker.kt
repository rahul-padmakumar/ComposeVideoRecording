package com.example.composevideorecording.utilities.file_checker

import android.content.Context
import android.net.Uri

interface FileChecker {

    fun getFileUri(fileName: String): Uri
    fun doFileExists(fileName: String): Boolean
}