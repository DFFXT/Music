package com.web.moudle.setting.chooser

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.base.OnItemClickListener
import com.web.common.constant.Constant
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.setting.chooser.adapter.LocalChooserAdapter
import com.web.moudle.setting.chooser.bean.LocalItem
import com.web.moudle.setting.chooser.model.LocalChooserViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_local_chooser.*
import java.io.File

class LocalChooserActivity:BaseActivity() {
    private var model:LocalChooserViewModel?=null
    private var selected:Boolean=false
    override fun getLayoutId(): Int= R.layout.activity_local_chooser

    private val adapter=LocalChooserAdapter()
    private var action:String= ACTION_FILE_SELECT
    private var selectValue=""

    override fun initView() {
        model=ViewModelProviders.of(this)[LocalChooserViewModel::class.java]
        action=intent.getStringExtra(INTENT_DATA)
        when(action){
            ACTION_FILE_SELECT->{
                topBar.setMainTitle(ResUtil.getString(R.string.chooser_fileSelect))
            }
            ACTION_DIR_SELECT->{
                topBar.setMainTitle(ResUtil.getString(R.string.chooser_dirSelect))
            }
        }
        rv_localChooser.layoutManager=LinearLayoutManager(this)
        rv_localChooser.addItemDecoration(
                DrawableItemDecoration(0,0,0,2,
                        RecyclerView.VERTICAL,
                        ResUtil.getDrawable(R.drawable.recycler_divider)))
        rv_localChooser.adapter=adapter

        adapter.setItemClickListener(object :OnItemClickListener<LocalItem> {
            override fun itemClick(item: LocalItem,position:Int) {
                when(item.type) {
                    LocalItem.TYPE_FILE -> when(action){
                        ACTION_FILE_SELECT->{
                            adapter.select(position)
                        }
                    }
                    LocalItem.TYPE_DIR->{
                        model?.requestEntry(item.abPath)
                    }
                    LocalItem.TYPE_BACK -> model?.requestEntry(item.abPath)
                }
            }
        })
        adapter.setItemSelectListener(object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tv_select.setTextColor(ResUtil.getColor(R.color.gray))
                selected=false
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tv_select.setTextColor(ResUtil.getColor(R.color.white))
                selectValue=adapter.data!![position].abPath
                selected=true
            }
        })

        model?.localItemList?.observe(this, Observer<List<LocalItem>>{
            adapter.select(-1)
            adapter.update(it)
        })

        model?.currentPath?.observe(this,Observer<String>{
            selectValue=it
            tv_currentPath.text=it
        })
        //**选择文件夹模式下，任何目录都是可选的，所以颜色为白色
        if(action== ACTION_DIR_SELECT){
            tv_select.setTextColor(ResUtil.getColor(R.color.white))
            selected=true
        }
        model?.requestEntry(File(Constant.LocalConfig.cachePath))

        tv_select.setOnClickListener {
            if(!selected)return@setOnClickListener
            val intent=Intent()
            intent.putExtra(INTENT_DATA,selectValue)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }






    companion object {
        @JvmStatic
        val ACTION_FILE_SELECT="fileSelect"
        val ACTION_DIR_SELECT="dirSelect"
        @JvmStatic
        fun actionStartFileSelect(ctx:Activity,requestCode:Int){
            val intent=Intent(ctx,LocalChooserActivity::class.java)
            intent.putExtra(BaseActivity.INTENT_DATA, ACTION_FILE_SELECT)
            ctx.startActivityForResult(intent,requestCode)
        }
        @JvmStatic
        fun actionStartDirSelect(ctx:Activity,requestCode:Int){
            val intent=Intent(ctx,LocalChooserActivity::class.java)
            intent.putExtra(BaseActivity.INTENT_DATA, ACTION_DIR_SELECT)
            ctx.startActivityForResult(intent,requestCode)
        }
    }
}