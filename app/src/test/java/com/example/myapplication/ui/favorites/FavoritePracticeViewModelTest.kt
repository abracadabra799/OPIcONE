package com.example.myapplication.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

private class FakeFavoritePracticeSpeechPlayer(
    initialAvailability: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initialAvailability)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    val spoken = mutableListOf<String>()
    var releaseCount = 0

    fun setAvailability(value: SpeechAvailability) {
        mutableAvailability.value = value
    }

    override fun speak(text: String) {
        spoken += text
    }

    override fun stop() {}

    override fun release() {
        releaseCount += 1
    }
}

private class FakeFavoritePracticeVoiceRecorder : VoiceRecorder {
    var outputFile: File? = null
    var stopCount = 0
    private var recording = false

    override fun startRecording(outputFile: File) {
        this.outputFile = outputFile
        recording = true
    }

    override fun stopRecording() {
        stopCount += 1
        recording = false
    }

    override fun isRecording(): Boolean = recording
}

private class FakeFavoritePracticeVoicePlayer : VoicePlayer {
    var played: File? = null
    var stopCount = 0

    override fun play(file: File) {
        played = file
    }

    override fun stop() {
        stopCount += 1
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritePracticeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `loads favorite and exposes legacy question fallback data`() = runTest {
        val dao = FakeFavoriteDao().apply {
            insert(
                FavoriteSentence(
                    category = "HOUSING",
                    opicQuestion = null,
                    koreanHint = "힌트",
                    englishSentence = "Answer.",
                    createdAt = 1
                )
            )
        }
        val viewModel = buildViewModel(
            favoriteId = 1,
            repository = FavoriteRepository(dao)
        )

        advanceUntilIdle()

        val ready = viewModel.uiState.value as FavoritePracticeUiState.Ready
        assertNull(ready.favorite.opicQuestion)
        assertEquals("힌트", ready.favorite.koreanHint)
        assertEquals("Answer.", ready.favorite.englishSentence)
    }

    @Test
    fun `missing favorite exposes not found state`() = runTest {
        val viewModel = buildViewModel(
            favoriteId = 999,
            repository = FavoriteRepository(FakeFavoriteDao())
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FavoritePracticeUiState.NotFound)
    }

    @Test
    fun `record stop play and complete update one item flow and release resources`() = runTest {
        val repository = repositoryWithFavorite()
        val speechPlayer = FakeFavoritePracticeSpeechPlayer()
        val voiceRecorder = FakeFavoritePracticeVoiceRecorder()
        val voicePlayer = FakeFavoritePracticeVoicePlayer()
        val recordingFile = temporaryFolder.newFile("favorite-practice.m4a")
        val viewModel = FavoritePracticeViewModel(
            favoriteId = 1,
            favoriteRepository = repository,
            speechPlayer = speechPlayer,
            voiceRecorder = voiceRecorder,
            voicePlayer = voicePlayer,
            recordingFile = recordingFile
        )
        advanceUntilIdle()

        viewModel.startRecording()
        assertTrue((viewModel.uiState.value as FavoritePracticeUiState.Ready).isRecording)
        assertEquals(recordingFile, voiceRecorder.outputFile)

        viewModel.stopRecording()
        val stopped = viewModel.uiState.value as FavoritePracticeUiState.Ready
        assertFalse(stopped.isRecording)
        assertTrue(stopped.hasRecording)

        viewModel.playMyRecording()
        assertEquals(recordingFile, voicePlayer.played)

        viewModel.complete()
        val completed = viewModel.uiState.value as FavoritePracticeUiState.Ready
        assertTrue(completed.isComplete)
        assertFalse(completed.hasRecording)
        assertFalse(recordingFile.exists())
        assertEquals(1, voicePlayer.stopCount)
        assertEquals(1, speechPlayer.releaseCount)
    }

    @Test
    fun `model sentence playback follows current speech availability`() = runTest {
        val player = FakeFavoritePracticeSpeechPlayer(SpeechAvailability.Unavailable)
        val viewModel = buildViewModel(
            favoriteId = 1,
            repository = repositoryWithFavorite(),
            speechPlayer = player
        )
        advanceUntilIdle()

        viewModel.playModelSentence()
        assertTrue(player.spoken.isEmpty())
        assertEquals(
            SpeechAvailability.Unavailable,
            (viewModel.uiState.value as FavoritePracticeUiState.Ready).speechAvailability
        )

        player.setAvailability(SpeechAvailability.Available)
        advanceUntilIdle()
        viewModel.playModelSentence()

        assertEquals(listOf("Answer."), player.spoken)
    }

    @Test
    fun `clearing while recording stops and releases resources and deletes recording`() = runTest {
        val speechPlayer = FakeFavoritePracticeSpeechPlayer()
        val voiceRecorder = FakeFavoritePracticeVoiceRecorder()
        val voicePlayer = FakeFavoritePracticeVoicePlayer()
        val recordingFile = temporaryFolder.newFile("favorite-practice-clear.m4a")
        val repository = repositoryWithFavorite()
        val viewModelStore = ViewModelStore()
        val viewModel = ViewModelProvider(
            viewModelStore,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FavoritePracticeViewModel(
                        favoriteId = 1,
                        favoriteRepository = repository,
                        speechPlayer = speechPlayer,
                        voiceRecorder = voiceRecorder,
                        voicePlayer = voicePlayer,
                        recordingFile = recordingFile
                    ) as T
            }
        )[FavoritePracticeViewModel::class.java]
        advanceUntilIdle()
        viewModel.startRecording()

        viewModelStore.clear()

        assertEquals(1, voiceRecorder.stopCount)
        assertEquals(1, voicePlayer.stopCount)
        assertEquals(1, speechPlayer.releaseCount)
        assertFalse(recordingFile.exists())
    }

    private suspend fun repositoryWithFavorite(): FavoriteRepository {
        val dao = FakeFavoriteDao()
        dao.insert(
            FavoriteSentence(
                category = "HOUSING",
                opicQuestion = "Question",
                koreanHint = "힌트",
                englishSentence = "Answer.",
                createdAt = 1
            )
        )
        return FavoriteRepository(dao)
    }

    private fun buildViewModel(
        favoriteId: Long,
        repository: FavoriteRepository,
        speechPlayer: FakeFavoritePracticeSpeechPlayer = FakeFavoritePracticeSpeechPlayer()
    ): FavoritePracticeViewModel = FavoritePracticeViewModel(
        favoriteId = favoriteId,
        favoriteRepository = repository,
        speechPlayer = speechPlayer,
        voiceRecorder = FakeFavoritePracticeVoiceRecorder(),
        voicePlayer = FakeFavoritePracticeVoicePlayer(),
        recordingFile = temporaryFolder.newFile("favorite-practice-$favoriteId.m4a")
    )
}
