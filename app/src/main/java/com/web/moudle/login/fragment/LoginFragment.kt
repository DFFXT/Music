package com.web.moudle.login.fragment

import android.app.Activity
import android.view.View
import com.web.common.base.BaseFragment
import com.web.common.base.get
import com.web.common.tool.MToast
import com.web.moudle.login.model.LoginModel
import com.web.moudle.user.User
import com.web.moudle.user.UserManager
import com.music.m.R
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment() {

    private val vm=LoginModel()
    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun initView(rootView: View) {


        rootView.et_account.setText(UserManager.getUserId().toString())

        rootView.tv_login.setOnClickListener {
            vm.login(rootView.et_account.text.toString().toLong(),rootView.et_password.text.toString())
                    .get(
                            onNext = {
                                if(it.code==200){
                                    UserManager.login(it)
                                    (context as Activity).finish()
                                }
                            },
                            onError = {
                                it.printStackTrace()
                            }
                    )
        }

    }
}