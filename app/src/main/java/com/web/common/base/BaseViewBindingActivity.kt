package com.web.common.base

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * ViewBinding基类
 */
abstract class BaseViewBindingActivity<T : ViewBinding> : BaseActivity() {
    protected lateinit var mBinding: T
    override fun viewBindingInit(): View {
        val type = this.javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        val method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
        mBinding = (method.invoke(null, layoutInflater) as T)
        return mBinding.root
    }

    // 不用再重载这个方法了
    @Deprecated("不用再重载该方法了，即使重载也不会生效", ReplaceWith("0"))
    override fun getLayoutId(): Int = 0
}