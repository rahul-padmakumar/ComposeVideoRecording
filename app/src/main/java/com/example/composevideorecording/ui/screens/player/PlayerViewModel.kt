package com.example.composevideorecording.ui.screens.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.composevideorecording.utilities.FILE_NAME
import com.example.composevideorecording.utilities.file_checker.FileChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val fileChecker: FileChecker
): ViewModel(){

    private val _playerState = MutableStateFlow(PlayerUIState())
    val playerState = _playerState.asStateFlow()
    init {
        if(fileChecker.doFileExists(FILE_NAME)){
            _playerState.update {
                it.copy(
                    doFileExists = true
                )
            }
            startPlay()
        } else {
            _playerState.update {
                it.copy(
                    doFileExists = false
                )
            }
        }
    }

    private fun startPlay() {
        player.prepare()
        player.addMediaItem(MediaItem.fromUri(fileChecker.getFileUri(FILE_NAME)))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}