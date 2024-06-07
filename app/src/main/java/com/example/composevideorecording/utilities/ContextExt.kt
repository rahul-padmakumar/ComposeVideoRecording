package com.example.composevideorecording.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(
        this,
        it
    ) == PackageManager.PERMISSION_GRANTED
}