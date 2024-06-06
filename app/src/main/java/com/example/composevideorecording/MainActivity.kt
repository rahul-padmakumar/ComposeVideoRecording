package com.example.composevideorecording

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.composevideorecording.ui.components.CameraPreview
import com.example.composevideorecording.ui.theme.ComposeVideoRecordingTheme
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private var recording = mutableStateOf<Recording?>(null)
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (isPermissionGranted(this).not()) {
            ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                0
            )
        }

        setContent {
            ComposeVideoRecordingTheme {

                val context = LocalContext.current
                val cameraController = remember {
                    LifecycleCameraController(
                        context
                    ).apply {
                        setEnabledUseCases(
                            CameraController.VIDEO_CAPTURE
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(
                            cameraController = cameraController,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(onClick = {
                            cameraController.cameraSelector =
                                if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else CameraSelector.DEFAULT_FRONT_CAMERA
                        }, modifier = Modifier.align(Alignment.TopStart)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Camera switcher"
                            )
                        }
                        IconButton(onClick = {
                             startRecording(context, cameraController)
                        }, modifier = Modifier.align(Alignment.BottomCenter)) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Video recording"
                            )
                        }
                        recording.value?.let {
                            IconButton(onClick = {
                                if(isPaused){
                                    recording.value?.resume()
                                } else {
                                    recording.value?.pause()
                                }
                            }, modifier = Modifier.align(Alignment.BottomStart)) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Pause recording"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        val CAMERA_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private fun isPermissionGranted(context: Context) = CAMERA_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording(context: Context, cameraController: LifecycleCameraController) {
        if (recording.value != null){
            recording.value?.close()
            recording.value = null
            return
        }
        recording.value = cameraController.startRecording(
            FileOutputOptions.Builder(File(context.filesDir, "recordng.mp4")).build(),
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(context)
        ) { event ->
            when(event){
                is VideoRecordEvent.Finalize -> {
                    if(event.hasError()){
                        recording.value?.close()
                        recording.value = null
                        Toast.makeText(context, "Video recording failed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Video recording succeeded", Toast.LENGTH_SHORT).show()
                    }
                }
                is VideoRecordEvent.Start -> {
                    Toast.makeText(context, "Video recording started", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Pause -> {
                    isPaused = true
                    Toast.makeText(context, "Video recording paused", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Resume -> {
                    isPaused = false
                    Toast.makeText(context, "Video recording resumed", Toast.LENGTH_SHORT).show()

                }
                is VideoRecordEvent.Status -> {
                    println("Recorded duration ${TimeUnit.NANOSECONDS.toMinutes(event.recordingStats.recordedDurationNanos)}")
                    println("Recorded bytes ${event.recordingStats.numBytesRecorded}")
                }
            }
        }
    }
}
