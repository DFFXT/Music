package com.web.moudle.login.fragment

import android.app.Activity
import android.view.View
import com.music.m.R
import com.music.m.databinding.FragmentLoginBinding
import com.web.common.base.BaseFragment
import com.web.common.base.get
import com.web.moudle.login.model.LoginModel
import com.web.moudle.user.UserManager

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val vm = LoginModel()
    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun initView(rootView: View) {
        binding.etAccount.setText(UserManager.getUserId().toString())

        binding.tvLogin.setOnClickListener {
            vm.login(
                binding.etAccount.text.toString().toLong(),
                binding.etPassword.text.toString()
            ).get(
                onNext = {
                    if (it.code == 200) {
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
