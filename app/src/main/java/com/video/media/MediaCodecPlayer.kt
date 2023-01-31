package com.video.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log

/**
 * mediaCodec播放器
 */
class MediaCodecPlayer : IPlayer {
    private val mTag = "MediaCodecPlayer"
    private var mMediaCodec: MediaCodec? = null
    private val mMediaExtractor = MediaExtractor()
    private var mMediaFormat: MediaFormat? = null
    override fun load(path: String) {
        mMediaExtractor.setDataSource(path)
    }

    override fun play() {

    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    override fun seekTo(mills: Int) {
        TODO("Not yet implemented")
    }

    override fun prepare() {
        for (i in 0 until mMediaExtractor.trackCount) {
            val mediaFormat = mMediaExtractor.getTrackFormat(i)
            if (mediaFormat.getString(MediaFormat.KEY_MIME)?.startsWith("audio") == true) {
                mMediaFormat = mediaFormat
                mMediaExtractor.selectTrack(i)
                break
            }
        }
        if (mMediaFormat == null) {
            Log.i(mTag, "不支持的音频文件")
            return
        }
        mMediaFormat?.let {
            mMediaCodec = MediaCodec.createByCodecName(it.getString(MediaFormat.KEY_MIME)!!)

        }
    }
}