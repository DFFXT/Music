package com.web.moudle.music.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri.fromParts
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.web.common.base.BaseDragHelper
import com.web.common.base.PlayerObserver
import com.web.common.constant.Constant
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.data.Music
import com.web.moudle.preference.SP
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
    private val dragHelper=object :BaseDragHelper(){
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
            setFloatWindowX(lp!!.x)
            setFloatWindowY(lp!!.y)
            rootView?.foreground=null
        }

        override fun onClick(x: Float, y: Float) {
            enableMove=!enableMove
            if(enableMove){
                rootView?.addView(layoutOp,0)
                rootView?.setBackgroundColor(0x66666666)

            }else{
                rootView?.setBackgroundColor(0)
                rootView?.removeViewAt(0)
                rootView?.foreground=null
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        enableMove=false
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
        connect.getPlayerInfo(null)


        if(!LyricsSettingActivity.isFloatWindowLocked()){
            rootView?.setOnTouchListener(dragHelper)
        }


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
        if(!requestPermission(appContext))return
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
        //**必须，不然在切换桌面是会出现黑色区域
        lp?.format=PixelFormat.TRANSPARENT

        if (LyricsSettingActivity.isFloatWindowLocked()){
            lp?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }else{
            lp?.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        lp?.width= (ViewUtil.screenWidth()*0.8).toInt()
        lp?.height=ViewUtil.dpToPx(40f+38)
        lp?.x= getFloatWindowX()
        lp?.y= getFloatWindowY()
        lp?.verticalWeight=0f
        wm.addView(rootView,lp)


        rootView?.lv_lyrics?.ableToTouch=false
        refreshLyrics()
    }
    private fun updatePosition(lp:WindowManager.LayoutParams){
        val wm=appContext.getSystemService(WindowManager::class.java)
        wm.updateViewLayout(rootView,lp)
    }
    fun refresh(){
        close()
        open()
    }
    fun close(){
        if(rootView==null)return
        val wm=appContext.getSystemService(WindowManager::class.java)
        wm.removeView(rootView)
        rootView=null
    }
    private fun refreshLyrics(){
        rootView?.lv_lyrics?.textColor=LyricsSettingActivity.getLyricsColor()
        rootView?.lv_lyrics?.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        rootView?.lv_lyrics?.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        rootView?.lv_lyrics?.setBlod(true)
    }

    companion object{

        @JvmStatic
        fun configChange(ctx:Context){
            MusicPlay.floatWindowChange(ctx)
        }

        @JvmStatic
        fun getFloatWindowX():Int{
            return SP.getInt(Constant.spName,Constant.SpKey.floatWindowX,0)
        }
        @JvmStatic
        fun getFloatWindowY():Int{
            val def=ViewUtil.screenHeight()/2-ViewUtil.dpToPx(36f)
            return SP.getInt(Constant.spName,Constant.SpKey.floatWindowY,-def)
        }
        @JvmStatic
        fun setFloatWindowX(x:Int){
            SP.putValue(Constant.spName,Constant.SpKey.floatWindowX,x)
        }
        @JvmStatic
        fun setFloatWindowY(y:Int){
            SP.putValue(Constant.spName,Constant.SpKey.floatWindowY,y)
        }

        @JvmStatic
        fun havePermission(appContext: Context):Boolean{
            return Settings.canDrawOverlays(appContext)
        }

        @JvmStatic
        fun requestPermission(appContext: Context):Boolean{
            if(!Settings.canDrawOverlays(appContext)){
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = fromParts("package", appContext.packageName, null)
                appContext.startActivity(intent)
                return false
            }
            return true
        }

    }
}