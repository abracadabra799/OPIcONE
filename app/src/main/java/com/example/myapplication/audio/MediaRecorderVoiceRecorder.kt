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
        val activeRecorder = recorder
        recorder = null
        recording = false
        try {
            activeRecorder?.stop()
        } catch (e: RuntimeException) {
            // stop() throws if too little data was recorded (e.g. instant tap-release) — the
            // output file may be invalid/empty in this case, which callers should already
            // tolerate, but the recorder must still be released and state must still reset.
        } finally {
            activeRecorder?.release()
        }
    }

    override fun isRecording(): Boolean = recording
}
