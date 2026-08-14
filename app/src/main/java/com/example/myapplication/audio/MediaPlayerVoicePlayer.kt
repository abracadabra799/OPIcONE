package com.example.myapplication.audio

import android.media.MediaPlayer
import java.io.File

class MediaPlayerVoicePlayer : VoicePlayer {

    private var player: MediaPlayer? = null

    override fun play(file: File) {
        stop()
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    override fun stop() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
    }
}
