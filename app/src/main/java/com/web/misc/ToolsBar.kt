package com.web.misc

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.ViewStubCompat
import com.web.common.base.BaseActivity
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.web.R
import kotlinx.android.synthetic.main.layout_action_tools.view.*

/**
 * 自定义 伪ActionMode
 * 同一个activity不同的ToolsBar都是维护的一个toolsBar
 * 必须在onCreate之后实例化
 */
class ToolsBar(private val ctx:BaseActivity) {
    private var rootView: ViewGroup?=null
    private var parent:ViewGroup
    private var stub:ViewStubCompat?=null
    private var itemNames=ArrayList<String>()
    private var itemIds=ArrayList<Int>()

    var itemClick:((Int)->Unit)?=null
    //**返回false关闭
    var backClick:(()->Unit)?=null
    private var backListener= BaseActivity.KeyListener {
        when {
            rootView==null -> return@KeyListener false
            rootView!!.visibility!=View.VISIBLE -> return@KeyListener  false
            else -> {
                close()
                return@KeyListener true
            }
        }
    }
    var fitWindow=false
        set(value) {
            if(value==field)return
            if(addPadding(value)){//添加删除成功，否则保持不变
                field=value
            }
        }
    init {
        val decorView=ctx.window.decorView
        val linearLayout=((decorView as ViewGroup).getChildAt(0) as ViewGroup)
        parent=(linearLayout.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup
        if(parent.getChildAt(1) is ViewStubCompat){
            stub=parent.getChildAt(1) as ViewStubCompat
        }else{
            rootView=parent.getChildAt(1) as ViewGroup
        }
    }

    fun removeAllItem():ToolsBar{
        rootView?.layout_itemBox?.removeAllViews()
        itemIds.clear()
        itemNames.clear()
        return this
    }
    fun addItem(id:Int,@StringRes itemName:Int):ToolsBar{
        return addItem(id,ResUtil.getString(itemName))
    }
    fun addItem(id:Int,itemName:String):ToolsBar{
        if(!itemIds.contains(id)){
            itemNames.add(itemName)
            itemIds.add(id)
        }

        if(rootView!=null){
            val tv=createItem(id,itemName)
            rootView?.layout_itemBox?.addView(tv)
        }
        return this
    }
    private var addPadding=false
    private fun addPadding(add:Boolean):Boolean{//**添加删除padding 返回是否真正添加
        if(add&&!addPadding){
            rootView!!.setPadding(rootView!!.paddingStart,rootView!!.paddingTop+WindowUtil.getStatusHeight(),rootView!!.paddingEnd,rootView!!.paddingBottom)
            if(rootView!=null) addPadding=true
        }else if(addPadding){
            rootView!!.setPadding(rootView!!.paddingStart,rootView!!.paddingTop-WindowUtil.getStatusHeight(),rootView!!.paddingEnd,rootView!!.paddingBottom)
            if(rootView!=null)addPadding=false
        }
        return rootView!=null
    }
    private fun createItem(id: Int,itemName: String):TextView{
        val tv=TextView(ctx)
        tv.text=itemName
        tv.setTextColor(Color.WHITE)
        tv.setPadding(0,0,20,0)
        tv.setOnClickListener{
            itemClick?.invoke(id)
        }
        return tv
    }

    @SuppressLint("RestrictedApi")
    fun show():ViewGroup{
        if(rootView==null){
            stub!!.layoutResource= R.layout.layout_action_tools
            rootView=stub!!.inflate() as ViewGroup
            for(i in itemIds.size-1 downTo 0){
                rootView?.layout_itemBox?.addView(createItem(itemIds[i],itemNames[i]))
            }
            rootView?.setOnClickListener{}
            rootView?.iv_back?.setOnClickListener { close() }
            addPadding(fitWindow)
        }else{
            rootView?.visibility=View.VISIBLE
        }
        ctx.addKeyEventListener(backListener)
        return rootView!!
    }
    fun close(){
        rootView?.visibility=View.GONE
        ctx.removeKeyListener(backListener)
        backClick?.invoke()
    }
}