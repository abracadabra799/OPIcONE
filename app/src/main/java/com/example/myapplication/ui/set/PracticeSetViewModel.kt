package com.example.myapplication.ui.set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeakingEvaluator
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.SpeechToTextEngine
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.remote.AuthenticationFailed
import com.example.myapplication.data.remote.MissingApiKey
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.session.CompletedPracticeItem
import com.example.myapplication.data.session.CompletedPracticeSet
import com.example.myapplication.data.session.PracticeSessionStore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class PracticeSetViewModel(
    private val category: PracticeCategory,
    private val practiceRepository: PracticeRepository,
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val recordingFile: File,
    private val sessionStore: PracticeSessionStore,
    private val speechToTextEngine: SpeechToTextEngine? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeSetUiState>(PracticeSetUiState.Loading)
    val uiState: StateFlow<PracticeSetUiState> = _uiState.asStateFlow()
    private val favoriteMutationMutex = Mutex()

    init {
        loadSet()
        viewModelScope.launch {
            speechPlayer.availability.collect { availability ->
                val current = _uiState.value as? PracticeSetUiState.Ready ?: return@collect
                _uiState.value = current.copy(speechAvailability = availability)
            }
        }
    }

    fun loadSet() {
        _uiState.value = PracticeSetUiState.Loading
        viewModelScope.launch {
            practiceRepository.generateSet(category = category).onSuccess { questions ->
                val favoriteQuestionKeys = questions
                    .filter { favoriteRepository.isFavorite(it) }
                    .mapTo(linkedSetOf()) { it.englishSentence }
                _uiState.value = PracticeSetUiState.Ready(
                    questions = questions,
                    currentIndex = 0,
                    isRecording = false,
                    hasRecording = false,
                    favoriteQuestionKeys = favoriteQuestionKeys,
                    speechAvailability = speechPlayer.availability.value,
                    isSetComplete = false,
                    evaluationResult = null
                )
            }.onFailure { error ->
                _uiState.value = PracticeSetUiState.Error(
                    message = error.message ?: "문제를 생성하지 못했습니다.",
                    showSettingsAction = error is MissingApiKey || error is AuthenticationFailed
                )
            }
        }
    }

    fun startRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.startRecording(recordingFile)
        speechToTextEngine?.startListening(
            onResult = { text ->
                val ready = _uiState.value as? PracticeSetUiState.Ready ?: return@startListening
                val currentQuestion = ready.questions[ready.currentIndex]
                val eval = SpeakingEvaluator.evaluate(text, currentQuestion.englishSentence)
                _uiState.value = ready.copy(evaluationResult = eval)
            },
            onError = { _ -> }
        )
        _uiState.value = current.copy(isRecording = true, isAnswerRevealed = true, evaluationResult = null)
    }

    fun stopRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.stopRecording()
        speechToTextEngine?.stopListening()
        _uiState.value = current.copy(isRecording = false, hasRecording = true, isAnswerRevealed = true)
    }

    fun revealAnswer() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        _uiState.value = current.copy(isAnswerRevealed = true)
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
        val isFavorite = !current.isCurrentFavorite
        _uiState.value = current.copy(
            favoriteQuestionKeys = if (isFavorite) {
                current.favoriteQuestionKeys + question.englishSentence
            } else {
                current.favoriteQuestionKeys - question.englishSentence
            }
        )
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            withContext(NonCancellable) {
                favoriteMutationMutex.withLock {
                    if (isFavorite) {
                        favoriteRepository.addFavorite(question)
                    } else {
                        favoriteRepository.removeFavoriteByQuestion(question)
                    }
                }
            }
        }
    }

    private fun stopRecordingResources() {
        if (voiceRecorder.isRecording()) {
            voiceRecorder.stopRecording()
        }
        speechToTextEngine?.stopListening()
        voicePlayer.stop()
        recordingFile.delete()
    }

    fun nextQuestion() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        stopRecordingResources()
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            sessionStore.saveCompletedSet(
                CompletedPracticeSet(
                    current.questions.map { question ->
                        CompletedPracticeItem(
                            question = question,
                            isFavorite = question.englishSentence in current.favoriteQuestionKeys
                        )
                    }
                )
            )
            _uiState.value = current.copy(
                isSetComplete = true,
                isRecording = false,
                hasRecording = false,
                isAnswerRevealed = false,
                evaluationResult = null
            )
            return
        }
        _uiState.value = current.copy(
            currentIndex = nextIndex,
            isRecording = false,
            hasRecording = false,
            isAnswerRevealed = false,
            evaluationResult = null
        )
    }

    override fun onCleared() {
        speechPlayer.release()
        speechToTextEngine?.release()
        stopRecordingResources()
    }
}
