package com.web.moudle.songSheetEntry.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
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
import kotlinx.android.synthetic.main.activity_song_sheet_entry.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SongSheetActivity:BaseActivity() {
    private lateinit var model:SongSheetViewModel
    private lateinit var sheetId:String
    private var page=0

    private val songList=ArrayList<Songlist>()

    override fun getLayoutId(): Int= R.layout.activity_song_sheet_entry

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        sheetId=intent.getStringExtra(INTENT_DATA)!!
        model=ViewModelProviders.of(this)[SongSheetViewModel::class.java]
        model.songSheetInfo.observe(this, Observer<BaseNetBean<SongSheetInfoBox>> {
            if(it==null||it.error_code!=22000){
                rootView.showError()
                return@Observer
            }
            if(it.result.have_more==0){
                srl_sheetSong.setEnableLoadMore(false)
            }
            page++
            bitmapColorSet(it.result.info.list_pic_middle,iv_sheetIcon,collapseToolbarLayout)
            tv_sheetName.text=it.result.info.list_title
            tv_sheetCreateTime.text=SimpleDateFormat("YYYY-MM-dd", Locale.CHINA).format(Date(it.result.info.createtime*1000))
            if(Shortcut.isStrictEmpty(it.result.info.list_desc)){
                tv_introductionLabel.visibility= View.GONE
                ex_introduction.visibility=View.GONE
            }else{
                ex_introduction.text=it.result.info.list_desc
            }

            val tagArray=it.result.info.list_tag.split(",")
            initTag(tagArray)
            initSongList(it.result.songlist)
            srl_sheetSong.finishLoadMore()
            rootView.showContent()
        })

        rootView.showLoading(true)
        model.getSongSheetInfo(sheetId,page)

        srl_sheetSong.setRefreshFooter(ClassicsFooter(this))
        srl_sheetSong.setEnableRefresh(false)
        srl_sheetSong.setOnLoadMoreListener {
            model.getSongSheetInfo(sheetId,page)
        }
    }

    private fun initTag(tags:List<String>){
        rv_tag.layoutManager= LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rv_tag.addItemDecoration(GapItemDecoration(left = 18,top = 5,remainEndPadding = true))
        rv_tag.adapter=SheetTagAdapter(this,tags)
    }
    private fun initSongList(songList:List<Songlist>){
        if(this.songList.size==0){
            this.songList.addAll(songList)
            rv_sheetSong.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(this)
            rv_sheetSong.addItemDecoration(DrawableItemDecoration(left = ViewUtil.dpToPx(20f),right = ViewUtil.dpToPx(15f),bottom = 10,drawable = getDrawable(R.drawable.dash_line_1px)!!,orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
            rv_sheetSong.adapter= SongSheetListAdapter(this, this.songList)
        }else{
            this.songList.addAll(songList)
            rv_sheetSong.adapter?.notifyDataSetChanged()
            rv_sheetSong.requestLayout()
        }
    }



    companion object {
        @JvmStatic
        val INTENT_DATA="_data"
        @JvmStatic
        fun actionStart(ctx:Context,sheetID:String){
            val intent=Intent(ctx,SongSheetActivity::class.java)
            intent.putExtra(INTENT_DATA,sheetID)
            ctx.startActivity(intent)
        }
    }
}