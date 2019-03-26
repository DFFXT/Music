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
import com.web.common.base.log
import com.web.common.constant.Constant
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
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
                equalizer = Equalizer(0, connect!!.mediaPlayId)
                equalizer?.enabled = true
                min = equalizer!!.bandLevelRange[0]
                max = equalizer!!.bandLevelRange[1]

                init()

                rv_soundsSetting.layoutManager = LinearLayoutManager(this@EqualizerActivity)
                equalizerAdapter = if(currentSelect==0){
                    EqualizerAdapter(createDefaultSoundSetList("").soundInfoList)
                }else{
                    EqualizerAdapter(savedData!![currentSelect-1].soundInfoList)
                }
                rv_soundsSetting.adapter = equalizerAdapter
                equalizerAdapter!!.seekToListener = { index, to ->
                    equalizer?.setBandLevel(index, (to + min).toShort())
                }

            }
        }
        bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        topBar.setEndImageListener(View.OnClickListener {
            if(currentSelect==0)return@OnClickListener
            savedData!![currentSelect-1].soundInfoList.forEach {
                log("$it ${it.save()}")
            }
            //val res=DataSupport.saveAll(savedData!![currentSelect-1].soundInfoList)
            MToast.showToast(this,"$currentSelect-1")
        })
    }
    private fun init(){
        //**获取存储的音效数据
        savedData = DataSupport.findAll(SoundSettingList::class.java,true)
        val titleList = ArrayList<String>()
        titleList.add(ResUtil.getString(R.string.default_))//**添加默认
        savedData?.forEach {
            titleList.add(it.title)
        }

        val adapter = SimpleSelectListAdapter(titleList)
        adapter.setIndex(currentSelect)
        adapter.setListener(object : ListSelectListener {
            override fun select(v: View?, position: Int) {
                if (position == 0) {
                    val sound = createDefaultSoundSetList("")
                    equalizerAdapter?.update(sound.soundInfoList)
                } else {
                    equalizerAdapter?.update(savedData!![position - 1].soundInfoList)
                }
                currentSelect=position
                setCurrentSoundEffectIndex(position)
            }

            override fun remove(v: View?, position: Int) {
                if(position==0){
                    titleList.add(0,ResUtil.getString(R.string.default_))
                    adapter.update(titleList)
                }else{
                    val obj=savedData?.removeAt(position-1)
                    obj?.delete()
                    if(titleList.size>=position){
                        select(null,position-1)
                        adapter.setIndex(position-1)
                    }else{
                        select(null,position)
                        adapter.setIndex(position)
                    }
                }
            }
        })
        val helper=ItemTouchHelper(MyItemTouchHelperCallBack(adapter))
        helper.attachToRecyclerView(rv_savedSoundSetting)
        rv_savedSoundSetting.layoutManager = LinearLayoutManager(this)
        rv_savedSoundSetting.addItemDecoration(DrawableItemDecoration(0,0,0,4,
                RecyclerView.VERTICAL,ResUtil.getDrawable(R.drawable.recycler_divider)))
        rv_savedSoundSetting.adapter = adapter


        //**创建新的音效
        tv_addAndSave.setOnClickListener {
            val soundSet = createDefaultSoundSetList("${savedData?.size}")
            savedData!!.add(soundSet)
            //**保存音效
            soundSet.saveAsync().listen {
                if (!it) return@listen
                titleList.add(soundSet.title)
                adapter.notifyItemInserted(titleList.size - 1)
            }
            soundSet.soundInfoList.forEach {
                it.save()
            }
        }
    }

    private fun createDefaultSoundSetList(title: String): SoundSettingList {
        val soundSet = SoundSettingList(title, ArrayList())
        for (i in 0 until equalizer!!.numberOfBands) {
            soundSet.soundInfoList.add(SoundInfo(
                    title = "${equalizer!!.getCenterFreq(i.toShort()) / 1000}Hz",
                    value = equalizer!!.getBandLevel(i.toShort()).toInt(),
                    min = min.toInt(),
                    max = max.toInt()))
        }
        return soundSet
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let {
            unbindService(it)
        }
        equalizer?.release()
    }

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
            SP.putValue(Constant.spName,Constant.SpKey.currentSoundEffect,index)
        }
    }
}