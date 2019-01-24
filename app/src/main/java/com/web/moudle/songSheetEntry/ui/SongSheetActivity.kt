package com.web.moudle.songSheetEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.web.common.base.*
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.config.Shortcut
import com.web.misc.DrawableItemDecoration
import com.web.misc.GapItemDecoration
import com.web.moudle.songSheetEntry.adapter.SheetTagAdapter
import com.web.moudle.songSheetEntry.adapter.SongSheetListAdapter
import com.web.moudle.songSheetEntry.bean.SongSheetInfo
import com.web.moudle.songSheetEntry.bean.Songlist
import com.web.moudle.songSheetEntry.model.SongSheetViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_song_sheet_entry.*
import java.text.SimpleDateFormat
import java.util.*

class SongSheetActivity:BaseActivity() {
    private lateinit var model:SongSheetViewModel
    private lateinit var sheetId:String

    override fun getLayoutId(): Int= R.layout.activity_song_sheet_entry

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        sheetId=intent.getStringExtra(INTENT_DATA)
        model=ViewModelProviders.of(this)[SongSheetViewModel::class.java]
        model.songSheetInfo.observe(this, Observer<SongSheetInfo> {
            if(it==null||it.error_code!=22000){
                rootView.showError()
                return@Observer
            }
            bitmapColorSet(it.result.info.list_pic_middle,findViewById(R.id.iv_sheetIcon),collapseToolbarLayout)
            findViewById<TextView>(R.id.tv_sheetName).text=it.result.info.list_title
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
            rootView.showContent()
        })

        rootView.showLoading()
        model.getSongSheetInfo(sheetId,0)

    }

    private fun initTag(tags:List<String>){
        rv_tag.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_tag.addItemDecoration(GapItemDecoration(left = 18,top = 5,remainEndPadding = true))
        rv_tag.adapter=SheetTagAdapter(this,tags)
    }
    private fun initSongList(songList:List<Songlist>){
        rv_sheetSong.layoutManager=LinearLayoutManager(this)
        rv_sheetSong.addItemDecoration(DrawableItemDecoration(left = ViewUtil.dpToPx(20f),right = ViewUtil.dpToPx(15f),bottom = 10,drawable = getDrawable(R.drawable.dash_line_1px)!!,orientation = LinearLayoutManager.VERTICAL))
        rv_sheetSong.adapter= SongSheetListAdapter(this, songList)
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