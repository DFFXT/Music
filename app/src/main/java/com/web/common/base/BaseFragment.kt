package com.web.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){
    //**fragment的title，一般用于tabLayout
    open var title=""
    private var created=false
    var rootView:View?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(rootView!=null)return rootView
        if(getLayoutId()!=0){
            this.rootView =inflater.inflate(getLayoutId(),container,false)
        }else{
            this.rootView=getLayoutView()
        }

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
    open fun getLayoutView():View?{
        throw Exception("need a view")
    }
    abstract fun initView(rootView:View)
    open fun viewCreated(view: View, savedInstanceState: Bundle?){}
    override fun onDestroy() {
        super.onDestroy()
        created=false
    }
}