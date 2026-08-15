package com.example.myapplication.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class AndroidSpeechPlayer(context: Context) : SpeechPlayer {

    private val lock = Any()
    private val mutableAvailability = MutableStateFlow<SpeechAvailability>(SpeechAvailability.Initializing)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability

    private var isReleased = false
    private var pendingInitializationStatus: Int? = null
    private var textToSpeech: TextToSpeech? = null

    init {
        val createdTextToSpeech = TextToSpeech(context.applicationContext) { status ->
            handleInitialization(status)
        }

        val pendingStatus = synchronized(lock) {
            textToSpeech = createdTextToSpeech
            pendingInitializationStatus.also { pendingInitializationStatus = null }
        }
        pendingStatus?.let(::handleInitialization)
    }

    private fun handleInitialization(status: Int) {
        synchronized(lock) {
            if (isReleased) {
                mutableAvailability.value = SpeechAvailability.Unavailable
                return
            }

            val speechEngine = textToSpeech
            if (speechEngine == null) {
                pendingInitializationStatus = status
                return
            }

            val languageResult = if (status == TextToSpeech.SUCCESS) {
                speechEngine.setLanguage(Locale.US)
            } else {
                TextToSpeech.ERROR
            }
            mutableAvailability.value = when (languageResult) {
                TextToSpeech.LANG_MISSING_DATA,
                TextToSpeech.LANG_NOT_SUPPORTED -> SpeechAvailability.Unavailable
                TextToSpeech.ERROR -> SpeechAvailability.Unavailable
                else -> SpeechAvailability.Available
            }
        }
    }

    override fun speak(text: String) {
        synchronized(lock) {
            if (mutableAvailability.value != SpeechAvailability.Available) return
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "opic_model_sentence")
        }
    }

    override fun release() {
        val speechEngine = synchronized(lock) {
            if (isReleased) return
            isReleased = true
            mutableAvailability.value = SpeechAvailability.Unavailable
            textToSpeech.also { textToSpeech = null }
        }
        speechEngine?.stop()
        speechEngine?.shutdown()
    }
}
