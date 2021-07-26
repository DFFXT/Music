package com.web.moudle.setting.lyrics

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.*
import com.web.common.constant.AppConfig
import com.web.common.tool.ColorPickerDialog
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.other.FloatLyricsManager
import com.web.web.R
import kotlinx.android.synthetic.main.activity_lyrics_setting.*

class LyricsSettingActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_lyrics_setting

    private val colorList = ResUtil.getIntArray(R.array.lyricsColorArray).asList()
    private val lyricsSample = arrayListOf<LyricsLine>()
    private val checkListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (FloatLyricsManager.havePermission(baseContext)) {
                enableLyricsOverlap(baseContext, isChecked)
            } else {
                FloatLyricsManager.requestPermission(baseContext)
                sw_lyricsOverlayWindow.setOnCheckedChangeListener(null)
                sw_lyricsOverlayWindow.isChecked = false
                sw_lyricsOverlayWindow.setOnCheckedChangeListener(this)
            }
        }
    }
    init {
        ResUtil.getStringArray(R.array.lyricsSample).forEachIndexed { index, string ->
            val line = LyricsLine()
            line.line = string
            line.time = index * 1000
            lyricsSample.add(line)
        }
    }

    override fun initView() {
        sw_lyricsOverlayWindow.isChecked = lyricsOverlap()

        sw_lyricsOverlayWindow.setOnCheckedChangeListener(checkListener)

        sw_lyricsLock.isChecked = isFloatWindowLocked()
        sw_lyricsLock.setOnCheckedChangeListener { _, isChecked ->
            setFloatWindowLocked(this, isChecked)
        }

        // **recyclerView设置
        rv_color.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rv_color.addItemDecoration(GapItemDecoration(10, 10, 10, 10, remainTopPadding = true, remainEndPadding = true, remainBottomPadding = true))
        rv_color.adapter = object : BaseAdapter<Int>(colorList) {
            override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Int) {
                (holder.itemView as ImageView).setImageDrawable(ColorDrawable(item))
                holder.itemView.setOnClickListener {
                    lyricsColorChange(item)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return createViewHolder()
            }
        }

        rv_focusColor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rv_focusColor.addItemDecoration(GapItemDecoration(10, 10, 10, 10, remainTopPadding = true, remainEndPadding = true, remainBottomPadding = true))
        rv_focusColor.adapter = object : BaseAdapter<Int>(colorList) {
            override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Int) {
                (holder.itemView as ImageView).setImageDrawable(ColorDrawable(item))
                holder.itemView.setOnClickListener {
                    lyricsFocusColorChange(item)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return createViewHolder()
            }
        }

        // **显示当前颜色
        v_lyricsColor.setImageDrawable(ColorDrawable(getLyricsColor()))
        v_lyricsColor.setOnClickListener {
            colorPick(getLyricsColor()) {
                lyricsColorChange(it)
            }
        }
        v_lyricsFocusColor.setImageDrawable(ColorDrawable(getLyricsFocusColor()))
        v_lyricsFocusColor.setOnClickListener {
            colorPick(getLyricsFocusColor()) {
                lyricsFocusColorChange(it)
            }
        }

        sb_lyricsSize.max = ResUtil.getSize(R.dimen.textSize_large) - ResUtil.getSize(R.dimen.textSize_min)
        sb_lyricsSize.progress = getLyricsSize() - ResUtil.getSize(R.dimen.textSize_min)
        sb_lyricsSize.onSeekTo(
            onChange = {
                setLyricsSize(it + ResUtil.getSize(R.dimen.textSize_min))
                lv_lyrics.setTextSize(getLyricsSize().toFloat())
            }
        )

        lv_lyrics.textColor = getLyricsColor()
        lv_lyrics.setTextSize(getLyricsSize().toFloat())
        lv_lyrics.setTextFocusColor(getLyricsFocusColor())
        lv_lyrics.setCanScroll(false)
        lv_lyrics.lyrics = lyricsSample
        lv_lyrics.maxLineAccount = 10
        lv_lyrics.setShowLineAccount(10)
        lv_lyrics.setCurrentTimeImmediately(lyricsSample[3].time)
    }

    private fun lyricsColorChange(@ColorInt color: Int) {
        setLyricsColor(color)
        v_lyricsColor.setImageDrawable(ColorDrawable(color))
        lv_lyrics.textColor = getLyricsColor()
    }
    private fun lyricsFocusColorChange(@ColorInt color: Int) {
        setLyricsFocusColor(color)
        v_lyricsFocusColor.setImageDrawable(ColorDrawable(color))
        lv_lyrics.setTextFocusColor(color)
    }

    /**
     * 颜色选择器
     */
    private fun colorPick(@ColorInt currentColor: Int, callback: ((color: Int) -> Unit)) {
        val colorPicker = ColorPickerDialog(this, currentColor)
        colorPicker.positiveButtonListener = {
            callback(it)
        }
        colorPicker.show()
    }

    private fun createViewHolder(): BaseViewHolder {
        val padding = ViewUtil.dpToPx(3f)
        val v = ImageView(this@LyricsSettingActivity)
        v.background = getDrawable(R.drawable.border_1dp)
        v.elevation = ViewUtil.dpToPx(3f).toFloat()
        val lp = ViewGroup.LayoutParams(ViewUtil.dpToPx(40f), ViewUtil.dpToPx(40f))
        v.layoutParams = lp
        v.setPadding(padding, padding, padding, padding)
        return BaseViewHolder(v)
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            val intent = Intent(ctx, LyricsSettingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            ctx.startActivity(intent)
        }

        @JvmStatic
        fun getLyricsColor(): Int = AppConfig.lyricsColor
        @JvmStatic
        fun getLyricsFocusColor(): Int = AppConfig.lyricsFocusColor
        @JvmStatic
        fun getLyricsSize(): Int = AppConfig.lyricsSize

        @JvmStatic
        fun lyricsOverlap(): Boolean = AppConfig.lyricsOverlapOpen

        @JvmStatic
        fun setLyricsColor(@ColorInt color: Int) {
            AppConfig.lyricsColor = color
        }
        @JvmStatic
        fun setLyricsFocusColor(@ColorInt color: Int) {
            AppConfig.lyricsFocusColor = color
        }
        @JvmStatic
        fun setLyricsSize(size: Int) {
            AppConfig.lyricsSize = size
        }

        @JvmStatic
        fun enableLyricsOverlap(ctx: Context, enable: Boolean, forceRefresh: Boolean = true) {
            AppConfig.lyricsOverlapOpen = enable
            if (forceRefresh) {
                FloatLyricsManager.configChange(ctx)
            }
        }

        @JvmStatic
        fun isFloatWindowLocked(): Boolean = AppConfig.isFloatWindowLocked

        @JvmStatic
        fun setFloatWindowLocked(ctx: Context, locked: Boolean) {
            AppConfig.isFloatWindowLocked = locked
            FloatLyricsManager.configChange(ctx)
        }
    }
}