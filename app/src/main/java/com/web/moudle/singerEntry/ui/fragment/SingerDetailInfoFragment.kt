package com.web.moudle.singerEntry.ui.fragment

import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.web.common.base.BaseFragment
import com.web.common.util.ResUtil
import com.web.moudle.singerEntry.model.SingerEntryViewModel
import com.music.m.R

class SingerDetailInfoFragment:BaseFragment() {
    override var title=ResUtil.getString(R.string.music)
    override fun getLayoutId(): Int = R.layout.fragment_singer_all_music

    private lateinit var vm:SingerEntryViewModel

    override fun initView(rootView: View) {
        vm=ViewModelProviders.of(this)[SingerEntryViewModel::class.java]




    }
}