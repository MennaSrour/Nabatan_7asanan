package com.menna.nabata_7asena.core.audio

import android.content.Context
import android.media.MediaPlayer
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaPlayer: MediaPlayer? = null
    private var introPlayer: MediaPlayer? = null

    fun playTaskSound(item: HomeUiState.UiTaskItem, onCompletion: () -> Unit) {
        if (item.category == TaskCategory.EXTRA) {
            onCompletion()
            return
        }

        val mainSoundResId = getSoundResId(item)
        if (mainSoundResId == null) {
            onCompletion()
            return
        }

        playSequence(mainSoundResId, onCompletion)
    }

    fun playWelcomeSound(onCompletion: () -> Unit = {}) {
        playSingleSound(R.raw.welcome_kids, onCompletion)
    }

    fun playCelebrationSound(onCompletion: () -> Unit = {}) {
        playSingleSound(R.raw.big_star_voice, onCompletion)
    }

    fun playCorrectAnswerSound(onCompletion: () -> Unit = {}) {
        playSingleSound(R.raw.good, onCompletion)
    }

    fun playAlarmSound(soundResId: Int, onCompletion: () -> Unit = {}) {
        playSequence(soundResId, onCompletion)
    }

    private fun playSequence(mainSoundResId: Int, onCompletion: () -> Unit) {
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
                    onCompletion()
                }

                introPlayer?.start()
            } else {
                mediaPlayer?.setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    onCompletion()
                }
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion()
        }
    }

    private fun playSingleSound(resId: Int, onCompletion: () -> Unit) {
        stopAnyPlayingSound()
        try {
            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    onCompletion()
                }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion()
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

    private fun getSoundResId(item: HomeUiState.UiTaskItem): Int? {
        return if (item.category == TaskCategory.PRAYER) {
            when (item.title) {
                "الفجر" -> R.raw.fajr
                "الظهر" -> R.raw.zuhr
                "العصر" -> R.raw.asr
                "المغرب" -> R.raw.sound_normal
                "العشاء" -> R.raw.ishaa
                else -> R.raw.sound_normal
            }
        } else {
            when (item.category) {
                TaskCategory.QURAN -> R.raw.werd
                TaskCategory.AZKAR -> when {
                    item.title.contains("سبحان الله وبحمده") -> R.raw.subhan_allah_wa_behamdeh
                    item.title.contains("لا حول ولا قوة إلا بالله") -> R.raw.la_hawla
                    item.title.contains("لا إله إلا الله") -> R.raw.la_ilah_ila_allah
                    item.title.contains("استغفر الله") -> R.raw.yala_zekr
                    item.title.contains("الله أكبر") -> R.raw.allahu_akbar
                    item.title.contains("الحمد لله") || item.title.contains("الحمدلله") -> R.raw.alhamdulillah
                    item.title.contains("سبحان الله") -> R.raw.subhan_allah
                    else -> R.raw.yala_zekr
                }
                TaskCategory.CHALLENGE -> R.raw.mohima
                else -> null
            }
        }
    }
}