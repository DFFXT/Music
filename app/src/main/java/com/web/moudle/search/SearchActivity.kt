package com.web.moudle.search

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.web.common.base.BaseActivity
import com.web.common.util.WindowUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.search.adapter.SearchSugAdapter
import com.web.moudle.search.model.SearchViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    private lateinit var viewModel: SearchViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private fun loadData() {
        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]
        viewModel.searchSug.observe(this, Observer { res ->
            var adapter = rv_searchSug.adapter
            if (adapter == null) {
                adapter = SearchSugAdapter(res!!)
                rv_searchSug.adapter = adapter
            } else {
                (adapter as SearchSugAdapter).searchSug = res!!
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window = window)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        loadData()

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        manager.stackFromEnd = false
        rv_searchSug.layoutManager = manager
        rv_searchSug.addItemDecoration(GapItemDecoration(bottom = 20))

        searchView_searchActivity.searchCallback = {
            val inputManager=getSystemService(InputMethodManager::class.java)
            inputManager.hideSoftInputFromWindow(searchView_searchActivity.windowToken,0)
            val intent=Intent()
            intent.putExtra(INPUT_DATA,it)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        searchView_searchActivity.textChangeCallback = {
            viewModel.getSearchSug(it)
        }

    }



    companion object {
        @JvmStatic
        fun actionStart(ctx: Activity, resCode: Int) {
            val intent = Intent(ctx, SearchActivity::class.java)
            ctx.startActivityForResult(intent,resCode)
        }
        const val INPUT_DATA="input_data"
    }
}