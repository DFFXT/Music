package com.web.moudle.login.fragment

import android.app.Activity
import android.view.View
import com.web.common.base.BaseFragment
import com.web.common.base.BaseObserver
import com.web.common.base.get
import com.web.common.tool.MToast
import com.web.moudle.login.bean.RegistResponse
import com.web.moudle.login.model.LoginModel
import com.web.moudle.user.UserManager
import com.music.m.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : BaseFragment() {

    private val vm:LoginModel=LoginModel()
    override fun getLayoutId(): Int = R.layout.fragment_register

    private val observer=object:BaseObserver<RegistResponse>(){
        override fun onNext(t: RegistResponse) {

        }
    }

    override fun initView(rootView: View) {


        rootView.tv_register.setOnClickListener {
            val account=rootView.et_account.text.toString()
            val pwd1=rootView.et_password.text.toString()
            val pwd2=rootView.et_passwordConfirm.text.toString()
            val nickname=rootView.et_nickName.text.toString()
            if(check(account, pwd1, pwd2, nickname)){
                vm.register(account,pwd1,nickname)
                        .get(onNext = {
                            if(it.code==200){
                                UserManager.login(it)
                                (context as Activity).finish()
                            }
                        },onError = {
                            it.printStackTrace()
                        })
            }
        }
    }


    private fun check(account:String,pwd1:String,pwd2:String,nikcName:String):Boolean{
        if(account.length<4||account.length>11){
            MToast.showToast(context!!,"--------?")
            return false
        }

        if(pwd1!=pwd2){
            MToast.showToast(context!!,"pwd1!=pwd2")
            return false
        }

        return true
    }


}