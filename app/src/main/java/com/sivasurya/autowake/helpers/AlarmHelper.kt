package com.sivasurya.autowake.helpers

import android.content.Context
import android.media.MediaPlayer
import com.sivasurya.autowake.R

object AlarmHelper {

    private var mediaPlayer: MediaPlayer? = null

    fun playAlarm(context: Context) {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarmsound)
            mediaPlayer?.isLooping = true
        }

        if (mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    fun stopAlarm() {

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }

        mediaPlayer = null
    }
}