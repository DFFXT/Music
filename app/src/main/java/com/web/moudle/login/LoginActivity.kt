package com.web.moudle.login

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.web.common.base.BaseActivity
import com.web.common.base.log
import com.web.common.util.WindowUtil
import com.web.moudle.login.fragment.LoginFragment
import com.web.moudle.login.fragment.RegisterFragment
import com.web.web.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(){
    override fun getLayoutId(): Int = R.layout.activity_login

    private val login=LoginFragment()
    private val register=RegisterFragment()
    private var currentPage:Fragment=login

    override fun initView() {

        WindowUtil.setImmersedStatusBar(window)


        switchPage(login)
        tv_switchPage.setOnClickListener {
            if(currentPage == login){
                switchPage(register)
                tv_switchPage.text=getString(R.string.register)
            }else{
                switchPage(login)
                tv_switchPage.text=getString(R.string.login)
            }
        }
    }

    private fun switchPage(fragment:Fragment){
        currentPage=fragment
        val transaction=supportFragmentManager.beginTransaction()
        if(fragment.isAdded){
            transaction.show(fragment)
        }else{
            transaction.replace(R.id.fragment_main,fragment)
        }
        transaction.commit()
    }

    companion object{
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,LoginActivity::class.java))
        }
    }
}