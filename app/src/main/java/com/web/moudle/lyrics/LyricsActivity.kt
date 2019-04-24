package com.web.moudle.lyrics

import android.animation.Animator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.BitmapDrawable
import android.media.audiofx.Visualizer
import android.os.IBinder
import android.view.KeyEvent
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.Ticker
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.config.Shortcut
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.Music
import com.web.misc.DrawableItemDecoration
import com.web.misc.imageDraw.WaveDraw
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.musicEntry.adapter.CommentAdapter
import com.web.moudle.musicEntry.bean.CommentItem
import com.web.moudle.musicEntry.model.DetailMusicViewModel
import com.web.moudle.service.FileDownloadService
import com.web.moudle.setting.lyrics.LyricsSettingActivity
import com.web.web.BuildConfig
import com.web.web.R
import kotlinx.android.synthetic.main.activity_music_detail.*
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.android.synthetic.main.music_control_big.view.*
import kotlinx.android.synthetic.main.music_lyrics_view.*
import kotlinx.android.synthetic.main.music_lyrics_view.rootView
import kotlinx.android.synthetic.main.music_lyrics_view.topBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.File

@ObsoleteCoroutinesApi
class LyricsActivity : BaseActivity() {
    private var connect: MusicPlay.Connect? = null
    private var visualizer:Visualizer?=null
    private val list = ArrayList<LyricsLine>()
    private var connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            connect = service as MusicPlay.Connect
            connect?.addObserver(this@LyricsActivity, observer)
            connect?.getPlayerInfo(this@LyricsActivity)


