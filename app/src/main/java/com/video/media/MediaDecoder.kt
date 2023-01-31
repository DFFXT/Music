package com.video.media

import android.media.MediaCodec
import android.media.MediaExtractor

class MediaDecoder(private val mediaCodec: MediaCodec, private val mediaExtractor: MediaExtractor) : Runnable {
    private val buffer = MediaCodec.BufferInfo()
    var decoding = false
    override fun run() {
        while (decoding) {
            val inputIndex = mediaCodec.dequeueInputBuffer(0)
            if (inputIndex >= 0) {
                val byteBuffer = mediaCodec.getInputBuffer(inputIndex)
                //mediaCodec.
            }
        }
    }
}