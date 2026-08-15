package com.example.myapplication.audio

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class VoiceRecorderPlaybackTest {

    private companion object {
        const val RECORDING_DURATION_MS = 1_000L
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val outputFile = File(context.cacheDir, "voice_recorder_test.m4a")
    private val recorder = MediaRecorderVoiceRecorder(context)
    private val player = MediaPlayerVoicePlayer()

    @After
    fun tearDown() {
        player.stop()
        outputFile.delete()
    }

    @Test
    fun startThenStopRecording_writesANonEmptyFile() {
        recorder.startRecording(outputFile)
        assertTrue(recorder.isRecording())
        Thread.sleep(RECORDING_DURATION_MS)
        recorder.stopRecording()

        assertTrue(outputFile.exists() && outputFile.length() > 0)
        assertFalse(recorder.isRecording())
    }

    @Test
    fun recordedFile_canBePlayedBack() {
        recorder.startRecording(outputFile)
        Thread.sleep(RECORDING_DURATION_MS)
        recorder.stopRecording()

        player.play(outputFile)
        Thread.sleep(200)
        player.stop()
    }
}
