package com.web.moudle.setting.suffix

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseTextWatcher
import com.web.common.base.BaseViewHolder
import com.web.data.ScanMusicType
import com.web.web.R

class SuffixSelectAdapter(private val ctx:Context,private val list:ArrayList<ScanMusicType>): BaseAdapter<ScanMusicType>(list) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_suffix_select,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:ScanMusicType?) {
        val type=list[position]
        val editTextSuffix=holder.bindText(R.id.et_suffixItem_suffix, type.scanSuffix) as EditText
        val editTextSize=holder.bindText(R.id.et_suffixSize,"${type.minFileSize}")
        val selector=holder.findViewById<View>(R.id.view_suffixItem_selector)
        selector.isSelected = type.isScanable
        selector.setOnClickListener {
            type.isScanable=!type.isScanable
            it.isSelected=type.isScanable
        }

        editTextSuffix.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus)return@setOnFocusChangeListener
            val value=editTextSuffix.text.toString()
            if(value!=type.scanSuffix){
                type.scanSuffix=value
            }
        }
        editTextSuffix.addCheck()
        editTextSize.setOnFocusChangeListener { _,hasFocus ->
            if(hasFocus)return@setOnFocusChangeListener
            val value=if(editTextSize.text.toString().isEmpty()) 0 else editTextSize.text.toString().toInt()
            if(value!=type.minFileSize){
                type.minFileSize=value
            }
        }

        holder.rootView.findViewById<View>(R.id.iv_deleteItem).setOnClickListener {
            remove(position)
        }
    }

    private fun EditText.check(s:String){
        val m=SuffixSelectActivity.pattern.matcher(s)
        if(m.find()){
            setTextColor(Color.BLACK)
        }else{
            setTextColor(Color.RED)
        }
    }
    private fun EditText.addCheck(){
        check(text.toString())
        this.addTextChangedListener(object :BaseTextWatcher(){
            override fun afterTextChanged(s: Editable) {
                check(s.toString())
            }
        })
    }

    /**
     *  移除动画
     */
    fun remove(position:Int){
        list.removeAt(position).deleteAsync().listen {}
        notifyItemRemoved(position)
        if (position != list.size) {
            notifyItemRangeChanged(position, list.size - position)
        }
    }
}