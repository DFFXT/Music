package com.web.moudle.setting.cache

import android.app.Activity
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
import com.web.moudle.setting.chooser.LocalChooserActivity
import com.web.web.R
import kotlinx.android.synthetic.main.activity_cache.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        getCacheSize {
            val render=ResUtil.getFileSize(it)
            val str=ResUtil.getString(R.string.setting_clearCache)+"  $render"
            twd_clearCache.setText(ResUtil.getSpannable(str,render,ResUtil.getColor(R.color.textColor_9),ResUtil.getSize(R.dimen.textSize_min)))
        }

        cachePathChange()
        downloadPathChange()

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
        twd_selectCachePath.setOnClickListener {
            LocalChooserActivity.actionStartDirSelect(this, getCustomerCachePath(), SELECT_CACHE)
        }
        twd_selectDownloadPath.setOnClickListener {
            LocalChooserActivity.actionStartDirSelect(this, getCustomerDownloadPath(), SELECT_DOWNLOAD)
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
    private fun cachePathChange(){
        val origin=ResUtil.getString(R.string.setting_cacheSelectPath)+"  "+ getCustomerCachePath()
        val render=getCustomerCachePath()
        twd_selectCachePath.setText(
                ResUtil.getSpannable(origin,render,ResUtil.getColor(R.color.textColor_9),ResUtil.getSize(R.dimen.textSize_min))
        )
    }
    private fun downloadPathChange(){
        val origin=ResUtil.getString(R.string.setting_downloadSelectPath)+"  "+ getCustomerDownloadPath()
        val render=getCustomerDownloadPath()
        twd_selectDownloadPath.setText(
                ResUtil.getSpannable(origin,render,ResUtil.getColor(R.color.textColor_9),ResUtil.getSize(R.dimen.textSize_min))
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode!= Activity.RESULT_OK)return
        when (requestCode){
            SELECT_CACHE->{
                setCustomerCachePath(data!!.getStringExtra(INTENT_DATA)!!)
                cachePathChange()
            }
            SELECT_DOWNLOAD->{
                setCustomerDownloadPath(data!!.getStringExtra(INTENT_DATA)!!)
                downloadPathChange()
            }
        }


    }

    companion object {
        private const val SELECT_CACHE=1
        private const val SELECT_DOWNLOAD=2

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

        @JvmStatic
        fun getCustomerCachePath():String{
            return SP.getString(Constant.spName,Constant.SpKey.customerCachePath,Constant.LocalConfig.musicCachePath)
        }
        @JvmStatic
        fun setCustomerCachePath(path:String){
            SP.putValue(Constant.spName,Constant.SpKey.customerCachePath,path)
        }

        @JvmStatic
        fun getCustomerDownloadPath():String{
            return SP.getString(Constant.spName,Constant.SpKey.customerDownloadPath,Constant.LocalConfig.musicDownloadPath)
        }

        @JvmStatic
        fun setCustomerDownloadPath(path:String){
            SP.putValue(Constant.spName,Constant.SpKey.customerDownloadPath,path)
        }

        @JvmStatic
        fun getCacheSize(callback:((size:Long)->Unit)){
            GlobalScope.launch {
                var size=0L
                val cacheList=DataSupport.findAll(MusicCache::class.java)
                cacheList.forEach {cache->
                    val sizeList=MusicCache.calculateCacheRange(cache)
                    sizeList.forEach {
                        size+=it.to-it.from
                    }
                }
                callback(size)
            }
        }
    }
}