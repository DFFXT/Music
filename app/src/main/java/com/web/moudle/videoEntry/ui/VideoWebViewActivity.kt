package com.web.moudle.videoEntry.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.music.m.R
import com.music.m.databinding.ActivityVideoWebViewBinding
import com.web.common.base.BaseViewBindingActivity

class VideoWebViewActivity: BaseViewBindingActivity<ActivityVideoWebViewBinding>() {

    override fun getLayoutId(): Int =R.layout.activity_video_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding.wvWebView.webViewClient= WebViewClient()
        binding.wvWebView.settings.javaScriptEnabled=true
        binding.wvWebView.loadUrl(intent.getStringExtra(INTENT_DATA)!!)
    }


    companion object {
        @JvmStatic
        fun actionStart(ctx: Context, url: String) {
            val intent = Intent(ctx, VideoWebViewActivity::class.java)
            intent.putExtra(INTENT_DATA, url)
            ctx.startActivity(intent)
        }
        @JvmStatic
        fun actionStartForResult(ctx: AppCompatActivity, url: String,reqCode:Int) {
            val intent = Intent(ctx, VideoWebViewActivity::class.java)
            intent.putExtra(INTENT_DATA, url)
            ctx.startActivityForResult(intent,reqCode)
        }
    }
}