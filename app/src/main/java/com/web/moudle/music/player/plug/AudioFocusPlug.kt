package com.web.moudle.music.player.plug

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import com.web.app.MyApplication
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayInterface
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 音频焦点管理
 */
class AudioFocusPlug(private val control: IMusicControl) : ServiceLifeCycle, PlayInterface {
    private var isGain = false
    private val audioManager = MyApplication.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var lossType: Int? = null

    private val l = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                isGain = true
                if (lossType == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || lossType == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    control.play()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            -> {
                control.pause()
                lossType = focusChange
            }
        }
    }
    private var request: Any? = null

    override fun onCreate() {
    }

    override fun onPlay() {
        if (!isGain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val request = AudioFocusRequest
                    .Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    .setOnAudioFocusChangeListener(l)
                    .build()
                this.request = request
                if (audioManager.requestAudioFocus(request) != AudioManager.AUDIOFOCUS_GAIN) {
                }
            } else {
                if (audioManager.requestAudioFocus(l, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_GAIN) {
                }
            }
        }
    }

    override fun onPause() {
        // 暂停后释放音频焦点，否则会恢复
        releaseAudioFocus()
    }

    private fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (request as? AudioFocusRequest)?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(l)
        }
        isGain = false
        lossType = null
    }

    override fun onDestroy() {
        releaseAudioFocus()
    }
}
