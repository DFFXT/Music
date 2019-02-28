package com.web.moudle.setting.suffix

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.data.ScanMusicType
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.web.R
import kotlinx.android.synthetic.main.activity_select_suffix.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.litepal.crud.DataSupport
import java.util.*
import java.util.regex.Pattern

class SuffixSelectActivity : BaseActivity() {
    private val types = ArrayList<ScanMusicType>()
    private val pattern=Pattern.compile("^\\.[0-9a-zA-Z]*?&")
    override fun getLayoutId(): Int {
        return R.layout.activity_select_suffix
    }

    override fun initView() {
        rv_suffixSelect.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        tv_addAndSave.setOnClickListener {
            rv_suffixSelect.adapter?.let { adapter ->
                types.add(ScanMusicType("", 10240, true))
                adapter.notifyItemInserted(types.size - 1)
                adapter.notifyItemRangeChanged(types.size - 1, 1)
            }
        }
        GlobalScope.launch {
            types.addAll(getScanType())
            GlobalScope.async {
                rv_suffixSelect.adapter = SuffixSelectAdapter(this@SuffixSelectActivity, types)
                ItemTouchHelper(MyItemTouchHelperCallBack { holder, _ ->
                    (rv_suffixSelect.adapter as SuffixSelectAdapter).remove(holder.adapterPosition)
                }).attachToRecyclerView(rv_suffixSelect)
            }
        }
    }

    override fun finish() {
        currentFocus?.clearFocus()//**清除焦点保存数据
        var i = 0
        while (i < types.size) {
            val m=pattern.matcher(types[i].scanSuffix)
            if (!m.find()) {
                types.removeAt(i)
                i -= 1
            }
            i += 1
        }
        DataSupport.saveAllAsync(types).listen {
            super.finish()
        }

    }

    companion object {
        @JvmStatic
        val pattern= Pattern.compile("^\\.[0-9a-zA-Z]*?$")!!

        @JvmStatic
        fun getScanType(): List<ScanMusicType> {
            val list = DataSupport.findAll(ScanMusicType::class.java)
            if (list.isEmpty()) {
                val suffixList = arrayListOf(".mp3", ".acc", ".wma", ".wav", ".mid")
                suffixList.forEach {
                    val type = ScanMusicType(it, 10240, true)
                    list.add(type)
                }
                DataSupport.saveAllAsync(list)
            }
            return list
        }

        @JvmStatic
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, SuffixSelectActivity::class.java))
        }
    }

}