package com.web.common.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment :Fragment(){
    private var created=false
    var rootView:View?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(rootView!=null)return rootView
        this.rootView =inflater.inflate(getLayoutId(),container,false)
        initView(rootView!!)
        return this.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        created=true
        viewCreated(view,savedInstanceState)
    }
    fun isInit():Boolean{
        return created
    }
    @LayoutRes abstract fun getLayoutId():Int
    abstract fun initView(rootView:View)
    abstract fun viewCreated(view: View, savedInstanceState: Bundle?)
    override fun onDestroy() {
        super.onDestroy()
        created=false
    }
}