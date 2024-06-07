package com.example.composevideorecording.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.composevideorecording.ui.components.CameraPreview
import com.example.composevideorecording.utilities.CAMERA_PERMISSION
import com.example.composevideorecording.utilities.FILE_NAME
import com.example.composevideorecording.utilities.isPermissionGranted
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
fun RecordingScreen(
    playRecording: () -> Unit
){
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
    val recordingState = remember {
        mutableStateOf<Recording?>(null)
    }
    var isPaused = remember {
        false
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
                startRecording(context, cameraController, recordingState){
                    isPaused = it
                }
            }, modifier = Modifier.align(Alignment.BottomCenter)) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Video recording"
                )
            }
            IconButton(onClick = playRecording, modifier = Modifier.align(Alignment.BottomEnd)) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Play recording"
                )
            }
            recordingState.value?.let {
                IconButton(onClick = {
                    if(isPaused){
                        it.resume()
                    } else {
                        it.pause()
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

@SuppressLint("MissingPermission")
private fun startRecording(
    context: Context,
    cameraController: LifecycleCameraController,
    recording: MutableState<Recording?>,
    isPaused: (Boolean) -> Unit
) {
    if (recording.value != null){
        recording.value?.close()
        recording.value = null
        return
    }
    if(!context.isPermissionGranted(CAMERA_PERMISSION)){
        return
    }
    recording.value = cameraController.startRecording(
        FileOutputOptions.Builder(File(context.filesDir, FILE_NAME)).build(),
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
                isPaused(true)
                Toast.makeText(context, "Video recording paused", Toast.LENGTH_SHORT).show()
            }
            is VideoRecordEvent.Resume -> {
                isPaused(false)
                Toast.makeText(context, "Video recording resumed", Toast.LENGTH_SHORT).show()

            }
            is VideoRecordEvent.Status -> {
                println("Recorded duration ${TimeUnit.NANOSECONDS.toMinutes(event.recordingStats.recordedDurationNanos)}")
                println("Recorded bytes ${event.recordingStats.numBytesRecorded}")
            }
        }
    }
}