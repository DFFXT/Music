package com.web.moudle.videoEntry.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.webkit.WebViewClient
import com.web.common.base.BaseActivity
import com.web.web.R
import kotlinx.android.synthetic.main.activity_video_web_view.*

class VideoWebViewActivity:BaseActivity() {

    override fun getLayoutId(): Int =R.layout.activity_video_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        wv_webView.webViewClient= WebViewClient()
        wv_webView.settings.javaScriptEnabled=true
        wv_webView.loadUrl(intent.getStringExtra(INTENT_DATA))
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