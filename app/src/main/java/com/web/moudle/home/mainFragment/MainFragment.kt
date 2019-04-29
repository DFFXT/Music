package com.web.moudle.home.mainFragment

import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.web.common.base.BaseFragment
import com.web.common.base.BaseFragmentPagerAdapter
import com.web.moudle.home.mainFragment.subFragment.AllArtistFragment
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.moudle.home.mainFragment.subFragment.BillBoardFragment
import com.web.moudle.home.mainFragment.subFragment.MusicMainFragment
import com.web.moudle.home.mainFragment.subFragment.SongSheetFragment
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : BaseFragment() {
    private lateinit var vm: MainFragmentViewModel
    override fun getLayoutId(): Int = R.layout.fragment_main
    private val pageList=ArrayList<BaseFragment>()

    init {
        pageList.add(MusicMainFragment())
        pageList.add(BillBoardFragment())
        pageList.add(SongSheetFragment())
        pageList.add(AllArtistFragment())
    }

    override fun initView(rootView: View) {

        vm= ViewModelProviders.of(this)[MainFragmentViewModel::class.java]

        rootView.viewPager.adapter=BaseFragmentPagerAdapter(fragmentManager!!,pageList)
        rootView.tabLayout.setupWithViewPager(rootView.viewPager)
        for(i in pageList.indices){
            rootView.tabLayout.getTabAt(i)?.text=pageList[i].title
        }



    }
}