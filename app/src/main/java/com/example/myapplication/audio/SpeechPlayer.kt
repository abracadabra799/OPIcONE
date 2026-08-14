package com.example.myapplication.audio

interface SpeechPlayer {
    fun speak(text: String)
    fun release()
}
