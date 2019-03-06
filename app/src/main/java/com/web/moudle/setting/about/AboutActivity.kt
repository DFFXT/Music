package com.web.moudle.setting.about

import android.content.Context
import android.content.Intent
import com.web.common.base.BaseActivity
import com.web.common.bean.Version
import com.web.common.constant.Apk
import com.web.common.constant.Constant
import com.web.common.util.ResUtil
import com.web.web.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AboutActivity:BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_about

    override fun initView() {
        tv_version.text=ResUtil.getString(R.string.setting_about_version,Apk.getVersionName())
        GlobalScope.launch(Dispatchers.IO) {
            val v=Version.readCurrentVersion()
            runOnUiThread {
                tv_updateTime.text=v?.publishTime
            }
        }
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