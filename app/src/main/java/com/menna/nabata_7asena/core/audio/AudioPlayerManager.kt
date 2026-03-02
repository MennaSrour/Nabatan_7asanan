package com.menna.nabata_7asena.core.audio

import android.content.Context
import android.media.MediaPlayer
import com.menna.nabata_7asena.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor() {

    private var mediaPlayer: MediaPlayer? = null
    private var introPlayer: MediaPlayer? = null

    
    fun playSequence(context: Context, mainSoundResId: Int) {
        stopAnyPlayingSound() 

        try {
            
            val introResId = R.raw.intro_sound

            introPlayer = MediaPlayer.create(context, introResId)
            mediaPlayer = MediaPlayer.create(context, mainSoundResId)

            if (introPlayer != null) {
                
                introPlayer?.setOnCompletionListener {
                    it.release()
                    introPlayer = null
                    mediaPlayer?.start()
                }

                
                mediaPlayer?.setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }

                introPlayer?.start()
            } else {
                
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAnyPlayingSound() {
        try {
            if (introPlayer?.isPlaying == true) introPlayer?.stop()
            introPlayer?.release()
            introPlayer = null

            if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}