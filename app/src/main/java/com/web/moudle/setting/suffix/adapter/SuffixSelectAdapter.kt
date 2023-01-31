package com.web.moudle.setting.suffix.adapter

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseTextWatcher
import com.web.common.base.BaseViewHolder
import com.web.common.base.log
import com.web.common.tool.MToast
import com.web.data.ScanMusicType
import com.web.moudle.setting.suffix.SuffixSelectActivity
import com.web.web.R
import org.litepal.crud.DataSupport

/**
 * suffix 选择器
 */
class SuffixSelectAdapter(private val list: ArrayList<ScanMusicType>) : BaseAdapter<ScanMusicType>(list), IAdapterAnimation {

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_suffix_select
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: ScanMusicType) {
        val type = list[position]
        val editTextSuffix = holder.bindText(R.id.et_suffixItem_suffix, type.scanSuffix) as EditText
        val editTextSize = holder.bindText(R.id.et_suffixSize, "${type.minTime}")
        val selector = holder.findViewById<CheckBox>(R.id.checkBox)
        selector.isChecked = type.isScanable
        selector.setOnCheckedChangeListener { buttonView, isChecked ->
            type.isScanable = isChecked
        }

        editTextSuffix.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)return@setOnFocusChangeListener
            val value = editTextSuffix.text.toString()
            if (value != type.scanSuffix) {
                type.scanSuffix = value
            }
        }
        editTextSuffix.addCheck()
        editTextSize.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)return@setOnFocusChangeListener
            val value = if (editTextSize.text.toString().isEmpty()) 0 else editTextSize.text.toString().toInt()
            if (value != type.minTime) {
                type.minTime = value
            }
        }

        holder.itemView.findViewById<View>(R.id.iv_deleteItem).setOnClickListener {
            remove(position)
        }
    }

    private fun EditText.check(s: String) {
        val m = SuffixSelectActivity.pattern.matcher(s)
        if (m.find()) {
            setTextColor(Color.BLACK)
        } else {
            setTextColor(Color.RED)
        }
    }
    private fun EditText.addCheck() {
        check(text.toString())
        this.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                check(s.toString())
            }
        })
    }

    /**
     *  移除动画
     */
    override fun remove(position: Int) {
        list.removeAt(position).deleteAsync().listen {}
        notifyItemRemoved(position)
    }

    override fun insert(position: Int) {
        list.add(ScanMusicType(".", SuffixSelectActivity.minTime, true))
        notifyItemInserted(list.size - 1)
        notifyItemRangeChanged(list.size - 1, 1)
    }

    /**
     * 保存数据
     */
    fun save(ctx: Context) {
        var i = 0
        while (i < list.size && i >= 0) {
            val m = SuffixSelectActivity.pattern.matcher(list[i].scanSuffix)
            val p = m.find()
            log(list[i].scanSuffix + " " + p)
            if (!p) {
                list.removeAt(i)
                i -= 1
            }
            i += 1
        }
        DataSupport.saveAllAsync(list).listen {
            MToast.showToast(ctx, R.string.setting_suffix_saveSuccess)
        }
    }
}