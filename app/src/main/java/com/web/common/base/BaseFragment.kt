package com.web.common.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment public constructor ():Fragment(){
    private var mRootView:View?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.mRootView =inflater.inflate(getLayoutId(),container,false)
        initView(mRootView!!)
        return this.mRootView
    }
    @LayoutRes abstract fun getLayoutId():Int
    abstract fun initView(rootView:View)
}