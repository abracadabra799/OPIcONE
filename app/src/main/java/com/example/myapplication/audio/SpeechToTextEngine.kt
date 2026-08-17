package com.example.myapplication.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

interface SpeechToTextEngine {
    fun isAvailable(): Boolean
    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)
    fun stopListening()
    fun release()
}

class AndroidSpeechToTextEngine(private val context: Context) : SpeechToTextEngine {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    override fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    override fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 음성을 인식하지 못했습니다."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간이 초과되었습니다."
                            SpeechRecognizer.ERROR_AUDIO -> "오디오 녹음 중 오류가 발생했습니다."
                            else -> "음성 인식 오류 (코드 $error)"
                        }
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        onResult(text)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        if (text.isNotBlank()) {
                            onResult(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                isListening = true
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                isListening = false
                onError(e.message ?: "음성 인식을 시작할 수 없습니다.")
            }
        }
    }

    override fun stopListening() {
        mainHandler.post {
            if (isListening) {
                try {
                    speechRecognizer?.stopListening()
                } catch (_: Exception) {}
                isListening = false
            }
        }
    }

    override fun release() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
            speechRecognizer = null
            isListening = false
        }
    }
}
