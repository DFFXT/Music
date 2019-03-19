package com.web.misc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.web.common.util.KeyboardManager
import com.web.web.R
import kotlinx.android.synthetic.main.item_edit.view.*

/**
 * 输入框，
 * 默认无法输入
 * 点击编辑弹出输法
 * 再次点击收起，无法输入，进行回调
 */
class InputItem @JvmOverloads constructor(ctx: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0):FrameLayout(ctx,attrs,defStyleAttr) {
    private val mRootView: View = LayoutInflater.from(ctx).inflate(R.layout.item_edit,this,true)
    val inputBox:EditText
    val clickButton:ImageView
    var listener:((text:String)->Unit)?=null
    var listenSelect:((isSelect:Boolean)->Unit)?=null
    init {
        inputBox=mRootView.et_name
        inputBox.isFocusableInTouchMode=false
        clickButton=mRootView.iv_changeStatus
        clickButton.setOnClickListener {
            it.isSelected=!it.isSelected
            listenSelect?.invoke(it.isSelected)
            inputBox.isFocusableInTouchMode=it.isSelected
            if(it.isSelected){
                inputBox.setOnClickListener(null)
                inputBox.setSelection(inputBox.text.length)
                KeyboardManager.requestKeyboard(inputBox)
            }else{
                KeyboardManager.hideKeyboard(it.context,it.windowToken)
                inputBox.clearFocus()
                listener?.invoke(inputBox.text.toString())
            }
        }
    }
    fun setText(text:String){
        inputBox.setText(text)
    }
}