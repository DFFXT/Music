package com.web.moudle.lockScreen.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.constraint.ConstraintLayout
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.BaseActivity
import com.web.common.base.BaseGlideTarget
import com.web.common.constant.Constant
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.config.Shortcut
import com.web.moudle.music.model.lyrics.model.LyricsLine
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.preference.SP
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity
import com.web.web.R
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.util.*
import javax.microedition.khronos.opengles.GL


class LockScreenActivity : BaseActivity() ,View.OnClickListener{
    private lateinit var browserCompat: MediaBrowserCompat
    private var controller:MediaControllerCompat?=null
    private var stateCompat:PlaybackStateCompat?=null
    private val lyrics= arrayListOf<LyricsLine>()
    private val arrowBitmap = arrayOfNulls<Bitmap>(2)
    private lateinit var params:ConstraintLayout.LayoutParams
    private var marginEnd=0
    val receiver=object :ResultReceiver(Handler(Looper.getMainLooper())){
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultData==null)return
            when(resultCode){
                MusicPlay.COMMAND_RESULT_CODE_CURRENT_POSITION->{//**返回进度
                    val pos=resultData.getInt(MusicPlay.COMMAND_SEND_SINGLE_DATA,123)
                    lyricView_lockScreen.setCurrentTimeImmediately(pos)
                }
                MusicPlay.COMMAND_RESULT_CODE_STATUS->{//**返回状态
                    val play= resultData.getBoolean(MusicPlay.COMMAND_SEND_SINGLE_DATA,false)
                    iv_lockScreen_status.setImageResource(
                            if(play) R.drawable.icon_play_white
                            else R.drawable.icon_pause_white
                    )
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_lock_screen
    }

    override fun initView() {
        ViewUtil.transparentStatusBar(window)
        val mode=LockScreenSettingActivity.getMode()
        when(mode){
            BG_MODE_IMAGE-> {

                ImageLoad.load(LockScreenSettingActivity.getBgImagePath()).into(object : BaseGlideTarget(ViewUtil.screenWidth(),ViewUtil.screenHeight()) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val rs = RenderScript.create(this@LockScreenActivity)
                        val blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                        val from=(resource as BitmapDrawable).bitmap
                        val allIn=Allocation.createFromBitmap(rs,from)
                        val to=Bitmap.createBitmap(from.width,from.height,Bitmap.Config.ARGB_4444)
                        val allOut=Allocation.createFromBitmap(rs,to)
                        blur.setRadius(12f)
                        blur.setInput(allIn)
                        blur.forEach(allOut)
                        allOut.copyTo(to)
                        rs.destroy()
                        Intent.ACTION_PICK
                        rootView_lockScreenActivity.background=BitmapDrawable(resources,to)
                    }
                })
            }
            else ->rootView_lockScreenActivity.setBackgroundColor(LockScreenSettingActivity.getBgColor())
        }
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.N_MR1)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        else
            setShowWhenLocked(true)
        showTime()
        iv_lockScreen_pre.setOnClickListener(this)
        iv_lockScreen_status.setOnClickListener(this)
        iv_lockScreen_next.setOnClickListener(this)

        iv_lockScreen_leftArrow.setImageBitmap(arrowBitmap[0])
        iv_lockScreen_rightArrow.setImageBitmap(arrowBitmap[1])
        params=iv_lockScreen_leftArrow.layoutParams as ConstraintLayout.LayoutParams
        marginEnd=params.marginEnd
        //**歌词行数
        lyricView_lockScreen.maxLineAccount=5
        lyricView_lockScreen.setCanScroll(false)


        rootView_lockScreenActivity.setOnTouchListener(object :View.OnTouchListener{
            private val maxDistance=ViewUtil.screenWidth()/3
            private var preX:Float=0f
            private var preY:Float=0f
            private var originX:Float=0f
            private var originY:Float=0f
            private var animatorRun=false
            private var preDis=0f
            private var marginAdd=true
            override fun onTouch(v: View?, e: MotionEvent?): Boolean {
                when(e?.action){
                    MotionEvent.ACTION_DOWN->{
                        preX=e.rawX
                        preY=e.rawY
                        originX=preX
                        originY=preY
                        animatorRun=false
                        preDis=0f
                    }
                    MotionEvent.ACTION_MOVE->{
                        if(Math.abs(e.rawX-originX)>maxDistance){
                            finish()
                        }
                        val alpha=(maxDistance*2-Math.abs(e.rawX-originX))/(maxDistance*2)
                        rootView_lockScreenActivity.alpha=alpha
                        if(!marginAdd&&params.marginEnd<=marginEnd){
                            marginAdd=true
                        }
                        val thisDis=e.rawX-preX
                        if(preDis*thisDis<0){//**转折点
                            marginAdd=!marginAdd
                        }
                        if(marginAdd)
                            params.marginEnd+=Math.abs(thisDis*1.5f).toInt()
                        else
                            params.marginEnd-=Math.abs(thisDis*1.5f).toInt()
                        if(params.marginEnd<marginEnd){
                            params.marginEnd=marginEnd
                        }
                        iv_lockScreen_leftArrow.layoutParams=params
                        preDis=thisDis
                        preX=e.rawX
                        preY=e.rawY

                    }
                    MotionEvent.ACTION_UP->{
                        animatorRun=true
                        val dis=marginEnd-params.marginEnd
                        val start=params.marginEnd
                        val animator=ValueAnimator.ofFloat(rootView_lockScreenActivity.alpha,1f)
                        animator.addUpdateListener {
                            if(!animatorRun){
                                it.cancel()
                                return@addUpdateListener
                            }
                            val value=it.animatedValue as Float
                            rootView_lockScreenActivity.alpha=value
                            params.marginEnd=start+(value*dis).toInt()
                            iv_lockScreen_leftArrow.layoutParams=params
                        }
                        animator.duration=300
                        animator.start()
                    }
                }
                return true
            }
        })

        browserCompat= MediaBrowserCompat(this, ComponentName(this, MusicPlay::class.java),
                object : MediaBrowserCompat.ConnectionCallback() {
                    override fun onConnected() {
                        super.onConnected()
                        try {
                            controller = MediaControllerCompat(this@LockScreenActivity, browserCompat.sessionToken)
                            controller?.registerCallback(object : MediaControllerCompat.Callback() {
                                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                                    stateCompat=state
                                    when(state?.actions){//**歌曲状态改变
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE->{
                                            if(state.state==PlaybackStateCompat.STATE_PLAYING){
                                                iv_lockScreen_status.setImageResource(R.drawable.icon_play_white)
                                            }else{
                                                iv_lockScreen_status.setImageResource(R.drawable.icon_pause_white)
                                            }
                                        }
                                        PlaybackStateCompat.ACTION_SEEK_TO->{
                                            val pos=state.extras?.getInt(MusicPlay.MUSIC_DURING,0)
                                            pos?.let {
                                                lyricView_lockScreen.setCurrentTime(it)
                                            }
                                        }
                                    }

                                }

                                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {//**歌曲信息改变
                                    musicSwitch(metadata)
                                }
                            })
                            controller?.sendCommand(MusicPlay.COMMAND_GET_CURRENT_POSITION,null,receiver)
                            iv_lockScreen_status.setImageResource(
                                    if(controller?.playbackState?.state==PlaybackStateCompat.STATE_PLAYING)R.drawable.icon_play_white
                                    else R.drawable.icon_pause_white
                            )
                            controller?.sendCommand(MusicPlay.COMMAND_GET_STATUS,null,receiver)
                            musicSwitch(controller?.metadata)
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }

                    }
                    override fun onConnectionFailed() {
                        MToast.showToast(this@LockScreenActivity,ResUtil.getString(R.string.failedConnectToPlayer))
                    }
                }, null)
    }

    /**
     * 音乐切换
     */
    private fun musicSwitch(metadata:MediaMetadataCompat?){
        tv_lockScreen_musicName.text=metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        tv_lockScreen_singerName.text=metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val path=metadata?.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)
        lyrics.clear()
        if (Shortcut.fileExsist(path)) {//---存在歌词
            val lyricsAnalysis = LyricsAnalysis(GetFiles().readText(path))
            lyrics.addAll(lyricsAnalysis.lyrics)
        } else {//**没找到歌词
            val line = LyricsLine()
            line.time = 0
            line.line = ResUtil.getString(R.string.lyrics_noLyrics)
            lyrics.add(line)
        }
        lyricView_lockScreen.lyrics=lyrics
    }

    override fun loadData(bundle: Bundle?) {
        val d=ResUtil.getDrawable(R.drawable.icon_lockscreen_slide_arrow)
        arrowBitmap[0]=ResUtil.getBitmapFromDrawable(d)
        val matrix=Matrix()
        matrix.postRotate(180f)
        arrowBitmap[1]=ResUtil.bitmapOp(arrowBitmap[0]!!,matrix)


    }

    /**
     * 设置显示的时间日期
     */
    private fun showTime(){
        val calendar=Calendar.getInstance()
        val time=calendar.get(Calendar.HOUR_OF_DAY).toString()+":"+calendar.get(Calendar.MINUTE)
        tv_lockScreen_time.text=time
        val date=(calendar.get(Calendar.MONTH)+1).toString()+"月"+calendar.get(Calendar.DAY_OF_MONTH)
        tv_lockScreen_date.text=date
        val dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)
        val week=ResUtil.getString(R.string.text_week)+when(dayOfWeek-1){
            0-> ResUtil.getString(R.string.text_0)
            1-> ResUtil.getString(R.string.text_1)
            2-> ResUtil.getString(R.string.text_2)
            3-> ResUtil.getString(R.string.text_3)
            4-> ResUtil.getString(R.string.text_4)
            5-> ResUtil.getString(R.string.text_5)
            6-> ResUtil.getString(R.string.text_6)
            else -> ""
        }
        tv_lockScreen_week.text=week
        val second=calendar.get(Calendar.SECOND)
        rootView_lockScreenActivity.postDelayed({//**隔一定时间更新时间
            showTime()
        },60_000L-second*1000)
    }
    override fun onClick(v:View){
        when(v.id){
            R.id.iv_lockScreen_pre-> controller?.transportControls?.skipToPrevious()
            R.id.iv_lockScreen_next-> controller?.transportControls?.skipToNext()
            R.id.iv_lockScreen_status-> {
                if(stateCompat?.state==PlaybackStateCompat.STATE_PLAYING){
                    controller?.transportControls?.pause()
                }else{
                    controller?.transportControls?.play()
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        browserCompat.connect()
    }

    override fun onStop() {
        super.onStop()
        browserCompat.disconnect()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_HOME->return true
            KeyEvent.KEYCODE_BACK->return true
        }
        return super.onKeyDown(keyCode, event)
    }
    companion object {
        const val BG_MODE_COLOR="color"
        const val BG_MODE_IMAGE="image"
        fun actionStart(ctx:Context){
            val intent=Intent(ctx,LockScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        }
    }
}
