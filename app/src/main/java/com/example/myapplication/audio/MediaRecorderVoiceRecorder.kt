package com.example.myapplication.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class MediaRecorderVoiceRecorder(
    private val newRecorder: () -> MediaRecorder
) : VoiceRecorder {

    constructor(context: Context) : this(
        newRecorder = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
        }
    )

    private var recorder: MediaRecorder? = null
    private var recording = false

    override fun startRecording(outputFile: File) {
        val mediaRecorder = newRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        recorder = mediaRecorder
        recording = true
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        recording = false
    }

    override fun isRecording(): Boolean = recording
}
