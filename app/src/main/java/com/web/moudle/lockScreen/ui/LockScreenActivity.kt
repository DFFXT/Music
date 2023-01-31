package com.web.moudle.lockScreen.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.BaseActivity
import com.web.common.base.BaseGlideTarget
import com.web.common.base.PlayerObserver
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.config.Shortcut
import com.web.data.Music
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.PlayerConnection
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity
import com.music.m.R
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.util.*


class LockScreenActivity : BaseActivity() ,View.OnClickListener{
    private val lyrics= arrayListOf<LyricsLine>()
    private val arrowBitmap = arrayOfNulls<Bitmap>(2)
    private lateinit var params: ConstraintLayout.LayoutParams
    private var marginEnd=0
    private var connect: PlayerConnection?=null
    private var connection:ServiceConnection=object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            connect=service as PlayerConnection
            connect?.addObserver(this@LockScreenActivity,observer)
            connect?.getPlayerInfo(this@LockScreenActivity)
        }
    }
    val observer=object :PlayerObserver(){
        override fun onLoad(music: Music?, maxTime: Int) {
            if(music==null)return
            tv_lockScreen_musicName.text=music.musicName
            tv_lockScreen_singerName.text=music.singer
            val path=music.lyricsPath
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

        override fun onPlay() {
            iv_lockScreen_status.setImageResource(R.drawable.icon_play_white)
        }

        override fun onPause() {
            iv_lockScreen_status.setImageResource(R.drawable.icon_pause_white)
        }

        override fun onCurrentTime(duration: Int, maxTime: Int) {
            lyricView_lockScreen.setCurrentTimeImmediately(duration)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_lock_screen
    }

    override fun initView() {
        ViewUtil.transparentStatusBar(window)
        loadData()
        val intent=Intent(this, NewPlayer::class.java)
        intent.action= ActionControlPlug.BIND
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
        when(LockScreenSettingActivity.getMode()){
            LockScreenSettingActivity.BG_MODE_IMAGE-> {

                ImageLoad.loadAsBitmap(LockScreenSettingActivity.getBgImagePath()).into(object : BaseGlideTarget(ViewUtil.screenWidth(),ViewUtil.screenHeight()) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        rootView_lockScreenActivity.background=BitmapDrawable(resources,ImageLoad.buildBlurBitmap(resource,10f))
                    }
                })
            }
            else ->rootView_lockScreenActivity.setBackgroundColor(LockScreenSettingActivity.getBgColor())
        }
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.N_MR1)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
            }
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
            @SuppressLint("ClickableViewAccessibility")
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
    }


    private fun loadData() {
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
            R.id.iv_lockScreen_pre-> connect?.pre()
            R.id.iv_lockScreen_next-> connect?.next(false)
            R.id.iv_lockScreen_status-> {
                connect?.changePlayerPlayingStatus()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val manager=getSystemService(KeyguardManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.requestDismissKeyguard(this,null)
        }
        unbindService(connection)
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

        fun actionStart(ctx:Context){
            val intent=Intent(ctx,LockScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val compat=ActivityOptions.makeCustomAnimation(ctx,0,0)
            ctx.startActivity(intent,compat.toBundle())
        }
    }
}
