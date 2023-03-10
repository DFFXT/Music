package com.web.moudle.music.page.local

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Outline
import android.graphics.PorterDuff
import android.os.IBinder
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.widget.TextViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.web.common.base.BasePageChangeListener
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.PlayerObserver
import com.web.common.constant.Apk
import com.web.common.tool.MToast.showToast
import com.web.common.util.KeyboardManager.hideKeyboard
import com.web.common.util.ResUtil
import com.web.common.util.ResUtil.timeFormat
import com.web.common.util.ViewUtil
import com.web.config.Shortcut
import com.web.data.Music
import com.web.moudle.lyrics.LyricsActivity
import com.web.moudle.music.page.BaseMusicPage
import com.web.moudle.music.page.local.control.interf.IMusicController
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.moudle.music.page.local.control.ui.SelectorListAlert
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.other.PlayerConfig.MusicOrigin
import com.web.moudle.music.player.other.PlayerConfig.PlayType
import com.web.moudle.music.player.other.PlayerConfig.bitmap
import com.web.moudle.music.player.other.PlayerConfig.music
import com.web.moudle.music.player.other.PlayerConfig.playType
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.music.player.plug.ActionControlPlug.Companion.scan
import com.web.moudle.musicDownload.ui.MusicDownLoadActivity
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.moudle.search.SearchActivity
import com.web.moudle.search.SearchActivity.Companion.actionStart
import com.web.moudle.setting.ui.SettingActivity
import com.music.m.R
import com.music.m.databinding.RestructMusicLayoutBinding
import java.io.File
import java.util.*

/**
 * 音乐播放主页
 * todo 转换为ViewBinding
 */
class MusicActivity : BaseViewBindingActivity<RestructMusicLayoutBinding>(), View.OnClickListener, IMusicController {
    private val RESULT_CODE_SEARCH : Int = 1 // ktlint-disable colon-spacing
    private var songName: TextView? = null
    private var singer: TextView? = null
    private var tv_musicOrigin: TextView? = null
    private var tv_duration: TextView? = null
    private var tv_currentTime: TextView? = // **音乐信息
        null
    private var iv_singerIcon: ImageView? = null
    private var pre: ImageView? = null
    private var pause: ImageView? = null
    private var next: ImageView? = null
    private var drawer: DrawerLayout? = null
    private var viewPager: ViewPager? = null
    private var musicListPage: MusicListPage? = null
    var connect: IMusicControl? = null
        private set
    private val pageList: MutableList<BaseMusicPage> = ArrayList()
    private var listAlert: SelectorListAlert? = null
    private val observer: PlayerObserver = object : PlayerObserver() {
        override fun onLoad(music: Music?, maxTime: Int) {
            mBinding.musicControlBox.bar.max = maxTime
            if (music == null) {
                mBinding.musicControlBox.songname.text = null
                mBinding.musicControlBox.singer.text = null
                tv_duration!!.text = ResUtil.getString(R.string.musicTime)
            } else {
                tv_duration!!.text = timeFormat("mm:ss", music.duration.toLong())
                songName!!.text = music.musicName
                singer!!.text = music.singer
            }
            if (bitmap == null) {
                iv_singerIcon!!.setImageResource(R.drawable.singer_default_icon)
            } else {
                iv_singerIcon!!.setImageBitmap(bitmap)
            }
            musicListPage!!.loadMusic(connect!!.getDataSource().localIndex)
        }

        override fun onPlay() {
            pause!!.setImageResource(R.drawable.icon_play_black)
            if (listAlert != null && PlayerConfig.musicOrigin === MusicOrigin.WAIT) {
                listAlert!!.setIndex(connect!!.getDataSource().index)
            }
        }

        override fun onPause() {
            pause!!.setImageResource(R.drawable.icon_pause_black)
        }

        override fun onPlayTypeChanged(playType: PlayType?) {
            showPlayType(playType)
        }

        override fun onCurrentTime(duration: Int, maxTime: Int) {
            if (mBinding.musicControlBox.bar.isPressed) {
                mBinding.musicControlBox.bar.progress = duration
                tv_currentTime!!.text = timeFormat("mm:ss", duration.toLong())
            }
        }

        override fun onMusicListChange(list: List<Music>) {
            musicListPage!!.setData(connect!!.getDataSource().localIndex, list)
        }

        override fun onBufferingUpdate(percent: Int) {
            mBinding.musicControlBox.bar.secondaryProgress = (percent * mBinding.musicControlBox.bar.max)
        }

        /***
         * 音乐源发生了变化
         * @param origin origin
         */
        @SuppressLint("SetTextI18n")
        override fun onMusicOriginChanged(origin: MusicOrigin?) {
            runOnUiThread {
                when (origin) {
                    MusicOrigin.LOCAL -> {
                        tv_musicOrigin!!.text = "LOCAL"
                    }
                    MusicOrigin.INTERNET -> {
                        tv_musicOrigin!!.text = "INTERNET"
                    }
                    MusicOrigin.WAIT -> {
                        tv_musicOrigin!!.text = "LIST"
                    }
                    MusicOrigin.STORAGE -> {
                        tv_musicOrigin!!.text = "STORAGE"
                    }
                    else -> {}
                }
            }
        }
    }

