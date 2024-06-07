package com.example.composevideorecording.ui.screens.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = hiltViewModel()){
    val state = viewModel.playerState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    DisposableEffect(key1 = lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event -> lifecycle.value = event }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    PlayerUI(state.value, viewModel.player, lifecycle.value)
}

@Composable
fun PlayerUI(value: PlayerUIState, player: Player, lifecycle: Lifecycle.Event) {
    Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()){
        Box(modifier = Modifier.fillMaxSize()){
            if(value.doFileExists){
                AndroidView(factory = {
                    PlayerView(
                        it
                    ).apply {
                        this.player = player
                    }
                }, update = {
                    when(lifecycle){
                        Lifecycle.Event.ON_PAUSE -> it.onPause()
                        Lifecycle.Event.ON_RESUME -> it.onResume()
                        else -> Unit
                    }
                }, modifier = Modifier
                    .fillMaxSize()
                )
            } else{
                Text(text = "File not found", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
