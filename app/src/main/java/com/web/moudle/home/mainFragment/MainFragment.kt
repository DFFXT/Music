package com.web.moudle.home.mainFragment

import android.app.Activity
import android.graphics.Bitmap
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.web.common.base.BaseFragment
import com.web.common.base.MyApplication
import com.web.common.util.MediaCreator
import com.web.common.util.ResUtil
import com.web.moudle.home.HomePageActivity
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.moudle.home.mainFragment.subFragment.AllArtistFragment
import com.web.moudle.home.mainFragment.subFragment.BillBoardFragment
import com.web.moudle.home.mainFragment.subFragment.MusicMainFragment
import com.web.moudle.home.mainFragment.subFragment.SongSheetFragment
import com.web.moudle.search.SearchActivity
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainFragment: BaseFragment() {
    private lateinit var vm: MainFragmentViewModel
    override fun getLayoutId(): Int = R.layout.fragment_main
    @Deprecated("重复了")
    private val pageList=ArrayList<BaseFragment>()

    init {
        pageList.add(MusicMainFragment())
        pageList.add(BillBoardFragment())
        pageList.add(SongSheetFragment())
        pageList.add(AllArtistFragment())
        val d = ResUtil.getDrawable(R.drawable.icon_pre_white_fill)
        /*d.setTint(ResUtil.getColor(R.color.black))
        val b = ResUtil.getBitmapFromDrawable(d)
        val uri = MediaCreator.createImageUri("pre.png")
        val out = MyApplication.context.contentResolver.openOutputStream(uri)
        b.compress(Bitmap.CompressFormat.PNG,100,out)
        out?.flush()
        out!!.close()*/

    }

    override fun initView(rootView: View) {
        vm= ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        rootView.viewPager.offscreenPageLimit = 4
        rootView.viewPager.adapter= object : FragmentStateAdapter(childFragmentManager,this.lifecycle){
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0-> MusicMainFragment()
                    1-> BillBoardFragment()
                    2-> SongSheetFragment()
                    else -> AllArtistFragment()
                }
            }
        }
        TabLayoutMediator(rootView.tabLayout,rootView.viewPager) { tab, position -> tab.text = pageList[position].title }.attach()

        rootView.topBar.setEndImageListener {
            SearchActivity.actionStart(context as Activity, HomePageActivity.searchCode)
        }


    }
}