package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.audio.AndroidSpeechPlayer
import com.example.myapplication.audio.AndroidSpeechToTextEngine
import com.example.myapplication.audio.MediaPlayerVoicePlayer
import com.example.myapplication.audio.MediaRecorderVoiceRecorder
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.SpeechToTextEngine
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.MIGRATION_1_2
import com.example.myapplication.data.remote.ClaudeApiClient
import com.example.myapplication.data.remote.DefaultPracticeRepository
import com.example.myapplication.data.remote.LocalBankAiProvider
import com.example.myapplication.data.remote.MediaPipeLlmEngine
import com.example.myapplication.data.remote.OnDeviceLlmAiProvider
import com.example.myapplication.data.remote.OpenAiApiClient
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.session.PracticeSessionStore
import com.example.myapplication.data.settings.AiSettingsStore
import com.example.myapplication.data.remote.DefaultWorldNewsRepository
import com.example.myapplication.data.remote.OnDeviceNewsAnalyzer
import com.example.myapplication.data.remote.WorldNewsRepository
import com.example.myapplication.data.settings.EncryptedAiSettingsStore
import java.io.File

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "opic_practice.db"
    ).addMigrations(MIGRATION_1_2).build()

    val favoriteRepository = FavoriteRepository(database.favoriteDao())

    val aiSettingsStore: AiSettingsStore = EncryptedAiSettingsStore(appContext)

    val practiceSessionStore = PracticeSessionStore()

    private val onDeviceEngine = MediaPipeLlmEngine(appContext) {
        aiSettingsStore.getModelPath()
    }

    val worldNewsRepository: WorldNewsRepository = DefaultWorldNewsRepository(
        OnDeviceNewsAnalyzer(onDeviceEngine)
    )

    val practiceRepository: PracticeRepository = DefaultPracticeRepository(
        providers = setOf(
            LocalBankAiProvider(),
            OnDeviceLlmAiProvider(onDeviceEngine),
            ClaudeApiClient(),
            OpenAiApiClient()
        ),
        settingsStore = aiSettingsStore,
        sessionStore = practiceSessionStore
    )

    fun newSpeechPlayer(): SpeechPlayer = AndroidSpeechPlayer(appContext)

    fun newSpeechToTextEngine(): SpeechToTextEngine = AndroidSpeechToTextEngine(appContext)

    fun newVoiceRecorder(): VoiceRecorder = MediaRecorderVoiceRecorder(appContext)

    fun newVoicePlayer(): VoicePlayer = MediaPlayerVoicePlayer()

    fun newRecordingFile(): File = File(appContext.cacheDir, "current_recording.m4a")

    fun newFavoriteRecordingFile(favoriteId: Long): File =
        File(appContext.cacheDir, "favorite_recording_$favoriteId.m4a")
}
