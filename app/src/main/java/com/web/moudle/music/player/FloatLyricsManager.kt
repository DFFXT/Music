package com.web.moudle.music.player

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.web.common.base.PlayerObserver
import com.web.common.util.ViewUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.data.Music
import com.web.moudle.setting.lyrics.LyricsSettingActivity
import com.web.web.R
import kotlinx.android.synthetic.main.layout_float_lyrics.view.*

class FloatLyricsManager (private val appContext:Context,private val connect:MusicPlay.Connect){

    private var rootView:View?=null
    private var observer:PlayerObserver?=null
    private var m:Music?=null
    private var timeImmediately=true
    private fun init(){
        rootView=LayoutInflater.from(appContext).inflate(R.layout.layout_float_lyrics,null,false)

        observer=object :PlayerObserver(){
            override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
                if(m==null){
                    m=music
                }else if(m?.path?.equals(music?.path)!!){
                    return
                }
                rootView?.lv_lyrics?.lyrics=LyricsAnalysis(GetFiles().readText(music?.lyricsPath)).lyrics
            }

            override fun currentTime(group: Int, child: Int, time: Int) {
                if(timeImmediately){
                    timeImmediately=false
                    rootView?.lv_lyrics?.setCurrentTimeImmediately(time)
                }else{
                    rootView?.lv_lyrics?.setCurrentTime(time)
                }
            }
        }
        connect.addObserver(null,observer)
        connect.getPlayerInfo()
    }

    fun open(){
        timeImmediately=true
        if(rootView!=null)return
        init()
        val wm=appContext.getSystemService(WindowManager::class.java)
        val lp=WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }else{
            lp.type=WindowManager.LayoutParams.TYPE_PHONE
        }
        lp.alpha=0.9f
        lp.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width= (ViewUtil.screenWidth()*0.8).toInt()
        lp.height=ViewUtil.dpToPx(50f)
        lp.verticalWeight=0f
        wm.addView(rootView,lp)

        rootView?.lv_lyrics?.ableToTouch=false
        refreshConfig()
    }
    fun close(){
        if(rootView==null)return
        val wm=appContext.getSystemService(WindowManager::class.java)
        wm.removeView(rootView)
        rootView=null
    }
    fun refreshConfig(){
        rootView?.lv_lyrics?.textColor=LyricsSettingActivity.getLyricsColor()
        rootView?.lv_lyrics?.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        rootView?.lv_lyrics?.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        rootView?.lv_lyrics?.setClipPaddingTop(ViewUtil.dpToPx(6f))
    }
}