package com.example.myapplication.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeakingEvaluationResult
import com.example.myapplication.audio.SpeakingEvaluator
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.SpeechToTextEngine
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.NewsVocabulary
import com.example.myapplication.data.model.WorldNewsArticle
import com.example.myapplication.data.remote.WorldNewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class NewsDetailUiState(
    val article: WorldNewsArticle? = null,
    val isRecording: Boolean = false,
    val hasRecording: Boolean = false,
    val isPlayingFullArticle: Boolean = false,
    val evaluationResult: SpeakingEvaluationResult? = null,
    val favoriteKeys: Set<String> = emptySet()
)

class NewsDetailViewModel(
    private val articleId: String,
    private val newsRepository: WorldNewsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val recordingFile: File,
    private val speechToTextEngine: SpeechToTextEngine? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    init {
        val article = newsRepository.getNewsById(articleId)
        _uiState.value = _uiState.value.copy(article = article)
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                val keys = favorites.map { it.englishSentence }.toSet()
                _uiState.value = _uiState.value.copy(favoriteKeys = keys)
            }
        }
    }

    fun playFullArticle() {
        val article = _uiState.value.article ?: return
        speechPlayer.speak(article.fullScript)
        _uiState.value = _uiState.value.copy(isPlayingFullArticle = true)
    }

    fun stopAudio() {
        speechPlayer.stop()
        _uiState.value = _uiState.value.copy(isPlayingFullArticle = false)
    }

    fun playVocabulary(vocab: NewsVocabulary) {
        speechPlayer.speak(vocab.word)
    }

    fun playKeySentence() {
        val article = _uiState.value.article ?: return
        speechPlayer.speak(article.keySentence)
    }

    fun toggleVocabFavorite(vocab: NewsVocabulary) {
        val article = _uiState.value.article ?: return
        val isFav = vocab.word in _uiState.value.favoriteKeys
        viewModelScope.launch {
            if (isFav) {
                favoriteRepository.removeFavoriteByEnglishText(vocab.word)
            } else {
                favoriteRepository.addNewsVocabFavorite(vocab, article)
            }
        }
    }

    fun toggleSentenceFavorite() {
        val article = _uiState.value.article ?: return
        val isFav = article.keySentence in _uiState.value.favoriteKeys
        viewModelScope.launch {
            if (isFav) {
                favoriteRepository.removeFavoriteByEnglishText(article.keySentence)
            } else {
                favoriteRepository.addNewsSentenceFavorite(article)
            }
        }
    }

    fun startRecording() {
        val article = _uiState.value.article ?: return
        voiceRecorder.startRecording(recordingFile)
        speechToTextEngine?.startListening(
            onResult = { text ->
                val eval = SpeakingEvaluator.evaluate(text, article.keySentence)
                _uiState.value = _uiState.value.copy(evaluationResult = eval)
            },
            onError = { _ -> }
        )
        _uiState.value = _uiState.value.copy(isRecording = true, evaluationResult = null)
    }

    fun stopRecording() {
        voiceRecorder.stopRecording()
        speechToTextEngine?.stopListening()
        _uiState.value = _uiState.value.copy(isRecording = false, hasRecording = true)
    }

    fun playMyRecording() {
        if (recordingFile.exists()) {
            voicePlayer.play(recordingFile)
        }
    }

    override fun onCleared() {
        if (voiceRecorder.isRecording()) {
            voiceRecorder.stopRecording()
        }
        speechToTextEngine?.stopListening()
        speechToTextEngine?.release()
        voicePlayer.stop()
        speechPlayer.release()
        recordingFile.delete()
    }
}