            visualizer=Visualizer(connect?.mediaPlayId!!)
            visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[0]
            visualizer!!.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            visualizer!!.setDataCaptureListener(object :Visualizer.OnDataCaptureListener{
                override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                }

                override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
                    waveDraw.byteArray=waveform
                }
            },Visualizer.getMaxCaptureRate(),true,true)
            visualizer!!.enabled=true
        }
    }
    private var actionStart = true
    private var canScroll=true
    private var rotation=0f
    private val waveDraw=WaveDraw()
    private var colorList:IntArray?=null
    //**dispatchers一定要是main不能是default，不然会导致过一段时间整个应用无法点击，类似于卡死
    private val tick=Ticker(30,0,Dispatchers.Main){
        rotation+=1f
        if(rotation>360){
            rotation -= 360f
        }
        iv_artistIcon.rotation=rotation

    }
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
            actionStart = true
            val bitmap=connect?.config?.bitmap?:ResUtil.getBitmapFromResoucs(R.drawable.singer_default_icon)
            val mBitmap=bitmap.copy(bitmap.config,false)
            iv_artistIcon.setImageBitmap(bitmap)
            rootView.background=BitmapDrawable(resources,ImageLoad.buildBlurBitmap(mBitmap,14f))

            layout_musicControl.iv_love.isSelected=music?.isLike?:false
            if(music is InternetMusicForPlay){
                layout_musicControl.card_love.alpha=0.5f
                layout_musicControl.card_love.cardElevation=0f
            }else{
                layout_musicControl.card_love.alpha=1f
                layout_musicControl.card_love.cardElevation=ViewUtil.dpToPx(2f).toFloat()
            }
            if(music!!.song_id!=""){
                card_comment.visibility=View.VISIBLE
            }else{
                card_comment.visibility=View.GONE
            }
            loadLyrics(music)
            play()
        }

        override fun play() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_play_white)
            tick.start()
        }

        override fun pause() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_pause_white_fill)
            tick.stop()
        }

        override fun currentTime(group: Int, child: Int, time: Int) {
            if (actionStart) {//**进入activity时需要立即同步
                lv_lyrics.setCurrentTimeImmediately(time)
                actionStart = false
            } else {
                lv_lyrics.setCurrentTime(time)
            }

        }

        override fun playTypeChanged(playType: PlayerConfig.PlayType?) {
            layout_musicControl.iv_playType.setImageResource(when(playType){
                PlayerConfig.PlayType.ALL_LOOP->R.drawable.music_type_all_loop
                PlayerConfig.PlayType.ONE_LOOP->R.drawable.music_type_one_loop
                PlayerConfig.PlayType.ALL_ONCE->R.drawable.music_type_all_once
                PlayerConfig.PlayType.ONE_ONCE->R.drawable.music_type_one_once
                PlayerConfig.PlayType.RANDOM->R.drawable.random_icon
                else ->R.drawable.random_icon
            })
        }

        override fun musicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
            if(origin== PlayerConfig.MusicOrigin.INTERNET){
                card_download.visibility=View.VISIBLE
            }else{
                card_download.visibility=View.GONE
            }
        }
    }

    override fun enableSwipeToBack(): Boolean =true

    override fun getLayoutId(): Int = R.layout.music_lyrics_view

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        riv_wave.afterDraw=waveDraw
        lv_lyrics.textColor = LyricsSettingActivity.getLyricsColor()
        lv_lyrics.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        lv_lyrics.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        lv_lyrics.lyrics = list
        topBar.setEndImageListener(View.OnClickListener {
            if (canScroll) {
                canScroll= false
                topBar.setEndImage(R.drawable.locked)
            } else {
                canScroll=true
                topBar.setEndImage(R.drawable.unlock)
            }
            lv_lyrics.setCanScroll(canScroll)
        })



        card_comment.setOnClickListener {
            showComment()
        }


        card_download.setOnClickListener {
            val m= (connect?.config?.music ?: return@setOnClickListener) as? InternetMusicForPlay
                    ?: return@setOnClickListener
            val im=InternetMusicDetail(
                    "",
                    m.musicName,
                    m.singer,
                    null,
                    m.album,
                    m.duration,
                    m.size,
                    m.lrcLink,
                    m.path,
                    m.imgAddress,
                    m.suffix
            )
            FileDownloadService.addTask(this,im)

        }


        layout_musicControl.iv_playType.setOnClickListener {
            connect?.changePlayType()
        }
        layout_musicControl.iv_love.setOnClickListener {
            val m= connect?.config?.music ?: return@setOnClickListener
            if(m is InternetMusicForPlay)return@setOnClickListener
            if (m.isLike) {
                SongSheetManager.removeLike(m)
            } else {
                SongSheetManager.setAsLike(m)
            }
            layout_musicControl.iv_love.isSelected=m.isLike
            connect?.refreshList()
        }

        layout_musicControl.next.setOnClickListener {
            connect?.next()
        }
        layout_musicControl.iv_play.setOnClickListener {
            connect?.changePlayerPlayingStatus()
        }
        layout_musicControl.pre.setOnClickListener {
            connect?.pre()
        }
        iv_musicEffect.setOnClickListener {
            EqualizerActivity.actionStart(this)
        }
        lv_lyrics.setSeekListener { seekTo ->
            connect?.seekTo(seekTo)
            true
        }

        iv_setting.setOnClickListener {
            toggleSettingBox()
        }

        iv_sizeIncrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize()+2)
        }
        iv_sizeDecrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize()-2)
        }
        iv_lyricsBg.setOnClickListener {
            val color=LyricsSettingActivity.getLyricsColor()
            val nextColor=getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsColor(nextColor)
            lv_lyrics.textColor=nextColor
        }
        iv_lyricsFore.setOnClickListener {
            val color=LyricsSettingActivity.getLyricsFocusColor()
            val nextColor=getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsFocusColor(nextColor)
            lv_lyrics.setTextFocusColor(nextColor)
        }
        iv_share.setOnClickListener {
            if(connect==null)return@setOnClickListener
            val intent=Intent(Intent.ACTION_SEND)

            val music=connect!!.config.music
            if(connect!!.config.musicOrigin== PlayerConfig.MusicOrigin.INTERNET){
                intent.type="text/plain"
                intent.putExtra(Intent.EXTRA_TEXT,music.path)
            }else{
                intent.putExtra(Intent.EXTRA_STREAM,
                        FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID,File(connect!!.config.music.path)))
                intent.type="video/*"
            }
            startActivity(Intent.createChooser(intent,"title"))

        }




        intent = Intent(this, MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        bindService(intent, connection, Context.BIND_AUTO_CREATE)




        addKeyEventListener {
            if(it.keyCode==KeyEvent.KEYCODE_BACK&&layout_commentBox.visibility==View.VISIBLE){
                hideComment()
                return@addKeyEventListener true
            }
            return@addKeyEventListener false
        }

    }
    private fun getNextLyricsColor(currentColor:Int):Int{
        var index=0
        if(colorList==null) colorList=ResUtil.getIntArray(R.array.lyricsColorArray)
        colorList!!.forEachIndexed { mIndex, c ->
            if(c==currentColor){
                index= mIndex+1
                if(index>=colorList!!.size){
                    index=0
                }
                return@forEachIndexed
            }
        }
        return colorList!![index]
    }
    private fun setLyricsSize(size:Int){
        val max=ResUtil.getSize(R.dimen.textSize_large)
        val min=ResUtil.getSize(R.dimen.textSize_min)
        val mSize = when {
            size<min -> min
            size>max -> max
            else -> size
        }
        LyricsSettingActivity.setLyricsSize(mSize)
        lv_lyrics.setTextSize(mSize.toFloat())
    }
    private fun toggleSettingBox(){
        val duration=300
        val lp=layout_setting.layoutParams
        if(layout_setting.height==0){
            //**加号不能放在第二行
            val height= layout_setting.childCount*ViewUtil.dpToFloatPx(26f)+ (layout_setting.childCount+1)*ViewUtil.dpToFloatPx(6f)+layout_setting.childCount
            ViewUtil.animator(layout_setting,0,height.toInt(),duration, {
                lp.height=it.animatedValue as Int
                layout_setting.layoutParams=lp
            },null)
        }else{
            ViewUtil.animator(layout_setting,layout_setting.height,0,duration,{
                lp.height=it.animatedValue as Int
                layout_setting.layoutParams=lp
            },null)
        }
    }


    private fun loadLyrics(music: Music?) {//--设置歌词内容
        if (music == null) return
        topBar.setMainTitle(music.musicName)
        list.clear()
        if (Shortcut.fileExsist(music.lyricsPath)) {//---存在歌词
            val lyricsAnalysis = LyricsAnalysis(GetFiles().readText(music.lyricsPath))
            list.addAll(lyricsAnalysis.lyrics)
        } else {//**没找到歌词
            val line = LyricsLine()
            line.time = 0
            line.line = ResUtil.getString(R.string.lyrics_noLyrics)
            list.add(line)
        }
        lv_lyrics!!.lyrics = list
    }


    private var songId=""
    private var page:Int=0
    private var pageSize=30
    private var model: DetailMusicViewModel?=null
    private val commentList=ArrayList<CommentItem>()
    private val adapter= CommentAdapter(commentList)
    private fun showComment(){
        if(model==null){
            model=ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
            layout_commentBox.setOnClickListener {
                hideComment()
            }
            initView(layout_comment!!)
        }
        if(songId!=connect!!.config!!.music.song_id){
            page=0
            commentList.clear()
            adapter.notifyDataSetChanged()
            songId=connect!!.config!!.music.song_id
            model?.getComment(songId,page,pageSize)
            layout_commentBox.visibility=View.VISIBLE
            rootView.post {
                layout_comment!!.rv_comment.showLoading()
            }
        }

        layout_commentBox.visibility=View.VISIBLE
        ViewUtil.animator(layout_comment,
                0f,1f,400, {
            layout_commentBox!!.alpha=it.animatedValue as Float
        },null)
    }
    private fun initView(rootView: View) {
        model?.comment?.observe(this, Observer {
            if(it==null)return@Observer
            when(it.code){
                LiveDataWrapper.CODE_OK->{
                    page++
                    rootView.tv_commentNum.text=ResUtil.getString(R.string.commentNum,it.value.commentlist_last_nums)
                    if(it.value.commentlist_hot!=null){
                        commentList.addAll(it.value.commentlist_hot)
                    }
                    if(it.value.commentlist_last!=null){
                        commentList.addAll(it.value.commentlist_last)
                    }
                    adapter.notifyDataSetChanged()
                    layout_comment!!.rv_comment.showContent()
                }
                LiveDataWrapper.CODE_NO_DATA->{
                    rootView.tv_commentNum.text=getString(R.string.commentNum,"0")
                    layout_comment.rv_comment.showError(getString(R.string.noData))
                }
                LiveDataWrapper.CODE_ERROR->{
                    layout_comment!!.rv_comment.showError()
                }
            }
        })
        rootView.rv_comment.layoutManager= LinearLayoutManager(rootView.context)
        rootView.rv_comment.adapter=adapter
        rv_comment.addItemDecoration(
                DrawableItemDecoration(ViewUtil.dpToPx(10f),0,ViewUtil.dpToPx(10f),2,
                        RecyclerView.VERTICAL,getDrawable(R.drawable.recycler_divider)))

    }
    private fun hideComment(){
        ViewUtil.animator(layout_comment,layout_comment!!.top,
                ViewUtil.screenHeight(),400, {
                    layout_comment!!.top=it.animatedValue as Int
                },object :BaseAnimatorListener(){
            override fun onAnimationEnd(animation: Animator) {
                layout_commentBox.visibility=View.GONE
            }
        })
    }



    override fun onDestroy() {
        tick.stop()
        unbindService(connection)
        visualizer?.release()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            ctx.startActivity(Intent(ctx, LyricsActivity::class.java))
        }
    }


}