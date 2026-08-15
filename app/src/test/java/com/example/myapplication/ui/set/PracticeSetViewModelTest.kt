package com.example.myapplication.ui.set

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.remote.AuthenticationFailed
import com.example.myapplication.data.remote.InvalidPracticeSet
import com.example.myapplication.data.remote.InvalidProviderResponse
import com.example.myapplication.data.remote.MissingApiKey
import com.example.myapplication.data.remote.NetworkFailure
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.remote.ProviderFailure
import com.example.myapplication.data.remote.RateLimited
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

private val QUESTION_1 = PracticeQuestion(
    opicQuestion = "Tell me about item 1.",
    koreanHint = "힌트1",
    englishSentence = "Sentence one.",
    category = PracticeCategory.HOBBY
)
private val QUESTION_2 = PracticeQuestion(
    opicQuestion = "Tell me about item 2.",
    koreanHint = "힌트2",
    englishSentence = "Sentence two.",
    category = PracticeCategory.HOBBY
)

private class FakePracticeRepository(
    private val result: Result<List<PracticeQuestion>>
) : PracticeRepository {
    override suspend fun generateSet(
        category: PracticeCategory,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = result
}

private class FakeSpeechPlayer(
    initial: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initial)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    var spokenText: String? = null
    override fun speak(text: String) {
        if (availability.value == SpeechAvailability.Available) spokenText = text
    }
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
        result: Result<List<PracticeQuestion>> = Result.success(listOf(QUESTION_1, QUESTION_2))
    ): PracticeSetViewModel = PracticeSetViewModel(
        category = PracticeCategory.HOBBY,
        practiceRepository = FakePracticeRepository(result),
        favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
        speechPlayer = FakeSpeechPlayer(),
        voiceRecorder = FakeVoiceRecorder(),
        voicePlayer = FakeVoicePlayer(),
        recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() }
    )

    @Test
    fun `missing key error offers settings action`() = runTest {
        val viewModel = buildViewModel(Result.failure(MissingApiKey(AiProvider.OPENAI)))
        advanceUntilIdle()

        assertEquals(
            PracticeSetUiState.Error("OpenAI API 키가 없습니다.", showSettingsAction = true),
            viewModel.uiState.value
        )
    }

    @Test
    fun `authentication failure offers settings action`() = runTest {
        val viewModel = buildViewModel(Result.failure(AuthenticationFailed(AiProvider.CLAUDE)))
        advanceUntilIdle()

        assertEquals(
            PracticeSetUiState.Error("Claude API 키를 확인해주세요.", showSettingsAction = true),
            viewModel.uiState.value
        )
    }

    @Test
    fun `rate limit error offers retry but not settings`() = runTest {
        val viewModel = buildViewModel(Result.failure(RateLimited(AiProvider.OPENAI)))
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as PracticeSetUiState.Error).showSettingsAction)
    }

    @Test
    fun `network error does not offer settings action`() = runTest {
        val viewModel = buildViewModel(
            Result.failure(NetworkFailure(AiProvider.OPENAI, IllegalStateException("socket detail")))
        )
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as PracticeSetUiState.Error).showSettingsAction)
    }

    @Test
    fun `provider error does not offer settings action`() = runTest {
        val viewModel = buildViewModel(Result.failure(ProviderFailure(AiProvider.OPENAI, 503)))
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as PracticeSetUiState.Error).showSettingsAction)
    }

    @Test
    fun `invalid provider response does not offer settings action`() = runTest {
        val viewModel = buildViewModel(Result.failure(InvalidProviderResponse(AiProvider.OPENAI)))
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as PracticeSetUiState.Error).showSettingsAction)
    }

    @Test
    fun `invalid practice set does not offer settings action`() = runTest {
        val viewModel = buildViewModel(
            Result.failure(InvalidPracticeSet(IllegalStateException("parse detail")))
        )
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as PracticeSetUiState.Error).showSettingsAction)
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
