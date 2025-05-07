package com.web.moudle.setting.about

import android.content.Context
import android.content.Intent
import com.music.m.R
import com.music.m.databinding.ActivityAboutBinding
import com.web.common.base.BaseActivity2
import com.web.common.bean.Version
import com.web.common.constant.Apk
import com.web.common.util.ResUtil
import com.web.misc.imageDraw.SnowDraw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AboutActivity: BaseActivity2<ActivityAboutBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_about

    override fun initView() {
        binding.tvVersion.text=ResUtil.getString(R.string.setting_about_version,Apk.getVersionName())
        GlobalScope.launch(Dispatchers.IO) {
            val v=Version.readCurrentVersion()
            runOnUiThread {
                binding.tvUpdateTime.text=v?.publishTime
            }
        }
        binding.roundTest.afterDraw = SnowDraw()
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,AboutActivity::class.java))
        }

        @JvmStatic
        fun getUpdateTime(){

        }
    }
}