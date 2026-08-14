package com.example.myapplication.audio

import java.io.File

interface VoiceRecorder {
    fun startRecording(outputFile: File)
    fun stopRecording()
    fun isRecording(): Boolean
}
