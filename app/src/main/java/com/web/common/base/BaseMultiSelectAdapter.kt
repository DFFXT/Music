package com.web.common.base

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import com.music.m.R
import com.web.common.util.ViewUtil

abstract class BaseMultiSelectAdapter<T : Any?>(private val ctx: Context, list: List<T>?) : BaseAdapter<T>(list) {

    companion object {
        private const val limit: Int = 10000000
        const val TYPE_NONE_SELECTOR = 1
        const val TYPE_LEFT_SELECTOR = 2
        const val TYPE_RIGHT_SELECTOR = 3

        @IntDef(TYPE_LEFT_SELECTOR, TYPE_NONE_SELECTOR, TYPE_RIGHT_SELECTOR)
        annotation class MultiSelectType
    }
    private val padding = ViewUtil.dpToPx(10f)

    val selectSet = HashSet<Int>()
    var isSelectAll = false
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    data.let {
                        for (i in it.indices) {
                            selectSet.add(i)
                        }
                    }
                } else {
                    selectSet.clear()
                }
                notifyItemRangeChanged(0, itemCount)
            }
        }
    var isSelect = false
        set(value) {
            if (field != value) {
                field = value
                selectSet.clear()
                if (!value) {
                    isSelectAll = false
                }
                notifyItemRangeChanged(0, itemCount)
            }
        }

    /**
     * 数据绑定、此处控制checkBox显示和隐藏
     */
    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: T) {
        val cb = holder.findViewById<View>(R.id.cb_itemMultiSelect)
        if (isSelect && getSelectType(position) != TYPE_NONE_SELECTOR) {
            cb.visibility = View.VISIBLE
            if (getSelectType(position) == TYPE_LEFT_SELECTOR) {
                holder.itemView.setPadding(padding, 0, 0, 0)
            } else {
                holder.itemView.setPadding(0, 0, 0, padding)
            }
            cb.isSelected = selectSet.contains(position)
            cb.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected)
                    selectSet.add(position)
                else selectSet.remove(position)
            }
        } else {
            cb.visibility = View.GONE
            holder.itemView.setPadding(0, 0, 0, 0)
            cb.setOnClickListener(null)
        }
        onBindItemView(holder, position, item)
    }

    /**
     * 创建itemView，将itemView放入带CheckBox的rootView
     */
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType / limit) {
            TYPE_LEFT_SELECTOR -> {
                BaseViewHolder(createParent(R.layout.item_multi_select_root_view, parent, viewType))
            }
            TYPE_RIGHT_SELECTOR -> {
                BaseViewHolder(createParent(R.layout.item_multi_select_right_root_view, parent, viewType))
            }
            else -> {
                BaseViewHolder(createParent(R.layout.item_multi_select_right_root_view, parent, viewType))
            }
        }
    }

    private fun createParent(@LayoutRes layout: Int, parent: ViewGroup, viewType: Int): ViewGroup {
        val rootView = LayoutInflater.from(ctx).inflate(layout, parent, false) as ViewGroup
        val child = onCreateItemView(rootView, viewType % limit)
        var lp = child.layoutParams
        if (lp == null) {
            lp = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        }

        if (viewType / limit == TYPE_LEFT_SELECTOR) {
            lp = lp as LinearLayout.LayoutParams
            lp.gravity = Gravity.CENTER_VERTICAL
            rootView.addView(child)
        } else {
            lp = lp as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.ALIGN_PARENT_START)
            lp.addRule(RelativeLayout.START_OF, R.id.cb_itemMultiSelect)
            lp.addRule(RelativeLayout.CENTER_VERTICAL)
            rootView.addView(child, 0)
        }
        child.layoutParams = lp
        return rootView
    }

    fun select(position: Int) {
        selectSet.add(position)
        notifyItemChanged(position)
    }

    fun unSelect(position: Int) {
        selectSet.remove(position)
        notifyItemChanged(position)
    }

    fun toggleSelect(position: Int) {
        if (selectSet.contains(position)) {
            unSelect(position)
        } else {
            select(position)
        }
    }

    fun <M : Any> getSelectList(interceptor: ((T, Int) -> M)): List<M> {
        val list = ArrayList<M>()
        selectSet.forEach {
            list.add(interceptor.invoke(data[it], it))
        }
        return list
    }

    final override fun getItemViewType(position: Int): Int {
        return getSelectType(position) * limit + getViewType(position)
    }

    abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): View
    abstract fun onBindItemView(holder: BaseViewHolder, position: Int, item: T)
    /**
     *
     */
    abstract fun getSelectType(position: Int): Int

    @MultiSelectType
    /**
     * 返回viewType，viewType>=0 viewType<limit
     */
    open fun getViewType(@IntRange(from = 0, to = limit.toLong() - 1) position: Int): Int {
        return position
    }
}