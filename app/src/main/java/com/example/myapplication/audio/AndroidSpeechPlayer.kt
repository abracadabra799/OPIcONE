package com.example.myapplication.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AndroidSpeechPlayer(context: Context) : SpeechPlayer {

    private var isReady = false

    private val textToSpeech: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            textToSpeech.language = Locale.US
        }
    }

    override fun speak(text: String) {
        if (!isReady) return
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "opic_model_sentence")
    }

    override fun release() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        isReady = false
    }
}
