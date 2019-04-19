package com.web.moudle.music.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.web.common.base.BaseDragHelper
import com.web.common.base.PlayerObserver
import com.web.common.base.log
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.data.Music
import com.web.moudle.setting.lyrics.LyricsSettingActivity
import com.web.web.R
import kotlinx.android.synthetic.main.layout_float_lyrics.view.*

/**
 * 歌词浮窗
 */
class FloatLyricsManager (private val appContext:Context,private val connect:MusicPlay.Connect){

    private var rootView:ViewGroup?=null
    private var layoutOp:ViewGroup?=null
    private var observer:PlayerObserver?=null
    private var timeImmediately=true

    private var lp:WindowManager.LayoutParams?=null
    private var enableMove=false

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        rootView=LayoutInflater.from(appContext).inflate(R.layout.layout_float_lyrics,null,false) as ViewGroup
        layoutOp=rootView!!.layout_op
        rootView?.removeViewAt(0)

        observer=object :PlayerObserver(){
            override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
                if(music==null){
                    return
                }
                rootView?.lv_lyrics?.lyrics=LyricsAnalysis(GetFiles().readText(music.lyricsPath)).lyrics
            }

            override fun currentTime(group: Int, child: Int, time: Int) {
                if(timeImmediately){
                    timeImmediately=false
                    rootView?.lv_lyrics?.setCurrentTimeImmediately(time)
                }else{
                    rootView?.lv_lyrics?.setCurrentTime(time)
                }
            }

            override fun play() {
                layoutOp?.iv_play?.setImageResource(R.drawable.icon_play_white)
            }

            override fun pause() {
                layoutOp?.iv_play?.setImageResource(R.drawable.icon_pause_white_fill)
            }
        }
        connect.addObserver(null,observer)
        connect.getPlayerInfo()


        rootView?.setOnTouchListener(object :BaseDragHelper(){
            override fun longOnClick(x: Float, y: Float): Boolean {
                rootView?.foreground=ResUtil.getDrawable(R.drawable.arrow_focus)
                return false
            }
            override fun longClickMove(dx: Float, dy: Float) {
                if(!enableMove)return
                lp?.x = lp?.x?.plus(dx.toInt())
                lp?.y = lp?.y?.plus(dy.toInt())
                updatePosition(lp!!)
            }

            override fun longClickUpNoMove(x: Float, y: Float) {
                rootView?.foreground=null
            }

            override fun longClickUpMoved(x: Float, y: Float) {
                rootView?.foreground=null
            }

            override fun onClick(x: Float, y: Float) {
                enableMove=!enableMove
                if(enableMove){
                    rootView?.addView(layoutOp,0)
                    rootView?.setBackgroundColor(0x44666666)

                }else{
                    rootView?.setBackgroundColor(0)
                    rootView?.removeViewAt(0)
                    rootView?.foreground=null
                }

            }
        })

        layoutOp?.iv_sizeIncrease?.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize()+2)
        }
        layoutOp?.iv_sizeDecrease?.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize()-2)
        }
        layoutOp?.iv_pre?.setOnClickListener {
            connect.pre()
        }
        layoutOp?.iv_play?.setOnClickListener {
            connect.changePlayerPlayingStatus()
        }
        layoutOp?.iv_next?.setOnClickListener {
            connect.next()
        }
        layoutOp?.iv_setting?.setOnClickListener {
            LyricsSettingActivity.actionStart(appContext)
        }

    }
    private fun setLyricsSize(size:Int){
        val max= ResUtil.getSize(R.dimen.textSize_large)
        val min= ResUtil.getSize(R.dimen.textSize_min)
        val mSize = when {
            size<min -> min
            size>max -> max
            else -> size
        }
        LyricsSettingActivity.setLyricsSize(mSize)
        rootView?.lv_lyrics?.setTextSize(mSize.toFloat())
    }

    fun open(){
        timeImmediately=true
        if(rootView!=null)return
        init()
        val wm=appContext.getSystemService(WindowManager::class.java)
        lp=WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp?.type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }else{
            lp?.type=WindowManager.LayoutParams.TYPE_PHONE
        }
        lp?.alpha=0.9f
        lp?.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp?.width= (ViewUtil.screenWidth()*0.8).toInt()
        lp?.height=ViewUtil.dpToPx(40f+38)
        lp?.verticalWeight=0f
        wm.addView(rootView,lp)


        rootView?.lv_lyrics?.ableToTouch=false
        refreshConfig()
    }
    private fun updatePosition(lp:WindowManager.LayoutParams){
        val wm=appContext.getSystemService(WindowManager::class.java)
        wm.updateViewLayout(rootView,lp)
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
    }
}