package com.web.moudle.setting.suffix

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.base.log
import com.web.common.constant.Constant
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.data.ScanMusicType
import com.web.misc.GapItemDecoration
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.preference.SP
import com.web.web.R
import kotlinx.android.synthetic.main.activity_select_suffix.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litepal.crud.DataSupport
import java.util.*
import java.util.regex.Pattern

class SuffixSelectActivity : BaseActivity() {
    private val types = ArrayList<ScanMusicType>()
    override fun getLayoutId(): Int {
        return R.layout.activity_select_suffix
    }

    override fun initView() {
        topBar.setEndImageListener(View.OnClickListener {
            save()
        })
        if(isEnableSystemMusic()){
            sw_enableSystemMusic.isChecked=true
        }
        sw_enableSystemMusic.setOnCheckedChangeListener { _, isChecked ->
            enableSystemMusic(isChecked)
        }
        rv_suffixSelect.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_suffixSelect.addItemDecoration(GapItemDecoration(bottom = ViewUtil.dpToPx(10f)))
        tv_addAndSave.setOnClickListener {
            rv_suffixSelect.adapter?.let { adapter ->
                types.add(ScanMusicType(".", minTime, true))
                adapter.notifyItemInserted(types.size - 1)
                adapter.notifyItemRangeChanged(types.size - 1, 1)
            }
        }
        GlobalScope.launch (Dispatchers.IO){
            types.addAll(getScanType())
            runOnUiThread {
                rv_suffixSelect.adapter = SuffixSelectAdapter(this@SuffixSelectActivity, types)
                ItemTouchHelper(MyItemTouchHelperCallBack { holder, _ ->
                    (rv_suffixSelect.adapter as SuffixSelectAdapter).remove(holder.adapterPosition)
                }).attachToRecyclerView(rv_suffixSelect)
            }
        }
    }

    private fun save() {
        currentFocus?.clearFocus()//**清除焦点保存数据
        var i = 0
        while (i < types.size && i >= 0) {
            val m = pattern.matcher(types[i].scanSuffix)
            val p=m.find()
            log(types[i].scanSuffix  +" "+ p)
            if (!p) {
                types.removeAt(i)
                i -= 1
            }
            i += 1
        }
        DataSupport.saveAllAsync(types).listen {
            MToast.showToast(this,R.string.setting_suffix_saveSuccess)
        }

    }

    companion object {
        @JvmStatic
        val pattern = Pattern.compile("^\\.[0-9a-zA-Z]{1,10}[0-9a-zA-Z]*?$")!!

        @JvmStatic
        val minTime=20

        @JvmStatic
        fun getScanType(): List<ScanMusicType> {
            val list = DataSupport.findAll(ScanMusicType::class.java)
            if (list.isEmpty()) {
                val suffixList = ResUtil.getStringArray(R.array.initialSuffix)
                suffixList.forEach {
                    val type = ScanMusicType(it, minTime, true)
                    list.add(type)
                }
                DataSupport.saveAllAsync(list)
            }
            return list
        }

        @JvmStatic
        fun enableSystemMusic(enable:Boolean){
            SP.putValue(Constant.spName,Constant.SpKey.enableSystemMusic,enable)
        }
        fun isEnableSystemMusic():Boolean{
            return SP.getBoolean(Constant.spName,Constant.SpKey.enableSystemMusic,true)
        }

        @JvmStatic
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, SuffixSelectActivity::class.java))
        }
    }

}