    override fun getLayoutId(): Int { // ---活动启动入口
        return R.layout.restruct_music_layout
    }

    override fun initView() {

        // WindowUtil.setImmersedStatusBar(getWindow());
        findID()
        setToolbar()
        musicListPage = MusicListPage()
        pageList.add(musicListPage!!)
        setAdapter()
        startService(Intent(this, NewPlayer::class.java))
        setListener()
        connect()
        iv_singerIcon!!.clipToOutline = true
        iv_singerIcon!!.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, view.width / 10f)
            }
        }
        viewPager!!.post { currentPage.title = "" }
    }

    @SuppressLint("RestrictedApi")
    private fun setToolbar() {
        val paddingStart = ViewUtil.dpToPx(6f)
        mBinding.toolbar.endImageView.setPadding(paddingStart, paddingStart, paddingStart, paddingStart)
        mBinding.toolbar.setEndImageListener {
            actionStart(
                this@MusicActivity,
                RESULT_CODE_SEARCH
            )
        }
        val titleView = mBinding.toolbar.setMainTitle(ResUtil.getString(R.string.page_local))
        TextViewCompat.setCompoundDrawableTintMode(titleView, PorterDuff.Mode.ADD)
        /*tv_title.setOnClickListener(v -> {
            if (pageList.get(viewPager.getCurrentItem()).getTitle().equals(MusicListPage.pageName)) {

                    LocalSheetListAlert listAlert = new LocalSheetListAlert(MusicActivity.this, ResUtil.getString(R.string.songSheet));
                    ArrayList<String> list = new ArrayList<>();
                    for (MusicList ml : groupList) {
                        list.add(ml.getTitle());
                    }
                    listAlert.setList(list);
                    listAlert.setIndex(groupIndex);
                    listAlert.setCanTouchRemove(false);
                    listAlert.setListener(new LocalSheetListener() {
                        @Override
                        public void select(View v, int position) {
                            connect.selectList(position,-1);
                            connect.getList();
                            listAlert.dismiss();
                        }

                        @Override
                        public void remove(View v, int position) {
                            if (position == 0||position==1) {// **默认，喜好歌单无法删除
                                listAlert.dismiss();
                                return;
                            }
                            SongSheetManager.INSTANCE.getSongSheetList().removeSongSheet(position - 1);
                            connect.groupChange();
                        }

                        @Override
                        public void saveEdit(String name, int index) {
                            if (groupList.get(index).getTitle().equals(name)) return;
                            SongSheetList list = SongSheetManager.INSTANCE.getSongSheetList();
                            list.getSongList().get(index - 1).setName(name);
                            list.save();
                            groupList.get(index).setTitle(name);
                        }
                    });
                    listAlert.show(v);
            }
        });*/
    }

    /**
     * 获取传输的数据
     */
    private val intentData: Unit
        private get() {
            val intent = intent
            val action = intent.action
            /*if(ACTION_DEF.equals(action)){
            connect.selectList(0,-1);
            connect.getList();
        }else if(ACTION_LIKE_SHEET.equals(action)){
            connect.selectList(1,-1);
            connect.getList();
        }else */if (intent.data != null) {
                val path = intent.data!!.path
                if (path != null) {
                    val file = File(path)
                    val name = file.name
                    val out = arrayOfNulls<String>(2)
                    Shortcut.getName(out, name)
                    val music = Music(out[0], out[1], path)
                    connect!!.play(music)
                }
            }
        }

    private fun findID() {
        drawer = findViewById(R.id.drawer)
        viewPager = findViewById(R.id.viewPager)
        var v = findViewById<View>(R.id.musicControlBox)
        v.findViewById<View>(R.id.iv_singerIcon).setOnClickListener { view: View? ->
            if (music != null) {
                LyricsActivity.actionStart(this)
            }
        }
        iv_singerIcon = v.findViewById(R.id.iv_singerIcon)
        songName = v.findViewById(R.id.songname)
        singer = v.findViewById(R.id.singer)
        pre = v.findViewById(R.id.pre)
        pause = v.findViewById(R.id.pause)
        next = v.findViewById(R.id.next)

        // ***音乐控制View
        songName = findViewById(R.id.songname)
        singer = findViewById(R.id.singer)
        tv_musicOrigin = findViewById(R.id.musicOrigin)
        tv_duration = findViewById(R.id.tv_duration)
        tv_currentTime = findViewById(R.id.tv_currentTime)
        pre = findViewById(R.id.pre)
        pause = findViewById(R.id.pause)
        next = findViewById(R.id.next)
        v = findViewById(R.id.leftDrawer)
        v.findViewById<View>(R.id.scanLocalMusic).setOnClickListener(this)
        v.findViewById<View>(R.id.goDownload).setOnClickListener(this)
        v.findViewById<View>(R.id.item_setting1).setOnClickListener(this)
        mBinding.musicControlBox.musicOrigin.setOnClickListener(this)
        mBinding.leftDrawer.tvVersion.text = ResUtil.getString(R.string.about_app, Apk.getVersionName())
        Apk.init(this, drawer)
    }

    private var serviceConnection: ServiceConnection? = null

    /**
     * 连接服务
     */
    private fun connect() {
        val intent = Intent(this, NewPlayer::class.java)
        intent.action = ActionControlPlug.BIND
        bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    connect = service as IMusicControl
                    connect!!.addObserver(this@MusicActivity, observer)
                    intentData // *************获取输入数据
                    connect!!.getPlayerInfo(this@MusicActivity) // **获取播放器状态
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    connect = null
                }
            }.also { serviceConnection = it },
            BIND_AUTO_CREATE
        )
    }

    private fun setAdapter() { // --设置本地适配器
        viewPager!!.offscreenPageLimit = 2
        viewPager!!.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return pageList[position]
            }

            override fun getCount(): Int {
                return pageList.size
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }
        }
        viewPager!!.setCurrentItem(pageList.size - 1, false)
    }

    /**
     * 给控件绑定事件
     */
    private fun setListener() {
        pre!!.setOnClickListener(this)
        pause!!.setOnClickListener(this)
        next!!.setOnClickListener(this)
        mBinding.musicControlBox.musicplayType.setOnClickListener(this)
        mBinding.musicControlBox.bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            // --进度条拖动
            override fun onStopTrackingTouch(arg0: SeekBar) {
                val progress = arg0.progress
                connect!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(arg0: SeekBar) {}
            override fun onProgressChanged(v: SeekBar, now: Int, tf: Boolean) { // --发送进度条信息
            }
        })
        viewPager!!.addOnPageChangeListener(object : BasePageChangeListener() {
            override fun onPageSelected(position: Int) {
                hideKeyboard(this@MusicActivity, viewPager!!.windowToken)
                pageList[position].title = ""
            }
        })
    }

    override fun getMusicControl(): IMusicControl {
        return connect!!
    }

    override fun onClick(v: View) { // --点击事件
        when (v.id) {
            R.id.goDownload, R.id.download -> {
                MusicDownLoadActivity.actionStart(this)
                drawer!!.closeDrawer(GravityCompat.START)
            }
            R.id.pre -> {
                connect!!.pre()
            }
            R.id.pause -> {
                connect!!.changePlayerPlayingStatus()
            }
            R.id.next -> {
                connect!!.next(false)
            }
            R.id.musicplay_type -> {
                connect!!.changePlayType(playType.next())
            }
            R.id.subSettingBox -> {
            }
            R.id.item_setting1 -> {
                SettingActivity.actionStart(this)
                drawer!!.closeDrawer(GravityCompat.START)
                return
            }
            R.id.scanLocalMusic -> {
                // --扫描本地文件
                scan(this)
                showToast(this, ResUtil.getString(R.string.musicIsScanning))
                drawer!!.closeDrawer(GravityCompat.START)
            }
            R.id.musicOrigin -> {
                if (PlayerConfig.musicOrigin !== PlayerConfig.MusicOrigin.WAIT) return
                if (listAlert == null) {
                    listAlert = SelectorListAlert(this, ResUtil.getString(R.string.playList))
                }
                listAlert!!.setListener(object : ListSelectListener {
                    override fun select(v: View, position: Int) {
                        connect!!.play(position, PlayerConfig.MusicOrigin.WAIT)
                    }

                    override fun remove(v: View, position: Int) {
                        connect!!.getDataSource().remove(position)
                        if (connect!!.getDataSource().size == 0) {
                            listAlert!!.dismiss()
                            return
                        }
                        listAlert!!.setIndex(connect!!.getDataSource().index)
                    }
                })
                val list = ArrayList<String>()
                for (m in connect!!.getDataSource()) {
                    list.add(m.musicName)
                }
                listAlert!!.setCanTouchRemove(true)
                listAlert!!.list = list
                listAlert!!.setIndex(connect!!.getDataSource().index)
                listAlert!!.show(v)
            }
        }
    }

    private val currentPage: BaseMusicPage
        get() = pageList[viewPager!!.currentItem]

    // **根据playType显示图标
    private fun showPlayType(playType: PlayType?) {
        val musicPlayType = mBinding.musicControlBox.musicplayType
        when (playType) {
            PlayType.ALL_LOOP -> {
                musicPlayType.setImageResource(R.drawable.music_type_all_loop)
            }
            PlayType.ONE_LOOP -> {
                musicPlayType.setImageResource(R.drawable.music_type_one_loop)
            }
            PlayType.ALL_ONCE -> {
                musicPlayType.setImageResource(R.drawable.music_type_all_once)
            }
            PlayType.ONE_ONCE -> {
                musicPlayType.setImageResource(R.drawable.music_type_one_once)
            }
            PlayType.RANDOM -> {
                musicPlayType.setImageResource(R.drawable.random_icon)
            }
            else -> {}
        }
    }

    public override fun onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection!!)
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        if (data == null) return
        if (requestCode == RESULT_CODE_SEARCH) {
            InternetMusicActivity.actionStart(this, data.getStringExtra(SearchActivity.INPUT_DATA))
        }
    }

    companion object {
        private const val ACTION_LIKE_SHEET = "like"
        private const val ACTION_DEF = "def"
        fun actionStart(context: Context) {
            val intent = Intent(context, MusicActivity::class.java)
            intent.action = ACTION_DEF
            context.startActivity(intent)
        }

        fun actionStartLike(context: Context) {
            val intent = Intent(context, MusicActivity::class.java)
            intent.action = ACTION_LIKE_SHEET
            context.startActivity(intent)
        }
    }
}