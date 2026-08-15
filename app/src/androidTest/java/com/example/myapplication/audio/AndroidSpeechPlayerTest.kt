package com.example.myapplication.audio

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
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

    @Test
    fun initialization_reachesTerminalAvailability() {
        val player = AndroidSpeechPlayer(ApplicationProvider.getApplicationContext())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val reachedTerminalAvailability = waitUntil(timeoutMillis = 5_000) {
            player.availability.value != SpeechAvailability.Initializing
        }

        assertTrue(reachedTerminalAvailability)
        assertTrue(
            player.availability.value == SpeechAvailability.Available ||
                player.availability.value == SpeechAvailability.Unavailable
        )
        player.release()
    }

    private fun waitUntil(timeoutMillis: Long, condition: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return true
            Thread.sleep(25)
        }
        return condition()
    }
}
