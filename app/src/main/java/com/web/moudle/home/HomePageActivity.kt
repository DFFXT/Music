package com.web.moudle.home

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.web.common.base.BaseActivity
import com.web.common.util.ResUtil
import com.web.moudle.home.local.LocalFragment
import com.web.moudle.home.mainFragment.MainFragment
import com.web.moudle.home.video.VideoRecommendFragment
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.moudle.search.SearchActivity
import com.web.web.R
import kotlinx.android.synthetic.main.activity_home_page.*
import me.jessyan.autosize.internal.CustomAdapt

class HomePageActivity : BaseActivity(), View.OnClickListener {

    private var currentFragmentIndex = 0
    private val pageList = ArrayList<Fragment>(3)
    private val tabImage = ArrayList<ImageView>(pageList.size)
    private val tabText = ArrayList<TextView>(pageList.size)
    override fun getLayoutId(): Int = R.layout.activity_home_page

    private fun init() {
        pageList.add(MainFragment())
        pageList.add(VideoRecommendFragment())
        pageList.add(LocalFragment())
        tabImage.add(iv_home)
        tabText.add(tv_home)
        tabImage.add(iv_video)
        tabText.add(tv_video)
        tabImage.add(iv_local)
        tabText.add(tv_local)
    }


    override fun initView() {
        init()
        layout_overAll.setOnClickListener(this)
        layout_video.setOnClickListener(this)
        layout_local.setOnClickListener(this)


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_main, pageList[0])
        transaction.commit()
        tabFocus(0)

    }


    override fun onClick(v: View) {
        val transaction = supportFragmentManager.beginTransaction()
        clearTabFocus(currentFragmentIndex)
        transaction.hide(pageList[currentFragmentIndex])
        when (v.id) {
            R.id.layout_overAll -> {
                tabFocus(0)
            }
            R.id.layout_video -> {
                tabFocus(1)
            }
            R.id.layout_local -> {
                tabFocus(2)
            }
        }
        if (pageList[currentFragmentIndex].isAdded) {
            transaction.show(pageList[currentFragmentIndex])
        } else {
            transaction.add(R.id.fragment_main, pageList[currentFragmentIndex])
        }
        transaction.commit()
    }

    private val dimeColor = ResUtil.getColor(R.color.textColor_9)
    private val focusColor = ResUtil.getColor(R.color.themeColor)
    private val colorTintFocus = ColorStateList.valueOf(focusColor)
    private val colorTintDime = ColorStateList.valueOf(dimeColor)
    private fun clearTabFocus(index: Int) {
        tabImage[index].imageTintList = colorTintDime
        tabText[index].setTextColor(dimeColor)
    }

    private fun tabFocus(index: Int) {
        currentFragmentIndex = index
        tabImage[index].imageTintList = colorTintFocus
        tabText[index].setTextColor(focusColor)
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == searchCode) {
            data?.getStringExtra(SearchActivity.INPUT_DATA)?.let {
                InternetMusicActivity.actionStart(this, it)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val searchCode=333
        @JvmStatic
        fun actionStart(ctx: Context) {

            ctx.startActivity(Intent(ctx, HomePageActivity::class.java))
        }
    }
}