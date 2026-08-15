package com.example.myapplication.ui.set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PracticeSetViewModel(
    private val category: PracticeCategory,
    private val practiceRepository: PracticeRepository,
    private val favoriteRepository: FavoriteRepository,
    private val apiKeyStore: ApiKeyStore,
    private val speechPlayer: SpeechPlayer,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val recordingFile: File
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeSetUiState>(PracticeSetUiState.Loading)
    val uiState: StateFlow<PracticeSetUiState> = _uiState.asStateFlow()

    init {
        loadSet()
    }

    fun loadSet() {
        _uiState.value = PracticeSetUiState.Loading
        viewModelScope.launch {
            val apiKey = apiKeyStore.getApiKey()
            if (apiKey.isNullOrBlank()) {
                _uiState.value = PracticeSetUiState.Error("설정에서 API 키를 먼저 입력해주세요.")
                return@launch
            }
            practiceRepository.generateSet(
                category = category,
                alreadyAskedQuestions = emptyList()
            ).onSuccess { questions ->
                _uiState.value = PracticeSetUiState.Ready(
                    questions = questions,
                    currentIndex = 0,
                    isRecording = false,
                    hasRecording = false,
                    isCurrentFavorite = favoriteRepository.isFavorite(questions.first()),
                    isSetComplete = false
                )
            }.onFailure { error ->
                _uiState.value = PracticeSetUiState.Error(error.message ?: "문제를 생성하지 못했습니다.")
            }
        }
    }

    fun startRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.startRecording(recordingFile)
        _uiState.value = current.copy(isRecording = true)
    }

    fun stopRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.stopRecording()
        _uiState.value = current.copy(isRecording = false, hasRecording = true)
    }

    fun playMyRecording() {
        if (recordingFile.exists()) voicePlayer.play(recordingFile)
    }

    fun playModelSentence() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        speechPlayer.speak(current.questions[current.currentIndex].englishSentence)
    }

    fun toggleFavorite() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        val question = current.questions[current.currentIndex]
        viewModelScope.launch {
            if (current.isCurrentFavorite) {
                favoriteRepository.removeFavoriteByQuestion(question)
            } else {
                favoriteRepository.addFavorite(question)
            }
            val refreshed = _uiState.value as? PracticeSetUiState.Ready ?: return@launch
            _uiState.value = refreshed.copy(isCurrentFavorite = !current.isCurrentFavorite)
        }
    }

    fun nextQuestion() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        recordingFile.delete()
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            _uiState.value = current.copy(isSetComplete = true, isRecording = false, hasRecording = false)
            return
        }
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(current.questions[nextIndex])
            _uiState.value = current.copy(
                currentIndex = nextIndex,
                isRecording = false,
                hasRecording = false,
                isCurrentFavorite = isFavorite
            )
        }
    }

    override fun onCleared() {
        speechPlayer.release()
        recordingFile.delete()
    }
}
