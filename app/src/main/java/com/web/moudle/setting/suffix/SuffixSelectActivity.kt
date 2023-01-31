package com.web.moudle.setting.suffix

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.base.OnItemClickListener
import com.web.common.base.log
import com.web.common.constant.AppConfig
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.data.ScanMusicType
import com.web.misc.GapItemDecoration
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.preference.SP
import com.music.m.R
import com.web.moudle.setting.suffix.adapter.*
import kotlinx.android.synthetic.main.activity_select_suffix.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litepal.crud.DataSupport
import java.util.*
import java.util.regex.Pattern

class SuffixSelectActivity : BaseActivity() {
    private val types = ArrayList<ScanMusicType>()
    private val mConcatAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(), emptyList())
    private val mSuffixAdapter = SuffixSelectAdapter(types)
    private val mIgnorePathAdapter = IgnorePathAdapter()
    override fun getLayoutId(): Int {
        return R.layout.activity_select_suffix
    }

    override fun initView() {
        topBar.setEndImageListener {
            save()
        }
        if (isEnableSystemMusic()) {
            sw_enableSystemMusic.isChecked = true
        }
        sw_enableSystemMusic.setOnCheckedChangeListener { _, isChecked ->
            enableSystemMusic(isChecked)
        }
        rv_suffixSelect.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_suffixSelect.addItemDecoration(GapItemDecoration(bottom = ViewUtil.dpToPx(10f)))
        GlobalScope.launch(Dispatchers.IO) {
            types.addAll(getScanType())
            runOnUiThread {
                mConcatAdapter.addAdapter(SuffixTitleAdapter("需要扫描的音乐类型"))
                mConcatAdapter.addAdapter(mSuffixAdapter)
                mConcatAdapter.addAdapter(
                    AddNewAdapter().apply {
                        onClickListener = object : OnItemClickListener<String> {
                            override fun itemClick(item: String, position: Int) {
                                mSuffixAdapter.insert(-1)
                            }
                        }
                    }
                )
                mConcatAdapter.addAdapter(SuffixTitleAdapter("需要排除的音乐路径"))
                mConcatAdapter.addAdapter(mIgnorePathAdapter)
                mConcatAdapter.addAdapter(
                    AddNewAdapter().apply {
                        onClickListener = object : OnItemClickListener<String> {
                            override fun itemClick(item: String, position: Int) {
                                mIgnorePathAdapter.insert(-1)
                            }
                        }
                    }
                )
                rv_suffixSelect.adapter = mConcatAdapter
                ItemTouchHelper(
                    object : MyItemTouchHelperCallBack({ holder, _ ->
                        (holder.bindingAdapter as? IAdapterAnimation)?.remove(holder.bindingAdapterPosition)
                    }) {
                        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                            if (viewHolder.bindingAdapter !is IAdapterAnimation) {
                                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.ACTION_STATE_SWIPE)
                            }
                            return super.getMovementFlags(recyclerView, viewHolder)
                        }
                    }
                ).attachToRecyclerView(rv_suffixSelect)
            }
        }
    }

    private fun save() {
        currentFocus?.clearFocus() // **清除焦点保存数据
        mSuffixAdapter.save(this)
        mIgnorePathAdapter.save()
    }

    companion object {
        @JvmStatic
        val pattern = Pattern.compile("^\\.[0-9a-zA-Z]{1,10}[0-9a-zA-Z]*?$")

        @JvmStatic
        val minTime = 20

        @JvmStatic
        fun getScanType(): List<ScanMusicType> {
            val list = DataSupport.findAll<ScanMusicType>(ScanMusicType::class.java)
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
        fun enableSystemMusic(enable: Boolean) {
            AppConfig.enableSystemMusic = enable
        }
        fun isEnableSystemMusic(): Boolean = AppConfig.enableSystemMusic

        @JvmStatic
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, SuffixSelectActivity::class.java))
        }
    }
}