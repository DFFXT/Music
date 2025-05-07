package com.web.moudle.songSheetEntry.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.*
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.config.Shortcut
import com.web.misc.DrawableItemDecoration
import com.web.misc.GapItemDecoration
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.songSheetEntry.adapter.SheetTagAdapter
import com.web.moudle.songSheetEntry.adapter.SongSheetListAdapter
import com.web.moudle.songSheetEntry.bean.SongSheetInfoBox
import com.web.moudle.songSheetEntry.bean.Songlist
import com.web.moudle.songSheetEntry.model.SongSheetViewModel
import com.music.m.R
import com.music.m.databinding.ActivitySongSheetEntryBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SongSheetActivity : BaseViewBindingActivity<ActivitySongSheetEntryBinding>() {
    private lateinit var model: SongSheetViewModel
    private lateinit var sheetId: String
    private var page = 0

    private val songList = ArrayList<Songlist>()

    override fun getLayoutId(): Int = R.layout.activity_song_sheet_entry

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        sheetId = intent.getStringExtra(INTENT_DATA)!!
        model = ViewModelProviders.of(this)[SongSheetViewModel::class.java]
        model.songSheetInfo.observe(this, Observer<BaseNetBean<SongSheetInfoBox>> {
            if (it == null || it.error_code != 22000) {
                binding.rootView.showError()
                return@Observer
            }
            if (it.result.have_more == 0) {
                binding.srlSheetSong.setEnableLoadMore(false)
            }
            page++
            bitmapColorSet(it.result.info.list_pic_middle, binding.ivSheetIcon, binding.collapseToolbarLayout)
            binding.tvSheetName.text = it.result.info.list_title
            binding.tvSheetCreateTime.text = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(it.result.info.createtime * 1000))
            if (Shortcut.isStrictEmpty(it.result.info.list_desc)) {
                binding.tvIntroductionLabel.visibility = View.GONE
                binding.exIntroduction.visibility = View.GONE
            } else {
                binding.exIntroduction.text = it.result.info.list_desc
            }

            val tagArray = it.result.info.list_tag.split(",")
            initTag(tagArray)
            initSongList(it.result.songlist)
            binding.srlSheetSong.finishLoadMore()
            binding.rootView.showContent()
        })

        binding.rootView.showLoading(true)
        model.getSongSheetInfo(sheetId, page)

        binding.srlSheetSong.setRefreshFooter(ClassicsFooter(this))
        binding.srlSheetSong.setEnableRefresh(false)
        binding.srlSheetSong.setOnLoadMoreListener {
            model.getSongSheetInfo(sheetId, page)
        }
    }

    private fun initTag(tags: List<String>) {
        binding.rvTag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvTag.addItemDecoration(GapItemDecoration(left = 18, top = 5, remainEndPadding = true))
        binding.rvTag.adapter = SheetTagAdapter(this, tags)
    }

    private fun initSongList(songList: List<Songlist>) {
        if (this.songList.size == 0) {
            this.songList.addAll(songList)
            binding.rvSheetSong.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            binding.rvSheetSong.addItemDecoration(
                DrawableItemDecoration(
                    left = ViewUtil.dpToPx(20f),
                    right = ViewUtil.dpToPx(15f),
                    bottom = 10,
                    drawable = getDrawable(R.drawable.dash_line_1px)!!,
                    orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
                )
            )
            binding.rvSheetSong.adapter = SongSheetListAdapter(this, this.songList)
        } else {
            this.songList.addAll(songList)
            binding.rvSheetSong.adapter?.notifyDataSetChanged()
            binding.rvSheetSong.requestLayout()
        }
    }

    companion object {
        @JvmStatic
        val INTENT_DATA = "_data"
        @JvmStatic
        fun actionStart(ctx: Context, sheetID: String) {
            val intent = Intent(ctx, SongSheetActivity::class.java)
            intent.putExtra(INTENT_DATA, sheetID)
            ctx.startActivity(intent)
        }
    }
}