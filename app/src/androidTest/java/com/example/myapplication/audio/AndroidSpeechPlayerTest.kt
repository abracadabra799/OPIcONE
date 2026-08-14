package com.example.myapplication.audio

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

// TTS engine availability varies by emulator/device, so these are smoke tests:
// the assertion is that the wrapper never throws, not that audio is produced.
@RunWith(AndroidJUnit4::class)
class AndroidSpeechPlayerTest {

    @Test
    fun speak_beforeInitCompletes_doesNotThrow() {
        val player = AndroidSpeechPlayer(ApplicationProvider.getApplicationContext())

        player.speak("Hello there.")
        player.release()
    }

    @Test
    fun release_calledTwice_doesNotThrow() {
        val player = AndroidSpeechPlayer(ApplicationProvider.getApplicationContext())

        player.release()
        player.release()
    }
}
