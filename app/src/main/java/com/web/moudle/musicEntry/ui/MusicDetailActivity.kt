package com.web.moudle.musicEntry.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.IBinder
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.Music
import com.web.misc.DrawableItemDecoration
import com.web.moudle.home.local.ListDialog
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.moudle.music.page.local.control.ui.SelectorListAlert
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.model.WWSongSheetModel
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.musicEntry.adapter.CommentAdapter
import com.web.moudle.musicEntry.bean.CommentItem
import com.web.moudle.service.FileDownloadService
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.musicEntry.model.DetailMusicViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_music_detail.*
import kotlinx.android.synthetic.main.activity_music_detail.rootView
import kotlinx.android.synthetic.main.music_navigator_control.*

class MusicDetailActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: DetailMusicViewModel
    private var connection: IMusicControl? = null
    private var serviceConnection: ServiceConnection? = null

    private var commentPage=0
    private val commentList=ArrayList<CommentItem>()
    private var adapter:CommentAdapter= CommentAdapter(commentList)
    private lateinit var data:MusicDetailInfo
    private lateinit var music:InternetMusicForPlay

    override fun getLayoutId(): Int {
        return R.layout.activity_music_detail
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)!!
        model = ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
        model.detailMusic.observe(this, Observer<LiveDataWrapper<MusicDetailInfo>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    this.data=data.value
                    val res = data.value
                    tv_musicName.text = res.songInfo.title
                    tv_mainSinger.text = res.songInfo.artistName
                    tv_duration.text = ResUtil.timeFormat("mm:ss", res.songInfo.duration.toLong() * 1000)
                    tv_album.text = res.songInfo.albumName
                    tv_publishTime.text = res.songInfo.publishTime
                    tv_publishCompany.text = res.songInfo.proxyCompany
                    tv_listenTimes.text = res.songInfo.listenTimes


                    card_info.setOnClickListener {
                        showListPop()
                    }


                    //**加载图片
                    ImageLoad.loadAsBitmap(res.songInfo.artistPic500x500).into(object : BaseGlideTarget() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            androidx.palette.graphics.Palette.from(resource).generate {
                                it?.vibrantSwatch?.let { sw ->
                                    collapseToolbarLayout.setBackgroundColor(sw.rgb)
                                }
                            }
                            iv_playIconSwitch.imageTintList= ColorStateList.valueOf(Color.WHITE)
                            iv_bigImage_detailMusicActivity.setImageBitmap(resource)
                        }
                    })
                    rootView.showContent()
                    //**获取歌词
                    model.getLyrics(res.songInfo.lrcLink)
                    music = map(res)


                    var theSameMusic = false
                    //**播放器观测者
                    val observer = object : PlayerObserver() {
                        override fun onPlay() {
                            if (theSameMusic)
                                iv_playIconSwitch.setImageResource(R.drawable.icon_play_white)
                        }

                        override fun onPause() {
                            iv_playIconSwitch.setImageResource(R.drawable.icon_pause_white)
                        }

                        override fun onLoad(m: Music?, maxTime: Int) {
                            if (m!=null&&musicEqual(music,m)) {
                                theSameMusic = true
                                onPlay()
                            } else {
                                theSameMusic = false
                                onPause()
                            }
                        }
                    }

                    val intent = Intent(this@MusicDetailActivity, NewPlayer::class.java)
                    intent.action = ActionControlPlug.BIND
                    serviceConnection = object : ServiceConnection {
                        override fun onServiceDisconnected(name: ComponentName?) {
                        }

                        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                            connection = service as IMusicControl
                            connection!!.addObserver(this@MusicDetailActivity, observer)
                            connection!!.getPlayerInfo(this@MusicDetailActivity)
                        }
                    }
                    //**连接 播放器
                    bindService(intent, serviceConnection!!, BIND_AUTO_CREATE)

                    iv_playIconSwitch.setOnClickListener {
                        connection?.let { con ->
                            if (PlayerConfig.music == music) {
                                con.changePlayerPlayingStatus()
                            } else {
                                con.play(music)
                            }
                        }
                    }


                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    rootView.showError()
                    rootView.errorClickLinsten = View.OnClickListener {
                        model.getDetail(songId = id)
                        rootView.showLoading()
                    }
                }

            }

        })
        model.lyrics.observe(this, Observer { wrapper ->
            if (wrapper == null) return@Observer
            if (wrapper.code == LiveDataWrapper.CODE_OK) {
                val builder = StringBuilder()
                wrapper.value.forEach {
                    builder.append(it.line)
                    builder.append("\n")
                }
                lyricsView.text = builder.toString()
            }
        })

        model.comment.observe(this, Observer {wrapper->
            srl_comment.finishLoadMore()
            when {
                wrapper==null -> return@Observer
                wrapper.code == LiveDataWrapper.CODE_ERROR -> {

                }
                wrapper.code == LiveDataWrapper.CODE_OK -> {
                    commentPage++
                    tv_commentNum.text=wrapper.value.commentlist_last_nums.toString()
                    if(wrapper.value.commentlist_hot!=null){
                        commentList.addAll(wrapper.value.commentlist_hot)
                    }
                    if(wrapper.value.commentlist_last!=null){
                        commentList.addAll(wrapper.value.commentlist_last)
                    }
                    if(commentList.size==wrapper.value.commentlist_last_nums){
                        srl_comment.setNoMoreData(true)
                    }
                    adapter.notifyDataSetChanged()
                }
                wrapper.code == LiveDataWrapper.CODE_NO_DATA->{
                    srl_comment.setEnableLoadMore(false)
                    layout_noMoreData.visibility=View.VISIBLE
                }
            }
        })
        WWSongSheetModel.isLikeMusic(id.toLong()){
            isLike=it
        }
        model.getDetail(id)
        model.getComment(id,commentPage, pageSize)
    }

    override fun initView() {
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)

        val toolbar = toolbar
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
            when (-dy) {
                appBar.totalScrollRange -> {//**完全折叠
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    toolbar.setBackgroundColor(getColor(R.color.themeColor))
                }
                else -> {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        })
        srl_comment.setRefreshFooter(ClassicsFooter(this))
        srl_comment.setOnLoadMoreListener {
            model.getComment(id,commentPage, pageSize)
        }
        rv_comment.layoutManager=LinearLayoutManager(this)
        rv_comment.addItemDecoration(
                DrawableItemDecoration(0,0,0,2,
                        RecyclerView.VERTICAL,getDrawable(R.drawable.recycler_divider)))
        rv_comment.adapter=adapter

        loadData()

    }

    private var isLike=false
    private var listPop:ListDialog?=null
    private fun showListPop(){
        listPop=ListDialog(this)
                .addItem(ResUtil.getString(R.string.downloadSure), View.OnClickListener {
                    FileDownloadService.addTask(it.context, attributesMap(data))
                    listPop?.dismiss()
                })
                .addItem(ResUtil.getString(R.string.musicDetailActivity_addToWait), View.OnClickListener {
                    // connection?.addWait(music)
                    listPop?.dismiss()
                })
                .addItem(ResUtil.getString(R.string.musicDetailActivity_addToSheet), View.OnClickListener {
                    showSheetList()
                    listPop?.dismiss()
                })
                .addItem(//***根据是否已经喜爱来显示
                        if(!isLike)ResUtil.getString(R.string.setAsLike_real)
                        else ResUtil.getString(R.string.cancelLike), View.OnClickListener {
                    if(isLike){
                        WWSongSheetModel.removeAsLike(id.toLong()){
                            isLike=!it
                        }
                    }else{
                        WWSongSheetModel.setAsLike(id.toLong()){
                            if(it.code==200){
                                MToast.showToast(this,R.string.setAsLike_real)
                                isLike=true
                            }
                        }
                    }
                    listPop?.dismiss()

                })
        listPop?.show()
    }






    private var sheetListPop:SelectorListAlert?=null
    private fun showSheetList(){
        if(sheetListPop==null){
            WWSongSheetModel.getSongSheetList {res->
                sheetListPop=SelectorListAlert(this,ResUtil.getString(R.string.songSheet))
                sheetListPop!!.setIndex(-1)
                sheetListPop!!.list= ArrayList(res.map { it.name })
                sheetListPop!!.setListener(object :ListSelectListener{
                    override fun select(v: View?, position: Int) {
                        addSongToSheet(res[position].id)
                    }

                    override fun remove(v: View?, position: Int) {
                        sheetListPop!!.adapter.notifyDataSetChanged()
                    }
                })
                sheetListPop!!.showCenter(srl_comment)
            }
        }else{
            sheetListPop?.showCenter(srl_comment)
        }
    }
    private fun addSongToSheet(sheetId:Long){
        WWSongSheetModel.addSongToSheet(sheetId,id.toLong(),
                data.songInfo.title,data.songInfo.artistName,
                data.songInfo.albumName){
            sheetListPop?.dismiss()
        }
    }


    /**
     * 比较音乐是否是同一个地址
     * http://zhangmenshiting.qianqian.com/data2/music/0cc6430b863f1fc6032a29d42218ddcd/598649096/598649096.m4a?xcode=ec27389241337245e9c5304b21fec782
     * 没次xcode
     **/
    private fun musicEqual(m1: Music, m2: Music):Boolean {
        if(m1.path==m2.path)return true
        val index1=m1.path.indexOf("?")
        val index2=m2.path.indexOf("?")
        if(index1<0||index2<0)return false
        if(m1.path.substring(0,index1)==m2.path.substring(0,index2))return true
        return false

    }

    private fun attributesMap(info: MusicDetailInfo): InternetMusicDetail {
        val res = info.songInfo
        return InternetMusicDetail(
                songId = res.songId,
                songName = res.title,
                artistName = res.artistName,
                duration = res.duration.toInt(),
                size = info.bitRate.fileSize,
                lrcLink = res.lrcLink,
                songLink = info.bitRate.songLink,
                singerIconSmall = res.picSmall,
                albumId = res.albumId,
                albumName = res.albumName,
                format = info.bitRate.format
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceConnection != null) {
            unbindService(serviceConnection!!)
        }

    }

    companion object {
        private const val pageSize=30
        private const val ID = "itemId"
        @JvmStatic
        fun actionStart(ctx: Context, id: String) {
            val intent = Intent(ctx, MusicDetailActivity::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }

        @JvmStatic
        fun map(res:MusicDetailInfo):InternetMusicForPlay{
            val music = InternetMusicForPlay(res.songInfo.title,res.songInfo.artistName,res.bitRate.songLink)
            music.song_id=res.songInfo.songId
            music.imgAddress = res.songInfo.picSmall
            music.lrcLink = res.songInfo.lrcLink
            music.suffix = res.bitRate.format
            music.duration = res.songInfo.duration.toInt()*1000
            music.album = res.songInfo.albumName
            music.size = res.bitRate.fileSize
            return music
        }
        @JvmStatic
        fun map(res:MusicDetailInfo,music:InternetMusicForPlay){
            music.musicName=res.songInfo.title
            music.singer=res.songInfo.artistName
            music.path=res.bitRate.songLink
            music.song_id=res.songInfo.songId
            music.imgAddress = res.songInfo.picSmall
            music.lrcLink = res.songInfo.lrcLink
            music.suffix = res.bitRate.format
            music.duration = res.songInfo.duration.toInt()*1000
            music.album = res.songInfo.albumName
            music.size = res.bitRate.fileSize
        }
    }

}