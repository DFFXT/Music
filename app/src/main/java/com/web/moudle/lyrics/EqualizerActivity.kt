package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.audiofx.Equalizer
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.constant.Constant
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.lyrics.adapter.EqualizerAdapter
import com.web.moudle.lyrics.bean.SoundInfo
import com.web.moudle.lyrics.bean.SoundSettingList
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.music.page.local.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.preference.SP
import com.web.web.R
import kotlinx.android.synthetic.main.activity_equalizer.*
import org.litepal.crud.DataSupport
import kotlin.math.min

class EqualizerActivity : BaseActivity() {
    private var serviceConnection: ServiceConnection? = null
    private var connect: MusicPlay.Connect? = null
    private var equalizer: Equalizer? = null
    private var max: Short = 0
    private var min: Short = 0
    private var equalizerAdapter: EqualizerAdapter? = null
    private var savedData: MutableList<SoundSettingList>? = null
    private var currentSelect = getCurrentSoundEffectIndex()
    override fun getLayoutId(): Int = R.layout.activity_equalizer

    override fun initView() {
        val intent = Intent(this, MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {}

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                connect = service as MusicPlay.Connect
                equalizer = connect!!.equalizer
                min = equalizer!!.bandLevelRange[0]
                max = equalizer!!.bandLevelRange[1]

                init()

                rv_soundsSetting.layoutManager = LinearLayoutManager(this@EqualizerActivity)
                equalizerAdapter = EqualizerAdapter()
                loadSounds(currentSelect)
                rv_soundsSetting.adapter = equalizerAdapter
                equalizerAdapter!!.seekToListener = { index, to ->
                    equalizer?.setBandLevel(index, (to + min).toShort())
                }

            }
        }
        bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        topBar.setEndImageListener(View.OnClickListener {
            if (currentSelect == 0) return@OnClickListener
            savedData!![currentSelect].soundInfoList.forEach {
                it.save()
            }
            MToast.showToast(this, R.string.setting_suffix_saveSuccess)
        })
    }

    private fun loadSounds(position: Int) {
        if (position >= savedData!!.size) return
        equalizerAdapter?.canSeekable = position != 0
        val sound = savedData!![position]
        equalizerAdapter?.update(sound.soundInfoList)
        currentSelect = position
        for (i in sound.soundInfoList.indices) {
            equalizer!!.setBandLevel(i.toShort(), (sound.soundInfoList[i].value + sound.soundInfoList[i].min).toShort())
        }
        //connect?.setSoundEffect(sound.soundInfoList)
        setCurrentSoundEffectIndex(position)
    }

    private fun init() {
        //**获取存储的音效数据
        savedData = DataSupport.findAll(SoundSettingList::class.java, true)
        val titleList = ArrayList<String>()
        savedData?.forEach {
            titleList.add(it.title)
        }

        val adapter = SimpleSelectListAdapter(titleList)
        adapter.setIndex(currentSelect)
        adapter.setListener(object : ListSelectListener {
            override fun select(v: View?, position: Int) {
                loadSounds(position)
            }

            override fun remove(v: View?, position: Int) {
                if (position == 0) {
                    titleList.add(0, ResUtil.getString(R.string.default_))
                    adapter.update(titleList)
                } else {
                    val obj = savedData?.removeAt(position)
                    obj?.delete()
                    var select = currentSelect
                    if (position < currentSelect) {
                        select = currentSelect - 1
                    }
                    select = min(select, titleList.size - 2)
                    select(null, select)
                    adapter.setIndex(select)
                }
            }
        })
        val helper = ItemTouchHelper(MyItemTouchHelperCallBack(adapter))
        helper.attachToRecyclerView(rv_savedSoundSetting)
        rv_savedSoundSetting.layoutManager = LinearLayoutManager(this)
        rv_savedSoundSetting.addItemDecoration(DrawableItemDecoration(0, 0, 0, 4,
                RecyclerView.VERTICAL, ResUtil.getDrawable(R.drawable.recycler_divider)))
        rv_savedSoundSetting.adapter = adapter


        //**创建新的音效
        tv_addAndSave.setOnClickListener {
            if (savedData!!.size > 3) {
                MToast.showToast(this, R.string.only4Enable)
                return@setOnClickListener
            }
            val soundSet = createDefaultSoundSetList("${ResUtil.getString(R.string.soundEffect)} - ${savedData!!.size}", equalizer!!)
            savedData!!.add(soundSet)
            //**保存音效
            saveSoundEffect(soundSet)
            titleList.add(soundSet.title)
            adapter.notifyItemInserted(titleList.size - 1)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let {
            unbindService(it)
        }
    }

    override fun enableSwipeToBack(): Boolean = true

    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            ctx.startActivity(Intent(ctx, EqualizerActivity::class.java))
        }

        @JvmStatic
        fun getCurrentSoundEffectIndex(): Int {
            return SP.getInt(Constant.spName, Constant.SpKey.currentSoundEffect, 0)
        }

        @JvmStatic
        fun setCurrentSoundEffectIndex(index: Int) {
            SP.putValue(Constant.spName, Constant.SpKey.currentSoundEffect, index)
        }

        @JvmStatic
        fun getCurrentSoundEffect(): List<SoundInfo> {
            val savedData = DataSupport.findAll(SoundSettingList::class.java, true)
            val index = getCurrentSoundEffectIndex()
            if (index >= savedData.size) {
                setCurrentSoundEffectIndex(0)
                return ArrayList()
            }
            return savedData[index].soundInfoList
        }

        /**
         * 创建默认的音效
         */
        @JvmStatic
        fun createDefaultSoundSetList(title: String, equalizer: Equalizer): SoundSettingList {
            val sound = DataSupport.findFirst(SoundSettingList::class.java, true)

            val min = equalizer.bandLevelRange[0]
            val max = equalizer.bandLevelRange[1]
            val soundSet = SoundSettingList(title, ArrayList())
            for (i in 0 until equalizer.numberOfBands) {
                soundSet.soundInfoList.add(SoundInfo(
                        title = "${equalizer.getCenterFreq(i.toShort()) / 1000}Hz",
                        value = if (sound != null) {
                            sound.soundInfoList[i].value
                        } else {
                            equalizer.getBandLevel(i.toShort()).toInt() - min
                        },
                        min = min.toInt(),
                        max = max.toInt()))
            }
            return soundSet
        }

        @JvmStatic
        fun saveDefaultSoundEffect(equalizer: Equalizer) {
            val num = DataSupport.count(SoundSettingList::class.java)
            if (num == 0) {
                saveSoundEffect(createDefaultSoundSetList(ResUtil.getString(R.string.default_), equalizer))
            }
        }

        @JvmStatic
        fun saveSoundEffect(sound: SoundSettingList) {
            sound.save()
            sound.soundInfoList.forEach {
                it.save()
            }
        }
    }
}