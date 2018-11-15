package com.web.common.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment :Fragment(){
    private var mRootView:View?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(mRootView!=null)return mRootView
        this.mRootView =inflater.inflate(getLayoutId(),container,false)
        initView(mRootView!!)
        return this.mRootView
    }
    public fun isInit():Boolean{
        return mRootView!=null
    }
    @LayoutRes abstract fun getLayoutId():Int
    abstract fun initView(rootView:View)
}