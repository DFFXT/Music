package com.web.moudle.setting.cache

import android.content.Context
import android.content.Intent
import android.view.View
import com.web.common.base.BaseActivity
import com.web.common.constant.Constant
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.config.Shortcut
import com.web.data.MusicCache
import com.web.misc.ConfirmDialog
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.net.proxy.InternetProxy
import com.web.moudle.preference.SP
import com.web.web.R
import kotlinx.android.synthetic.main.activity_cache.*
import org.litepal.crud.DataSupport

class CacheActivity:BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_cache

    override fun initView() {

        twd_cacheEnable.setOnClickListener {
            sw_cacheEnable.isChecked=!sw_cacheEnable.isChecked
        }

        cacheEnable(getCacheEnable())

        sw_cacheEnable.setOnCheckedChangeListener { _, isChecked ->
            cacheEnable(isChecked)
        }

        //**清空缓存
        twd_clearCache.setOnClickListener {
            buildConfirm(it,ResUtil.getString(R.string.setting_clearCacheTip)){
                val cacheList=DataSupport.findAll(MusicCache::class.java)
                cacheList.forEach {cacheMusic->
                    Shortcut.fileDelete(cacheMusic.cachePath)
                }
                DataSupport.deleteAllAsync(MusicCache::class.java).listen { ok->
                    MToast.showToast(this,ResUtil.getString(R.string.setting_cacheHasCleared,ok))
                }
            }

        }
        //**清空音乐信息
        twd_clearAllMusic.setOnClickListener { v ->
            buildConfirm(v,ResUtil.getString(R.string.setting_clearAllMusicAlert)){
                val intent = Intent(this, MusicPlay::class.java)
                intent.action = MusicPlay.ACTION_ClEAR_ALL_MUSIC
                startService(intent)
            }
        }
    }

    /**
     * 构建确认框
     */
    private fun buildConfirm(v:View,message:String,confirmCallBack:(()->Unit)){
        ConfirmDialog(this)
                .setLeftText(ResUtil.getString(R.string.no))
                .setRightText(ResUtil.getString(R.string.yes))
                .setMsg(message)
                .setRightListener { dialog ->
                    confirmCallBack()
                    dialog.dismiss()
                }
                .setLeftListener { dialog ->
                    dialog.dismiss()
                }
                .showCenter(v)
    }
    private fun cacheEnable(enable: Boolean){
        if(enable){
            twd_cacheEnable.setText(ResUtil.getString(R.string.setting_cacheOpen))
            sw_cacheEnable.isChecked=true
        }else{
            twd_cacheEnable.setText(ResUtil.getString(R.string.setting_cacheClosed))
            sw_cacheEnable.isChecked=false
        }
        if(enable!= getCacheEnable()){
            setCacheEnable(enable)
        }
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,CacheActivity::class.java))
        }

        @JvmStatic
        fun getCacheEnable():Boolean{
            return SP.getBoolean(Constant.spName,Constant.SpKey.cacheEnable,false)
        }
        @JvmStatic
        fun setCacheEnable(enable:Boolean){
            InternetProxy.cacheEnable=enable
            SP.putValue(Constant.spName,Constant.SpKey.cacheEnable,enable)
        }
    }
}