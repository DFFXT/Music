package com.web.moudle.setting.suffix

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.web.common.base.BaseActivity
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.data.ScanMusicType
import com.web.web.R
import kotlinx.android.synthetic.main.activity_select_suffix.*
import org.litepal.crud.DataSupport

class SuffixSelectActivity: BaseActivity() {
    private lateinit var types:List<ScanMusicType>
    override fun getLayoutId(): Int {
        return R.layout.activity_select_suffix
    }

    override fun initView() {
        types=DataSupport.findAll(ScanMusicType::class.java)
        rv_suffixSelect.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_suffixSelect.adapter=object :BaseAdapter(types){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return BaseViewHolder(layoutInflater.inflate(R.layout.item_suffix_select,parent,false))
            }

            override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
                val type=types[position]
                holder.bindText(R.id.tv_suffixItem_suffix, type.scanSuffix)
                holder.bindText(R.id.et_suffixSize,"${type.minFileSize}")
                val selector=holder.findViewById<View>(R.id.view_suffixItem_selector)
                selector.isSelected = type.isScanable
                selector.setOnClickListener {
                    type.isScanable=!type.isScanable
                    it.isSelected=type.isScanable
                    type.saveAsync()
                }
                val editText=holder.findViewById<EditText>(R.id.et_suffixSize)
                editText.setOnKeyListener { v, _, _ ->
                    val value=(v as EditText).text.toString().toInt()
                    if(value!=type.minFileSize){
                        type.minFileSize=value
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }

    override fun finish() {
        DataSupport.saveAll(types)//**不能异步保存
        super.finish()
    }

    companion object {
        fun actionStart(context:Context){
            context.startActivity(Intent(context,SuffixSelectActivity::class.java))
        }
    }

}