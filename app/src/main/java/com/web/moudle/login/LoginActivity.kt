package com.web.moudle.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.web.common.base.BaseActivity
import com.web.common.base.BaseFragment
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.moudle.login.fragment.LoginFragment
import com.web.moudle.login.fragment.RegisterFragment
import com.web.web.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_login

    private val pageList = ArrayList<BaseFragment>()
    private val tagList = ArrayList<String>()
    private var currentPageIndex = 0

    init {
        pageList.add(LoginFragment())
        tagList.add(ResUtil.getString(R.string.register))
        pageList.add(RegisterFragment())
        tagList.add(ResUtil.getString(R.string.login))

    }

    /**
     * 这个activity里面有fragment，使用分屏时，activity生命周期会重置，但是视图会通过onSaveInstanceState保留下来，
     * FrameLayout中会保留分屏前的fragment视图，
     * 如果fragment使用add而不是replace来显示，那么分屏前的fragment仍然会显示在FrameLayout中，
     * 使用replace可以解决这个特点
     * 如果使用add，就需要阻止使用分屏前的视图，在onCreate中传入null即可
     * 如果要完全恢复fragment，需要在onSaveInstanceState中保存fragment里面的相关信息。
     */
    override fun onCreate(b: Bundle?) {
        super.onCreate(null)
    }


    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        switchPage(0)
        tv_switchPage.setOnClickListener {
            val nextIndex = (currentPageIndex + 1) % pageList.size
            switchPage(nextIndex)
        }
    }


    private fun switchPage(index: Int) {
        currentPageIndex = index
        val fragment = pageList[index]
        tv_switchPage.text = tagList[index]

        hideOtherFragment(fragment)
        val transaction = supportFragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            transaction.add(R.id.fragment_main, fragment)
        }
        transaction.show(fragment)
        transaction.commit()

    }

    private fun hideOtherFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        pageList.forEach { other ->
            if (other != fragment) {
                transaction.hide(other)
            }
        }
        transaction.commit()
    }


    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            ctx.startActivity(Intent(ctx, LoginActivity::class.java))
        }
    }
}