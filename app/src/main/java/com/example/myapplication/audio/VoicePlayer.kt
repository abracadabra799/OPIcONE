package com.example.myapplication.audio

import java.io.File

interface VoicePlayer {
    fun play(file: File)
    fun stop()
}
