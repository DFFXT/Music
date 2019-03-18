package com.web.moudle.music.page.recommend

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.billboard.BillBoardActivity
import com.web.moudle.billboradDetail.NetMusicListActivity
import com.web.moudle.music.page.BaseMusicPage
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.songSheetEntry.ui.SongSheetActivity
import com.web.moudle.videoEntry.ui.VideoEntryActivity
import com.web.web.R
import io.flutter.facade.Flutter
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterView
import kotlinx.android.synthetic.main.fragment_recommend.view.*

class RecommendPage : BaseMusicPage() {
    private var flutterView: FlutterView? = null

    companion object {
        const val pageName = "recommendPage"
    }

    override fun setConnect(connect: MusicPlay.Connect) {

    }

    override fun getPageName(): String = pageName

    override fun getLayoutId(): Int = R.layout.fragment_recommend


    override fun setTitle(textView: TextView) {
        textView.setText(R.string.page_Internet)
        textView.setCompoundDrawables(null, null, null, null)
    }

    override fun initView(rootView: View) {
        flutterView = Flutter.createView(activity!!, lifecycle, "RecommendView")
        MethodChannel(flutterView, "recommend/io").setMethodCallHandler { methodCall, _ ->
            when (methodCall.method) {
                "todayRecommend" -> {
                    NetMusicListActivity.actionStartRecommend(context!!, getString(R.string.todayRecommend))
                }
                "billboard" -> {
                    BillBoardActivity.actionStart(context!!)
                }
                "actionStart_SongSheetActivity" -> {
                    SongSheetActivity.actionStart(context!!, methodCall.arguments.toString())
                }
                "actionStart_AlbumEntryActivity" -> {
                    AlbumEntryActivity.actionStart(context!!, methodCall.arguments.toString())
                }
                "actionStart_NetMusicListActivity" -> {
                    val json = JSON.parseObject(methodCall.arguments.toString())
                    NetMusicListActivity.actionStartSingerMusic(context!!, json.getString("title"), json.getString("id"))
                }
                "actionStart_Video" -> {
                    VideoEntryActivity.actionStart(context!!, videoId = methodCall.arguments.toString())
                }
                "actionStart_MusicDetailActivity"->{
                    MusicDetailActivity.actionStart(context!!,methodCall.arguments.toString())
                }

            }
        }
        (rootView.layout_box as ViewGroup).addView(flutterView)
    }


}