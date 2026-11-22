package com.menna.nabatan_7asena.core.audio

import android.content.Context
import android.media.MediaPlayer
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.platform.createResourcePlayer
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    private var introPlayer: MediaPlayer? = null

    override fun playTaskSound(item: HomeUiState.UiTaskItem, onCompletion: () -> Unit) {
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

    override fun playWelcomeSound(onCompletion: () -> Unit) {
        playSingleSound(R.raw.welcome_kids, onCompletion)
    }

    override fun playCelebrationSound(onCompletion: () -> Unit) {
        playSingleSound(R.raw.big_star_voice, onCompletion)
    }

    override fun playCorrectAnswerSound(onCompletion: () -> Unit) {
        playSingleSound(R.raw.good, onCompletion)
    }

    override fun playAlarmSound(soundResId: Int, onCompletion: () -> Unit) {
        playSequence(soundResId, onCompletion)
    }

    override fun playDuaOrZekrSound(soundResId: Int, onCompletion: () -> Unit) {
        if (soundResId == 0) {
            onCompletion()
            return
        }
        playSingleSound(soundResId, onCompletion)
    }

    private fun playSequence(mainSoundResId: Int, onCompletion: () -> Unit) {
        stopAnyPlayingSound()

        try {
            val introResId = R.raw.intro_sound
            introPlayer = createResourcePlayer(context, introResId)
            mediaPlayer = createResourcePlayer(context, mainSoundResId)

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
            android.util.Log.w("AudioPlayerManager", "Could not play audio sequence: ${e.javaClass.simpleName}")
            onCompletion()
        }
    }

    private fun playSingleSound(resId: Int, onCompletion: () -> Unit) {
        stopAnyPlayingSound()
        try {
            mediaPlayer = createResourcePlayer(context, resId)?.apply {
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    onCompletion()
                }
                start()
            }
        } catch (e: Exception) {
            android.util.Log.w("AudioPlayerManager", "Could not play bundled audio: ${e.javaClass.simpleName}")
            onCompletion()
        }
    }

    override fun stopAnyPlayingSound() {
        try {
            if (introPlayer?.isPlaying == true) introPlayer?.stop()
            introPlayer?.release()
            introPlayer = null

            if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            android.util.Log.w("AudioPlayerManager", "Could not stop audio playback: ${e.javaClass.simpleName}")
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
                else -> null
            }
        }
    }
}
