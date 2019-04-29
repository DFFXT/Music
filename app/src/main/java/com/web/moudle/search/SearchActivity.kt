package com.web.moudle.search

import android.app.Activity
import android.content.Intent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.misc.ConfirmDialog
import com.web.moudle.search.adapter.SearchSugAdapter
import com.web.moudle.search.bean.SearchResItem
import com.web.moudle.search.model.SearchViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_search.*
import org.litepal.crud.DataSupport

class SearchActivity : BaseActivity() {
    private lateinit var viewModel: SearchViewModel


    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private fun loadData() {
        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]
        viewModel.searchSug.observe(this, Observer { res ->
            tv_type.text=ResUtil.getString(R.string.searchView_searchRes)
            setSearchData(res)
        })
        viewModel.defSearchRes.observe(this,Observer<List<SearchResItem>>{res->
            if(res==null)return@Observer
            tv_type.text=ResUtil.getString(R.string.searchView_desc)
            setSearchData(res)

        })

        viewModel.defSearch()
    }

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window = window)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        loadData()

        val manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        manager.stackFromEnd = false
        rv_searchSug.layoutManager = manager

        searchView_searchActivity.searchCallback = {
            val inputManager=getSystemService(InputMethodManager::class.java)
            inputManager.hideSoftInputFromWindow(searchView_searchActivity.windowToken,0)
            finish(it)
        }
        searchView_searchActivity.textChangeCallback = {
            if(it==""){
                viewModel.defSearch()
            }else{
                viewModel.getSearchSug(it)
            }

        }
        searchView_searchActivity.cancelCallback={
            finish()
        }
    }

    private fun finish(keyword:String){
        val intent=Intent()
        intent.putExtra(INPUT_DATA,keyword)
        setResult(Activity.RESULT_OK,intent)
        val item=SearchResItem(keyword,"","",SearchResItem.SearchItemType_Search)
        item.saveOrUpdateAsync()
        finish()
    }

    private fun setSearchData(res:List<SearchResItem>){
        var adapter = rv_searchSug.adapter
        if (adapter == null) {
            adapter = SearchSugAdapter(res)
            rv_searchSug.adapter = adapter
            adapter.search={
                finish(it)
            }
            adapter.clearAllHistory={
                ConfirmDialog(this)
                        .setMsg(ResUtil.getString(R.string.searchView_clearAllHistory))
                        .setLeftText(ResUtil.getString(R.string.no))
                        .setRightText(ResUtil.getString(R.string.yes))
                        .setLeftListener {
                            it.dismiss()
                        }
                        .setRightListener {
                            DataSupport.deleteAll(SearchResItem::class.java)
                            viewModel.refreshHistory()
                            it.dismiss()
                        }
                        .showCenter(searchView_searchActivity)
            }
            adapter.itemClick={_,_->
                searchView_searchActivity.postDelayed({
                    viewModel.refreshHistory()
                },1000)
            }
        } else {
            (adapter as SearchSugAdapter).update(res)
        }
    }



    companion object {
        @JvmStatic
        fun actionStart(ctx: Activity, resCode: Int) {
            val intent = Intent(ctx, SearchActivity::class.java)
            ctx.startActivityForResult(intent,resCode)
        }
        @JvmStatic
        fun actionStart(fragment: Fragment, resCode: Int) {
            val intent = Intent(fragment.context, SearchActivity::class.java)
            fragment.startActivityForResult(intent,resCode)
        }
        const val INPUT_DATA="input_data"
    }
}