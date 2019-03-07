package com.web.moudle.search

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.search.adapter.SearchSugAdapter
import com.web.moudle.search.bean.DefSearchRes
import com.web.moudle.search.bean.SearchSug
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
            tv_type.visibility= View.VISIBLE
            tv_type.text=ResUtil.getString(R.string.searchView_searchRes)
            setSearchData(res!!)
        })
        viewModel.defSearchRes.observe(this,Observer<DefSearchRes>{res->
            if(res==null)return@Observer
            tv_type.visibility= View.VISIBLE
            tv_type.text=ResUtil.getString(R.string.searchView_desc)
            res.recommendSug.recommendSongs.addAll(res.hotSearchSug.songList)
            val sug=SearchSug()
            sug.musicSugList=res.recommendSug.recommendSongs
            sug.albumList=ArrayList()
            sug.artistList=res.hotSearchSug.artistSugList
            sug.songSheetList=res.hotSearchSug.playList
            setSearchData(sug)
        })

        viewModel.defSearch(System.currentTimeMillis())
    }

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window = window)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        loadData()

        val manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
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
            if(it==""){
                viewModel.defSearch(System.currentTimeMillis())
            }else{
                viewModel.getSearchSug(it)
            }

        }
        searchView_searchActivity.cancelCallback={
            finish()
        }

    }
    private fun setSearchData(res:SearchSug){
        var adapter = rv_searchSug.adapter
        if (adapter == null) {
            adapter = SearchSugAdapter(res)
            rv_searchSug.adapter = adapter
        } else {
            (adapter as SearchSugAdapter).searchSug = res
            adapter.notifyDataSetChanged()
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