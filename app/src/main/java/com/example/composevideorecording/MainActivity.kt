package com.example.composevideorecording

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composevideorecording.ui.screens.RecordingScreen
import com.example.composevideorecording.ui.screens.Route
import com.example.composevideorecording.ui.screens.player.PlayerScreen
import com.example.composevideorecording.ui.theme.ComposeVideoRecordingTheme
import com.example.composevideorecording.utilities.CAMERA_PERMISSION
import com.example.composevideorecording.utilities.isPermissionGranted
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (isPermissionGranted(CAMERA_PERMISSION).not()) {
            ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                0
            )
        }

        setContent {
            ComposeVideoRecordingTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Route.RECORDING_SCREEN.routeName) {
                    composable(Route.RECORDING_SCREEN.routeName){
                        RecordingScreen{
                            navController.navigate(Route.PLAYER_SCREEN.routeName)
                        }
                    }
                    composable(Route.PLAYER_SCREEN.routeName){
                        PlayerScreen()
                    }
                }
            }
        }
    }
}
