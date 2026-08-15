package com.example.myapplication.ui.set

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

private val QUESTION_1 = PracticeQuestion("힌트1", "Sentence one.", PracticeCategory.HOBBY)
private val QUESTION_2 = PracticeQuestion("힌트2", "Sentence two.", PracticeCategory.HOBBY)

private class FakePracticeRepository(
    private val result: Result<List<PracticeQuestion>>
) : PracticeRepository {
    override suspend fun generateSet(
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = result
}

private class FakeApiKeyStore(private var key: String?) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

private class FakeSpeechPlayer : SpeechPlayer {
    var spokenText: String? = null
    override fun speak(text: String) { spokenText = text }
    override fun release() {}
}

private class FakeVoiceRecorder : VoiceRecorder {
    private var recording = false
    override fun startRecording(outputFile: File) { recording = true }
    override fun stopRecording() { recording = false }
    override fun isRecording(): Boolean = recording
}

private class FakeVoicePlayer : VoicePlayer {
    var playedFile: File? = null
    override fun play(file: File) { playedFile = file }
    override fun stop() {}
}

class PracticeSetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        result: Result<List<PracticeQuestion>> = Result.success(listOf(QUESTION_1, QUESTION_2)),
        apiKey: String? = "test-key"
    ): PracticeSetViewModel = PracticeSetViewModel(
        category = PracticeCategory.HOBBY,
        practiceRepository = FakePracticeRepository(result),
        favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
        apiKeyStore = FakeApiKeyStore(apiKey),
        speechPlayer = FakeSpeechPlayer(),
        voiceRecorder = FakeVoiceRecorder(),
        voicePlayer = FakeVoicePlayer(),
        recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() }
    )

    @Test
    fun `loadSet without an api key emits an error state`() = runTest {
        val viewModel = buildViewModel(apiKey = null)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PracticeSetUiState.Error)
    }

    @Test
    fun `loadSet success emits ready state with first question`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertEquals(0, state.currentIndex)
        assertEquals(QUESTION_1, state.questions[0])
        assertFalse(state.isCurrentFavorite)
    }

    @Test
    fun `start then stop recording updates flags`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.startRecording()
        assertTrue((viewModel.uiState.value as PracticeSetUiState.Ready).isRecording)

        viewModel.stopRecording()
        val afterStop = viewModel.uiState.value as PracticeSetUiState.Ready
        assertFalse(afterStop.isRecording)
        assertTrue(afterStop.hasRecording)
    }

    @Test
    fun `toggleFavorite marks then unmarks the current question`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertTrue((viewModel.uiState.value as PracticeSetUiState.Ready).isCurrentFavorite)

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertFalse((viewModel.uiState.value as PracticeSetUiState.Ready).isCurrentFavorite)
    }

    @Test
    fun `nextQuestion advances index and resets per-question flags`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.nextQuestion()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertEquals(1, state.currentIndex)
        assertFalse(state.hasRecording)
        assertFalse(state.isSetComplete)
    }

    @Test
    fun `nextQuestion past the last question marks the set complete`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.nextQuestion()
        advanceUntilIdle()
        viewModel.nextQuestion()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertTrue(state.isSetComplete)
    }

    @Test
    fun `playModelSentence speaks the current question's english sentence`() = runTest {
        val speechPlayer = FakeSpeechPlayer()
        val viewModel = PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1, QUESTION_2))),
            favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
            apiKeyStore = FakeApiKeyStore("test-key"),
            speechPlayer = speechPlayer,
            voiceRecorder = FakeVoiceRecorder(),
            voicePlayer = FakeVoicePlayer(),
            recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() }
        )
        advanceUntilIdle()

        viewModel.playModelSentence()

        assertEquals("Sentence one.", speechPlayer.spokenText)
    }
}
