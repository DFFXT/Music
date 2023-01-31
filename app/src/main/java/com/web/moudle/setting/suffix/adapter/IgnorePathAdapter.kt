package com.web.moudle.setting.suffix.adapter

import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseTextWatcher
import com.web.common.base.BaseViewHolder
import com.web.moudle.setting.suffix.sp.IgnorePath
import com.web.web.R

/**
 * 路径忽略adapter
 */
class IgnorePathAdapter : BaseAdapter<IgnorePath.IgnoreItem>(), IAdapterAnimation {
    private val mIgnorePath = IgnorePath()
    init {
        data = mIgnorePath.ignorePathList
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: IgnorePath.IgnoreItem) {
        holder.findViewById<EditText>(R.id.et_text).apply {
            setText(item.path)
            addTextChangedListener(object :BaseTextWatcher(){
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    item.path = s.toString()
                }
            })
        }
        holder.findViewById<CheckBox>(R.id.checkBox).apply {
            isChecked = !item.disabled
            setOnCheckedChangeListener { _, isChecked ->
                item.disabled = !isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_filter_ignore_path
    }

    override fun remove(position: Int) {
        val mNewData = mIgnorePath.ignorePathList
        mNewData.removeAt(position)
        data = mNewData
        notifyItemRemoved(position)
    }

    override fun insert(position: Int) {
        val list = mIgnorePath.ignorePathList
        list.add(IgnorePath.IgnoreItem("", false))
        notifyItemInserted(list.size - 1)
        notifyItemRangeChanged(list.size - 1, 1)
    }

    /**
     * 保存数据
     */
    fun save() {
        mIgnorePath.ignorePathList = mutableListOf<IgnorePath.IgnoreItem>().apply {
            addAll(data)
        }
    }
}