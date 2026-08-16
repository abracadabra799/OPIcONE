package com.example.myapplication.ui.set

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.remote.MissingApiKey
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.session.PracticeSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

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
    val spoken = mutableListOf<String>()
    override fun speak(text: String) {
        if (availability.value == SpeechAvailability.Available) spoken += text
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
    override fun play(file: File) {}
    override fun stop() {}
}

class PracticeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase

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
    }

    private fun buildViewModel(
        questionCount: Int = 2,
        result: Result<List<PracticeQuestion>>? = null,
        speechAvailability: SpeechAvailability = SpeechAvailability.Available
    ): PracticeSetViewModel {
        val questions = (1..questionCount).map {
            PracticeQuestion(
                opicQuestion = "Tell me about hobby $it.",
                koreanHint = "힌트$it",
                englishSentence = "Sentence $it.",
                category = PracticeCategory.HOBBY
            )
        }
        return PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(result ?: Result.success(questions)),
            favoriteRepository = FavoriteRepository(database.favoriteDao()),
            speechPlayer = FakeSpeechPlayer(speechAvailability),
            voiceRecorder = FakeVoiceRecorder(),
            voicePlayer = FakeVoicePlayer(),
            recordingFile = File.createTempFile("practice_screen_test", ".m4a").apply { deleteOnExit() },
            sessionStore = PracticeSessionStore()
        )
    }

    @Test
    fun tappingSettingsForMissingKey_opensSettings() {
        var opened = false
        val viewModel = buildViewModel(
            result = Result.failure(MissingApiKey(AiProvider.OPENAI))
        )
        composeTestRule.setContent {
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = {},
                onBack = {},
                onOpenSettings = { opened = true }
            )
        }

        composeTestRule.onNodeWithText("설정으로 이동").performClick()

        assertTrue(opened)
    }

    @Test
    fun recordingAndStopping_revealsModelSentenceAndFavoriteButton() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            PracticeScreen(viewModel = viewModel, onSetComplete = {}, onBack = {})
        }

        composeTestRule.onNodeWithText("힌트1").assertExists()
        composeTestRule.onNodeWithText("녹음 시작").performClick()
        composeTestRule.onNodeWithText("녹음 중지").performClick()

        composeTestRule.onNodeWithText("Sentence 1.").assertExists()
        composeTestRule.onNodeWithText("☆ 즐겨찾기").assertExists()
    }

    @Test
    fun readyState_showsActualOpicQuestion() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            PracticeScreen(viewModel = viewModel, onSetComplete = {}, onBack = {})
        }

        composeTestRule.onNodeWithText("Tell me about hobby 1.").assertExists()
    }

    @Test
    fun unavailableSpeech_showsGuidanceAndDisablesModelSentenceButton() {
        val viewModel = buildViewModel(speechAvailability = SpeechAvailability.Unavailable)
        composeTestRule.setContent {
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = {},
                onBack = {},
                canRecordAudio = false,
                recordAudioPermissionDenied = true
            )
        }

        composeTestRule.onNodeWithText("이 기기에서는 영어 음성 재생을 사용할 수 없습니다.").assertExists()
        composeTestRule.onNodeWithText("모범 문장 듣기").assertIsNotEnabled()
    }

    @Test
    fun tappingNextOnLastQuestion_triggersOnSetComplete() {
        var completed = false
        val viewModel = buildViewModel(questionCount = 1)
        composeTestRule.setContent {
            PracticeScreen(viewModel = viewModel, onSetComplete = { completed = true }, onBack = {})
        }

        composeTestRule.onNodeWithText("다음 문제").performClick()

        composeTestRule.waitUntil(timeoutMillis = 2_000) { completed }
    }

    @Test
    fun tappingRecordWithoutPermission_requestsPermissionInsteadOfStartingRecorder() {
        var permissionRequested = false
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = {},
                onBack = {},
                canRecordAudio = false,
                recordAudioPermissionDenied = false,
                onRequestRecordAudioPermission = { permissionRequested = true }
            )
        }

        composeTestRule.onNodeWithText("녹음 시작").performClick()

        assertTrue(permissionRequested)
        composeTestRule.onNodeWithText("녹음 시작").assertExists()
    }

    @Test
    fun deniedPermission_keepsModelSentenceAvailableWithoutRecording() {
        val viewModel = buildViewModel(questionCount = 1)
        composeTestRule.setContent {
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = {},
                onBack = {},
                canRecordAudio = false,
                recordAudioPermissionDenied = true,
                onRequestRecordAudioPermission = {}
            )
        }

        composeTestRule.onNodeWithText("마이크 권한 없이 모범 문장으로 연습할 수 있습니다.").assertExists()
        composeTestRule.onNodeWithText("Sentence 1.").assertExists()
        composeTestRule.onNodeWithText("모범 문장 듣기").assertExists()
    }
}
