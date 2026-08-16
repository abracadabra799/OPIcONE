package com.example.myapplication.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FavoritePracticeViewModel(
    private val favoriteId: Long,
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val recordingFile: File
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritePracticeUiState>(
        FavoritePracticeUiState.Loading
    )
    val uiState: StateFlow<FavoritePracticeUiState> = _uiState.asStateFlow()

    private var resourcesReleased = false

    init {
        viewModelScope.launch {
            val favorite = favoriteRepository.getFavorite(favoriteId)
            _uiState.value = if (favorite == null) {
                FavoritePracticeUiState.NotFound
            } else {
                FavoritePracticeUiState.Ready(
                    favorite = favorite,
                    speechAvailability = speechPlayer.availability.value
                )
            }
        }
        viewModelScope.launch {
            speechPlayer.availability.collect { availability ->
                val current = _uiState.value as? FavoritePracticeUiState.Ready
                    ?: return@collect
                _uiState.value = current.copy(speechAvailability = availability)
            }
        }
    }

    fun startRecording() {
        val current = activeReadyState() ?: return
        if (current.isRecording) return
        voiceRecorder.startRecording(recordingFile)
        _uiState.value = current.copy(isRecording = true)
    }

    fun stopRecording() {
        val current = activeReadyState() ?: return
        if (!current.isRecording) return
        voiceRecorder.stopRecording()
        _uiState.value = current.copy(isRecording = false, hasRecording = true)
    }

    fun playMyRecording() {
        val current = activeReadyState() ?: return
        if (current.hasRecording && recordingFile.exists()) {
            voicePlayer.play(recordingFile)
        }
    }

    fun playModelSentence() {
        val current = activeReadyState() ?: return
        if (
            current.speechAvailability == SpeechAvailability.Available &&
            speechPlayer.availability.value == SpeechAvailability.Available
        ) {
            speechPlayer.speak(current.favorite.englishSentence)
        }
    }

    fun complete() {
        val current = activeReadyState() ?: return
        _uiState.value = current.copy(
            isRecording = false,
            hasRecording = false,
            isComplete = true
        )
        releaseResources()
    }

    private fun activeReadyState(): FavoritePracticeUiState.Ready? =
        (_uiState.value as? FavoritePracticeUiState.Ready)?.takeUnless { it.isComplete }

    private fun releaseResources() {
        if (resourcesReleased) return
        resourcesReleased = true
        if (voiceRecorder.isRecording()) {
            voiceRecorder.stopRecording()
        }
        voicePlayer.stop()
        speechPlayer.release()
        recordingFile.delete()
    }

    override fun onCleared() {
        releaseResources()
    }
}
