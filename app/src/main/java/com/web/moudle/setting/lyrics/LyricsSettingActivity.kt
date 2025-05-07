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
import com.music.m.R
import com.music.m.databinding.ActivityLyricsSettingBinding
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.BaseViewHolder
import com.web.common.base.onSeekTo
import com.web.common.constant.AppConfig
import com.web.common.tool.ColorPickerDialog
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.other.FloatLyricsManager

class LyricsSettingActivity : BaseViewBindingActivity<ActivityLyricsSettingBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_lyrics_setting

    private val colorList = ResUtil.getIntArray(R.array.lyricsColorArray).asList()
    private val lyricsSample = arrayListOf<LyricsLine>()
    private val checkListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (FloatLyricsManager.havePermission(baseContext)) {
                enableLyricsOverlap(baseContext, isChecked)
            } else {
                FloatLyricsManager.requestPermission(baseContext)
                binding.swLyricsOverlayWindow.setOnCheckedChangeListener(null)
                binding.swLyricsOverlayWindow.isChecked = false
                binding.swLyricsOverlayWindow.setOnCheckedChangeListener(this)
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
        binding.swLyricsOverlayWindow.isChecked = lyricsOverlap()

        binding.swLyricsOverlayWindow.setOnCheckedChangeListener(checkListener)

        binding.swLyricsLock.isChecked = isFloatWindowLocked()

        binding.swLyricsLock.setOnCheckedChangeListener { _, isChecked ->
            setFloatWindowLocked(this, isChecked)
        }

        // **recyclerView设置
        binding.rvColor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvColor.addItemDecoration(GapItemDecoration(10, 10, 10, 10, remainTopPadding = true, remainEndPadding = true, remainBottomPadding = true))
        binding.rvColor.adapter = object : BaseAdapter<Int>(colorList) {
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

        binding.rvFocusColor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvFocusColor.addItemDecoration(GapItemDecoration(10, 10, 10, 10, remainTopPadding = true, remainEndPadding = true, remainBottomPadding = true))
        binding.rvFocusColor.adapter = object : BaseAdapter<Int>(colorList) {
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
        binding.vLyricsColor.setImageDrawable(ColorDrawable(getLyricsColor()))
        binding.vLyricsColor.setOnClickListener {
            colorPick(getLyricsColor()) {
                lyricsColorChange(it)
            }
        }
        binding.vLyricsFocusColor.setImageDrawable(ColorDrawable(getLyricsFocusColor()))
        binding.vLyricsFocusColor.setOnClickListener {
            colorPick(getLyricsFocusColor()) {
                lyricsFocusColorChange(it)
            }
        }

        binding.sbLyricsSize.max = ResUtil.getSize(R.dimen.textSize_large) - ResUtil.getSize(R.dimen.textSize_min)
        binding.sbLyricsSize.progress = getLyricsSize() - ResUtil.getSize(R.dimen.textSize_min)
        binding.sbLyricsSize.onSeekTo(
            onChange = {
                setLyricsSize(it + ResUtil.getSize(R.dimen.textSize_min))
                binding.lvLyrics.setTextSize(getLyricsSize().toFloat())
            }
        )

        binding.lvLyrics.textColor = getLyricsColor()
        binding.lvLyrics.setTextSize(getLyricsSize().toFloat())
        binding.lvLyrics.setTextFocusColor(getLyricsFocusColor())
        binding.lvLyrics.setCanScroll(false)
        binding.lvLyrics.lyrics = lyricsSample
        binding.lvLyrics.maxLineAccount = 10
        binding.lvLyrics.setShowLineAccount(10)
        binding.lvLyrics.setCurrentTimeImmediately(lyricsSample[3].time)
    }

    private fun lyricsColorChange(@ColorInt color: Int) {
        setLyricsColor(color)
        binding.vLyricsColor.setImageDrawable(ColorDrawable(color))
        binding.lvLyrics.textColor = getLyricsColor()
    }
    private fun lyricsFocusColorChange(@ColorInt color: Int) {
        setLyricsFocusColor(color)
        binding.vLyricsFocusColor.setImageDrawable(ColorDrawable(color))
        binding.lvLyrics.setTextFocusColor(color)
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