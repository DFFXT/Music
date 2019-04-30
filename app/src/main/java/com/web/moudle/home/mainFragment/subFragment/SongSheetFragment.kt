package com.web.moudle.home.mainFragment.subFragment

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.BaseFragment
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.util.ResUtil
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.moudle.home.mainFragment.subFragment.adapter.SongSheetInnerAdapter
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItem
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_song_sheet.view.*

class SongSheetFragment : BaseFragment() {
    override var title = ResUtil.getString(R.string.songSheet)
    private val pageSize = 20
    private lateinit var vm: MainFragmentViewModel
    override fun getLayoutId(): Int = R.layout.fragment_song_sheet


    private val stateList = ArrayList<RecyclerViewState>()
    private var tagIndex = 0
    private lateinit var layoutManager: LinearLayoutManager
    private val adapter = SongSheetInnerAdapter()

    init {
        ResUtil.getStringArray(R.array.songSheetStyle)
                .forEach {
                    stateList.add(RecyclerViewState(it))
                }
    }

    override fun initView(rootView: View) {

        vm = ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        vm.songSheetList.observe(this, Observer {
            rootView.srl_sheetList.showContent()
            val tagIndex = getTagIndex(it.tag)
            stateList[tagIndex].page++
            stateList[tagIndex].data.addAll(it.songSheetItems)
            if (tagIndex == this.tagIndex) {//**返回的数据是当前页面的数据
                rootView.rv_SheetList.adapter!!.notifyDataSetChanged()
                if (it.havemore == 1) {
                    stateList[tagIndex].haveMore = true
                    rootView.srl_sheetList.finishLoadMore()
                } else {
                    stateList[tagIndex].haveMore = false
                    rootView.srl_sheetList.setNoMoreData(true)
                }
            } else {//**返回的数据不是当前页的数据
                stateList[tagIndex].haveMore = it.havemore == 1
            }

        })
        //**设置主要显示RecyclerView
        layoutManager = LinearLayoutManager(context)
        rootView.rv_SheetList.layoutManager = layoutManager
        rootView.rv_SheetList.adapter = adapter
        reloadState(stateList[tagIndex])

        rootView.srl_sheetList.setRefreshFooter(ClassicsFooter(context))
        rootView.srl_sheetList.setOnLoadMoreListener {
            vm.getSongSheetType(stateList[tagIndex].tag, stateList[tagIndex].page * pageSize, pageSize)
        }


        //**设置类型RecyclerView
        rootView.rv_sheetType.layoutManager = LinearLayoutManager(context)
        val typeAdapter = SingleTextAdapter(stateList.map { it.tag })
        typeAdapter.selectIndex = 0
        rootView.rv_sheetType.adapter = typeAdapter
        typeAdapter.selectRender = { h, _ ->
            (h.itemView as TextView).setTextColor(ResUtil.getColor(R.color.themeColor))
            h.itemView.setBackgroundColor(ResUtil.getColor(R.color.gray))
        }
        typeAdapter.commonRender = { h, _ ->
            (h.itemView as TextView).setTextColor(ResUtil.getColor(R.color.textColor_6))
            h.itemView.setBackgroundColor(0)
        }
        typeAdapter.itemClickListener = { _, it ->
            val currentState = stateList[tagIndex]
            tagIndex = it
            currentState.position = layoutManager.findFirstVisibleItemPosition()
            currentState.offset = layoutManager.getChildAt(0)?.top ?: 0
            reloadState(stateList[it])

            if (stateList[tagIndex].page < 0) {
                initLoad(tagIndex)
            }
        }
        initLoad(0)
    }

    private fun initLoad(index: Int) {
        stateList[index].page = 0
        rootView!!.srl_sheetList.showLoading()
        vm.getSongSheetType(stateList[index].tag, stateList[index].page, pageSize)
    }

    private fun reloadState(state: RecyclerViewState) {
        rootView!!.srl_sheetList.setNoMoreData(!state.haveMore)
        adapter.update(state.data)
        (rootView!!.rv_SheetList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(state.position, state.offset)
    }

    private fun getTagIndex(tag: String): Int {
        for (i in stateList.indices) {
            if (tag == stateList[i].tag) {
                return i
            }
        }
        return -1
    }


    /**
     * 保存RecyclerView状态的类
     */
    private class RecyclerViewState(var tag: String) {
        var position: Int = 0
        var offset = 0
        var data = ArrayList<SongSheetItem>()
        var page = -1
        var haveMore = true
    }
}