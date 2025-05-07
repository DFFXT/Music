package com.web.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseFragment<T: ViewBinding> : Fragment(){
    //**fragment的title，一般用于tabLayout

    protected lateinit var binding: T
    private fun viewBindingInit(view: View): View {
        var isBreak = false
        var type = this.javaClass.genericSuperclass
        while (!isBreak) {
            if (type is ParameterizedType) {
                type.actualTypeArguments.forEach {
                    val cls = it as Class<*>
                    if (ViewBinding::class.java.isAssignableFrom(cls)) {
                        val method = cls.getDeclaredMethod("bind", View::class.java)
                        binding = method.invoke(null, view) as T
                        isBreak = true
                        return@forEach
                    }
                }
            }
            type = type.javaClass.genericSuperclass
        }

        return binding.root
    }

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
        viewBindingInit(rootView!!)
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