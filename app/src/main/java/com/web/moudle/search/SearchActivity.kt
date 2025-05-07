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
import com.music.m.R
import com.music.m.databinding.ActivitySearchBinding
import com.web.common.base.BaseViewBindingActivity
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.misc.ConfirmDialog
import com.web.moudle.search.adapter.SearchSugAdapter
import com.web.moudle.search.bean.SearchResItem
import com.web.moudle.search.model.SearchViewModel
import org.litepal.crud.DataSupport

class SearchActivity : BaseViewBindingActivity<ActivitySearchBinding>() {
    private lateinit var viewModel: SearchViewModel


    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private fun loadData() {
        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]
        viewModel.searchSug.observe(this, Observer { res ->
            binding.tvType.text = ResUtil.getString(R.string.searchView_searchRes) // 替换 tv_type 为 binding.tvType
            setSearchData(res)
        })
        viewModel.defSearchRes.observe(this, Observer<List<SearchResItem>> { res ->
            if (res == null) return@Observer
            binding.tvType.text = ResUtil.getString(R.string.searchView_desc) // 替换 tv_type 为 binding.tvType
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
        binding.rvSearchSug.layoutManager = manager // 替换 rv_searchSug 为 binding.rvSearchSug

        binding.searchViewSearchActivity.searchCallback = { // 替换 searchView_searchActivity 为 binding.searchViewSearchActivity
            val inputManager = getSystemService(InputMethodManager::class.java)
            inputManager.hideSoftInputFromWindow(binding.searchViewSearchActivity.windowToken, 0)
            finish(it)
        }
        binding.searchViewSearchActivity.textChangeCallback = { // 替换 searchView_searchActivity 为 binding.searchViewSearchActivity
            if (it == "") {
                viewModel.defSearch()
            } else {
                viewModel.getSearchSug(it)
            }
        }
        binding.searchViewSearchActivity.cancelCallback = { // 替换 searchView_searchActivity 为 binding.searchViewSearchActivity
            finish()
        }
    }

    private fun finish(keyword: String) {
        val intent = Intent()
        intent.putExtra(INPUT_DATA, keyword)
        setResult(Activity.RESULT_OK, intent)
        val item = SearchResItem(keyword, "", "", SearchResItem.SearchItemType_Search)
        item.saveOrUpdateAsync()
        finish()
    }

    private fun setSearchData(res: List<SearchResItem>) {
        var adapter = binding.rvSearchSug.adapter // 替换 rv_searchSug 为 binding.rvSearchSug
        if (adapter == null) {
            adapter = SearchSugAdapter(res)
            binding.rvSearchSug.adapter = adapter // 替换 rv_searchSug 为 binding.rvSearchSug
            adapter.search = {
                finish(it)
            }
            adapter.clearAllHistory = {
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
                    .showCenter(binding.searchViewSearchActivity) // 替换 searchView_searchActivity 为 binding.searchViewSearchActivity
            }
            adapter.itemClick = { _, _ ->
                binding.searchViewSearchActivity.postDelayed({ // 替换 searchView_searchActivity 为 binding.searchViewSearchActivity
                    viewModel.refreshHistory()
                }, 1000)
            }
        } else {
            (adapter as SearchSugAdapter).update(res)
        }
    }



    companion object {
        @JvmStatic
        fun actionStart(ctx: Activity, resCode: Int) {
            val intent = Intent(ctx, SearchActivity::class.java)
            ctx.startActivityForResult(intent, resCode)
        }

        @JvmStatic
        fun actionStart(fragment: Fragment, resCode: Int) {
            val intent = Intent(fragment.context, SearchActivity::class.java)
            fragment.startActivityForResult(intent, resCode)
        }

        const val INPUT_DATA = "input_data"
    }
}