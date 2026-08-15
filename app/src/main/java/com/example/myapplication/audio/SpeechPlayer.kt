package com.example.myapplication.audio

import kotlinx.coroutines.flow.StateFlow

sealed interface SpeechAvailability {
    data object Initializing : SpeechAvailability
    data object Available : SpeechAvailability
    data object Unavailable : SpeechAvailability
}

interface SpeechPlayer {
    val availability: StateFlow<SpeechAvailability>
    fun speak(text: String)
    fun release()
}
