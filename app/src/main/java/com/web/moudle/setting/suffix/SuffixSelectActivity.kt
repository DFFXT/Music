package com.web.moudle.setting.suffix

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseActivity
import com.web.data.ScanMusicType
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.web.R
import kotlinx.android.synthetic.main.activity_select_suffix.*
import org.litepal.crud.DataSupport
import org.litepal.crud.callback.FindMultiCallback
import java.util.*

class SuffixSelectActivity: BaseActivity() {
    private val types=ArrayList<ScanMusicType>()
    override fun getLayoutId(): Int {
        return R.layout.activity_select_suffix
    }

    override fun initView() {
        rv_suffixSelect.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        tv_addAndSave.setOnClickListener {
            rv_suffixSelect.adapter?.let {adapter->
                types.add(ScanMusicType("",10240,true))
                adapter.notifyItemInserted(types.size-1)
                adapter.notifyItemRangeChanged(types.size-1,1)
            }
        }
        DataSupport.findAllAsync(ScanMusicType::class.java).listen(object :FindMultiCallback{
            override fun <T : Any?> onFinish(t: MutableList<T>) {
                if(t.isEmpty()){
                    val suffixList= arrayListOf(".mp3",".acc",".wma",".wav",".mid")
                    suffixList.forEach{
                        val type=ScanMusicType(it,10240,true)
                        t.add(type as T)
                    }
                    DataSupport.saveAllAsync(t as List<ScanMusicType>)
                }
                types.addAll(t as List<ScanMusicType>)

                rv_suffixSelect.adapter=SuffixSelectAdapter(this@SuffixSelectActivity,types)
                ItemTouchHelper(MyItemTouchHelperCallBack { holder, _ ->
                    (rv_suffixSelect.adapter as SuffixSelectAdapter).remove(holder.adapterPosition)
                }).attachToRecyclerView(rv_suffixSelect)

            }
        })
    }

    override fun finish() {
        currentFocus.clearFocus()//**清除焦点保存数据
        var i=0
        while (i<types.size){
            if(TextUtils.isEmpty(types[i].scanSuffix)){
                types.removeAt(i)
                i -= 1
            }
            i +=1
        }
        DataSupport.saveAllAsync(types).listen {
            super.finish()
        }

    }

    companion object {
        fun actionStart(context:Context){
            context.startActivity(Intent(context,SuffixSelectActivity::class.java))
        }
    }

}