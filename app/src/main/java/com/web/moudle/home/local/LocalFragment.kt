package com.web.moudle.home.local

import android.app.Activity
import android.content.Intent
import android.view.View
import com.web.common.base.BaseFragment
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.moudle.home.HomePageActivity
import com.web.moudle.home.local.model.LocalModel
import com.web.moudle.login.LoginActivity
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicDownload.ui.MusicDownLoadActivity
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.moudle.recentListen.RecentListenActivity
import com.web.moudle.search.SearchActivity
import com.web.moudle.setting.ui.SettingActivity
import com.web.moudle.user.UserManager
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_local.view.*

class LocalFragment : BaseFragment() {
    private val model = LocalModel()
    override fun getLayoutId(): Int = R.layout.fragment_local

    override fun initView(rootView: View) {
        rootView.topBar.setEndImageListener(View.OnClickListener {
            SettingActivity.actionStart(context)
        })

        rootView.layout_localBg.setOnClickListener {
            MusicActivity.actionStart(context)
        }

        rootView.layout_recent.setOnClickListener {
            RecentListenActivity.actionStart(it.context)
        }

        rootView.iv_search.setOnClickListener {
            SearchActivity.actionStart(context as Activity, HomePageActivity.searchCode)
        }
        rootView.layout_prefer.setOnClickListener {
            MusicActivity.actionStartLike(context)
        }

        rootView.layout_download.setOnClickListener {
            MusicDownLoadActivity.actionStart(it.context)
        }

        rootView.layout_fastScan.setOnClickListener {
            MusicPlay.scan(context)
        }

        initData()


    }

    private fun initData(){
        model.getMusicNum {
            rootView!!.tv_musicNum?.text = it.toString()
        }
        model.getPreferNum {
            rootView!!.tv_preferNum?.text = it.toString()
        }

        model.getDownloadNum {
            rootView!!.tv_downloadNum.text = it.toString()
        }

        model.getRecentMusicNum {
            rootView!!.tv_recentListen.text = it.toString()
        }

        if(UserManager.isLogin()){
            ImageLoad.load("").placeholder(R.drawable.def_user_icon).into(rootView!!.iv_userIcon)
            rootView!!.tv_userName.text=UserManager.getUserName()
            rootView!!.iv_userIcon.setOnClickListener(null)
        }else{
            rootView!!.iv_userIcon.setImageResource(R.drawable.def_user_icon)
            rootView!!.tv_userName.text=ResUtil.getString(R.string.login)
            rootView!!.iv_userIcon.setOnClickListener {
                LoginActivity.actionStart(it.context)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }




}