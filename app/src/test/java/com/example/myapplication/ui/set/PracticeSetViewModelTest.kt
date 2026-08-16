package com.example.myapplication.ui.set

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
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
import com.example.myapplication.data.session.CompletedPracticeItem
import com.example.myapplication.data.session.PracticeSessionStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
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
    var stopCount = 0
    override fun startRecording(outputFile: File) { recording = true }
    override fun stopRecording() {
        stopCount++
        recording = false
    }
    override fun isRecording(): Boolean = recording
}

private class FakeVoicePlayer : VoicePlayer {
    var playedFile: File? = null
    var stopCount = 0
    override fun play(file: File) { playedFile = file }
    override fun stop() { stopCount++ }
}

private class DelayedFavoriteDao : FavoriteDao {
    private val favorites = MutableStateFlow<List<FavoriteSentence>>(emptyList())
    val allowInsert = CompletableDeferred<Unit>()

    override suspend fun insert(favorite: FavoriteSentence): Long {
        allowInsert.await()
        favorites.value = favorites.value + favorite.copy(id = 1)
        return 1
    }

    override suspend fun delete(favorite: FavoriteSentence) {
        favorites.value = favorites.value.filterNot { it.id == favorite.id }
    }

    override suspend fun deleteByEnglishSentence(englishSentence: String) {
        favorites.value = favorites.value.filterNot { it.englishSentence == englishSentence }
    }

    override fun observeAll(): Flow<List<FavoriteSentence>> = favorites

    override suspend fun findById(id: Long): FavoriteSentence? =
        favorites.value.firstOrNull { it.id == id }

    override suspend fun isFavorite(englishSentence: String): Boolean =
        favorites.value.any { it.englishSentence == englishSentence }
}

class PracticeSetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        result: Result<List<PracticeQuestion>> = Result.success(listOf(QUESTION_1, QUESTION_2)),
        sessionStore: PracticeSessionStore = PracticeSessionStore()
    ): PracticeSetViewModel = PracticeSetViewModel(
        category = PracticeCategory.HOBBY,
        practiceRepository = FakePracticeRepository(result),
        favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
        speechPlayer = FakeSpeechPlayer(),
        voiceRecorder = FakeVoiceRecorder(),
        voicePlayer = FakeVoicePlayer(),
        recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() },
        sessionStore = sessionStore
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
    fun `finishing last item stores questions and favorite flags`() = runTest {
        val sessionStore = PracticeSessionStore()
        val viewModel = buildViewModel(sessionStore = sessionStore)
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        viewModel.nextQuestion()
        advanceUntilIdle()
        viewModel.nextQuestion()
        advanceUntilIdle()

        val completed = requireNotNull(sessionStore.lastCompletedSet())
        assertEquals(listOf(true, false), completed.items.map(CompletedPracticeItem::isFavorite))
        assertEquals(listOf(QUESTION_1, QUESTION_2), completed.items.map(CompletedPracticeItem::question))
    }

    @Test
    fun `finishing immediately after favorite preserves summary and persistence after clear`() = runTest {
        val dao = DelayedFavoriteDao()
        val sessionStore = PracticeSessionStore()
        val viewModelStore = ViewModelStore()
        val viewModel = ViewModelProvider(
            viewModelStore,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PracticeSetViewModel(
                        category = PracticeCategory.HOBBY,
                        practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1))),
                        favoriteRepository = FavoriteRepository(dao),
                        speechPlayer = FakeSpeechPlayer(),
                        voiceRecorder = FakeVoiceRecorder(),
                        voicePlayer = FakeVoicePlayer(),
                        recordingFile = File.createTempFile("recording_favorite_clear", ".m4a"),
                        sessionStore = sessionStore
                    ) as T
            }
        )[PracticeSetViewModel::class.java]
        advanceUntilIdle()

        viewModel.toggleFavorite()
        viewModel.nextQuestion()
        viewModelStore.clear()
        dao.allowInsert.complete(Unit)
        advanceUntilIdle()

        assertTrue(requireNotNull(sessionStore.lastCompletedSet()).items.single().isFavorite)
        assertTrue(dao.isFavorite(QUESTION_1.englishSentence))
    }

    @Test
    fun `next question stops active recording and playback before deleting recording`() = runTest {
        val voiceRecorder = FakeVoiceRecorder()
        val voicePlayer = FakeVoicePlayer()
        val recordingFile = File.createTempFile("recording_next", ".m4a")
        val viewModel = PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1, QUESTION_2))),
            favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
            speechPlayer = FakeSpeechPlayer(),
            voiceRecorder = voiceRecorder,
            voicePlayer = voicePlayer,
            recordingFile = recordingFile,
            sessionStore = PracticeSessionStore()
        )
        advanceUntilIdle()
        viewModel.startRecording()

        viewModel.nextQuestion()

        assertEquals(1, voiceRecorder.stopCount)
        assertEquals(1, voicePlayer.stopCount)
        assertFalse(recordingFile.exists())
    }

    @Test
    fun `completing set stops active recording and playback before deleting recording`() = runTest {
        val voiceRecorder = FakeVoiceRecorder()
        val voicePlayer = FakeVoicePlayer()
        val recordingFile = File.createTempFile("recording_complete", ".m4a")
        val viewModel = PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1))),
            favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
            speechPlayer = FakeSpeechPlayer(),
            voiceRecorder = voiceRecorder,
            voicePlayer = voicePlayer,
            recordingFile = recordingFile,
            sessionStore = PracticeSessionStore()
        )
        advanceUntilIdle()
        viewModel.startRecording()

        viewModel.nextQuestion()

        assertEquals(1, voiceRecorder.stopCount)
        assertEquals(1, voicePlayer.stopCount)
        assertFalse(recordingFile.exists())
    }

    @Test
    fun `clearing set stops active recording and playback before deleting recording`() = runTest {
        val voiceRecorder = FakeVoiceRecorder()
        val voicePlayer = FakeVoicePlayer()
        val recordingFile = File.createTempFile("recording_clear", ".m4a")
        val viewModelStore = ViewModelStore()
        val viewModel = ViewModelProvider(
            viewModelStore,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PracticeSetViewModel(
                        category = PracticeCategory.HOBBY,
                        practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1))),
                        favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
                        speechPlayer = FakeSpeechPlayer(),
                        voiceRecorder = voiceRecorder,
                        voicePlayer = voicePlayer,
                        recordingFile = recordingFile,
                        sessionStore = PracticeSessionStore()
                    ) as T
            }
        )[PracticeSetViewModel::class.java]
        advanceUntilIdle()
        viewModel.startRecording()

        viewModelStore.clear()

        assertEquals(1, voiceRecorder.stopCount)
        assertEquals(1, voicePlayer.stopCount)
        assertFalse(recordingFile.exists())
    }

    @Test
    fun `advancing before last item does not store a completed set`() = runTest {
        val sessionStore = PracticeSessionStore()
        val viewModel = buildViewModel(sessionStore = sessionStore)
        advanceUntilIdle()

        viewModel.nextQuestion()
        advanceUntilIdle()

        assertEquals(null, sessionStore.lastCompletedSet())
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
            recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() },
            sessionStore = PracticeSessionStore()
        )
        advanceUntilIdle()

        viewModel.playModelSentence()

        assertEquals("Sentence one.", speechPlayer.spokenText)
    }
}
