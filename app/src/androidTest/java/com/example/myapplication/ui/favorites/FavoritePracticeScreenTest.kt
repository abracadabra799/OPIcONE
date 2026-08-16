package com.example.myapplication.ui.favorites

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

private class FakeFavoriteScreenSpeechPlayer(
    initialAvailability: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initialAvailability)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    val spoken = mutableListOf<String>()

    override fun speak(text: String) {
        spoken += text
    }

    override fun release() = Unit
}

private class FakeFavoriteScreenVoiceRecorder : VoiceRecorder {
    private var recording = false

    override fun startRecording(outputFile: File) {
        recording = true
    }

    override fun stopRecording() {
        recording = false
    }

    override fun isRecording(): Boolean = recording
}

private class FakeFavoriteScreenVoicePlayer : VoicePlayer {
    var played: File? = null

    override fun play(file: File) {
        played = file
    }

    override fun stop() = Unit
}

class FavoritePracticeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase
    private val recordingFiles = mutableListOf<File>()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
        recordingFiles.forEach(File::delete)
    }

    @Test
    fun actualQuestion_isShown() = runBlocking {
        val actualId = insertFavorite(opicQuestion = "Tell me about your home.")
        val viewModel = buildViewModel(actualId)

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = {}
            )
        }
        composeTestRule.onNodeWithText("Tell me about your home.").assertExists()
        Unit
    }

    @Test
    fun legacyFavorite_showsExactQuestionFallback() = runBlocking {
        val legacyId = insertFavorite(opicQuestion = null, englishSentence = "Legacy answer.")
        val viewModel = buildViewModel(legacyId)

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = {}
            )
        }
        composeTestRule.onNodeWithText(
            "저장된 질문 없음 — 힌트를 보고 답해보세요."
        ).assertExists()
        Unit
    }

    @Test
    fun recordingControls_recordStopAndPlayOneAttempt() = runBlocking {
        val favoriteId = insertFavorite()
        val voicePlayer = FakeFavoriteScreenVoicePlayer()
        val recordingFile = newRecordingFile("record-controls")
        val viewModel = buildViewModel(
            favoriteId = favoriteId,
            voicePlayer = voicePlayer,
            recordingFile = recordingFile
        )

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("녹음 시작").performClick()
        composeTestRule.onNodeWithText("녹음 중지").performClick()
        composeTestRule.onNodeWithText("내 녹음 듣기").performClick()

        assertEquals(recordingFile, voicePlayer.played)
    }

    @Test
    fun unavailableSpeech_keepsRecordingPracticeAndDisablesModelPlayback() = runBlocking {
        val favoriteId = insertFavorite()
        val speechPlayer = FakeFavoriteScreenSpeechPlayer(SpeechAvailability.Unavailable)
        val viewModel = buildViewModel(
            favoriteId = favoriteId,
            speechPlayer = speechPlayer
        )

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = {},
                canRecordAudio = false,
                recordAudioPermissionDenied = true
            )
        }

        composeTestRule.onNodeWithText("녹음 시작").assertExists()
        composeTestRule.onNodeWithText("모범 문장 듣기").assertIsNotEnabled()
        composeTestRule.onNodeWithText(
            "이 기기에서는 영어 음성 재생을 사용할 수 없습니다."
        ).assertExists()
        assertTrue(speechPlayer.spoken.isEmpty())
    }

    @Test
    fun recordWithoutPermission_requestsPermission() = runBlocking {
        val favoriteId = insertFavorite()
        var permissionRequested = false
        val viewModel = buildViewModel(favoriteId)

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = {},
                canRecordAudio = false,
                onRequestRecordAudioPermission = { permissionRequested = true }
            )
        }

        composeTestRule.onNodeWithText("녹음 시작").performClick()

        assertTrue(permissionRequested)
        composeTestRule.onNodeWithText("녹음 시작").assertExists()
        Unit
    }

    @Test
    fun practiceComplete_callsCompletionExactlyOnce() = runBlocking {
        val favoriteId = insertFavorite()
        var completionCount = 0
        val viewModel = buildViewModel(favoriteId)

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = { completionCount += 1 },
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("연습 완료").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2_000) { completionCount == 1 }
        composeTestRule.runOnIdle { assertEquals(1, completionCount) }
    }

    @Test
    fun missingFavorite_showsNotFoundAndReturnsToList() {
        var returned = false
        val viewModel = buildViewModel(999)

        composeTestRule.setContent {
            FavoritePracticeScreen(
                viewModel = viewModel,
                onComplete = {},
                onBack = { returned = true }
            )
        }

        composeTestRule.onNodeWithText("즐겨찾기 문장을 찾을 수 없습니다.").assertExists()
        composeTestRule.onNodeWithText("목록으로").performClick()

        assertTrue(returned)
    }

    private suspend fun insertFavorite(
        opicQuestion: String? = "Question",
        englishSentence: String = "Answer."
    ): Long = database.favoriteDao().insert(
        FavoriteSentence(
            category = "HOUSING",
            opicQuestion = opicQuestion,
            koreanHint = "힌트",
            englishSentence = englishSentence,
            createdAt = 1
        )
    )

    private fun buildViewModel(
        favoriteId: Long,
        speechPlayer: FakeFavoriteScreenSpeechPlayer = FakeFavoriteScreenSpeechPlayer(),
        voicePlayer: FakeFavoriteScreenVoicePlayer = FakeFavoriteScreenVoicePlayer(),
        recordingFile: File = newRecordingFile("favorite-$favoriteId")
    ) = FavoritePracticeViewModel(
        favoriteId = favoriteId,
        favoriteRepository = FavoriteRepository(database.favoriteDao()),
        speechPlayer = speechPlayer,
        voiceRecorder = FakeFavoriteScreenVoiceRecorder(),
        voicePlayer = voicePlayer,
        recordingFile = recordingFile
    )

    private fun newRecordingFile(name: String): File =
        File.createTempFile(name, ".m4a").also(recordingFiles::add)
}
