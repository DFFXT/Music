package com.web.moudle.music.page.local.control.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.web.common.util.ResUtil.getFileSize
import com.web.common.util.ResUtil.timeFormat
import com.web.data.Music
import com.web.misc.InputItem
import com.web.web.R

/**
 * 歌曲详情弹窗
 */
class LocalMusicDetailWindow(
    private val music: Music,
    private val nameSaveListener: () -> Unit,
    private val artistSaveListener: () -> Unit
) : DialogFragment(R.layout.layout_music_detail) {

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val tvAbPath = v.findViewById<TextView>(R.id.tv_abPath)
        tvAbPath.text = music.path
        v.findViewById<TextView>(R.id.tv_duration).text = timeFormat("mm:ss", music.duration.toLong())
        v.findViewById<TextView>(R.id.tv_size).text = getFileSize(music.size)
        val musicName: InputItem = v.findViewById(R.id.layout_musicName)
        musicName.setText(music.musicName)
        musicName.listenerSave = { text: String ->
            if (text != music.musicName && music.rename(text, music.singer)) {
                tvAbPath.text = music.path
                nameSaveListener.invoke()
            }
            music.musicName
        }
        val artistName: InputItem = v.findViewById(R.id.layout_artistName)
        artistName.setText(music.singer)
        artistName.listenerSave = { text: String ->
            if (text != music.singer && music.rename(music.musicName, text)) {
                tvAbPath.text = music.path
                artistSaveListener.invoke()
            }
            music.singer
        }
        tvAbPath.setOnClickListener {
            val clipboard = it.context.getSystemService(ClipboardManager::class.java)
            clipboard.setPrimaryClip(ClipData.newPlainText("音乐路径", tvAbPath.text))
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.attributes?.apply {
            dimAmount = 0.3f
            dialog?.window?.attributes = this
        }
    }
}