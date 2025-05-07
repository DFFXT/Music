package com.web.moudle.home.mainFragment.subFragment

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.music.m.R
import com.music.m.databinding.FragmentSongSheetBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.web.common.base.BaseFragment
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.util.ResUtil
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.moudle.home.mainFragment.subFragment.adapter.SongSheetInnerAdapter
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItem
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter

class SongSheetFragment : BaseFragment<FragmentSongSheetBinding>() {
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
            binding.srlSheetList.showContent()
            val tagIndex = getTagIndex(it.tag)
            stateList[tagIndex].page++
            stateList[tagIndex].data.addAll(it.songSheetItems)
            if (tagIndex == this.tagIndex) {
                binding.rvSheetList.adapter!!.notifyDataSetChanged()
                if (it.havemore == 1) {
                    stateList[tagIndex].haveMore = true
                    binding.srlSheetList.finishLoadMore()
                } else {
                    stateList[tagIndex].haveMore = false
                    binding.srlSheetList.setNoMoreData(true)
                }
            } else {
                stateList[tagIndex].haveMore = it.havemore == 1
            }
        })

        layoutManager = LinearLayoutManager(context)
        binding.rvSheetList.layoutManager = layoutManager
        binding.rvSheetList.adapter = adapter
        reloadState(stateList[tagIndex])

        binding.srlSheetList.setRefreshFooter(ClassicsFooter(context))
        binding.srlSheetList.setOnLoadMoreListener {
            vm.getSongSheetType(stateList[tagIndex].tag, stateList[tagIndex].page * pageSize, pageSize)
        }

        binding.rvSheetType.layoutManager = LinearLayoutManager(context)
        val typeAdapter = SingleTextAdapter(stateList.map { it.tag })
        typeAdapter.selectIndex = 0
        binding.rvSheetType.adapter = typeAdapter
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
        binding.srlSheetList.showLoading()
        vm.getSongSheetType(stateList[index].tag, stateList[index].page, pageSize)
    }

    private fun reloadState(state: RecyclerViewState) {
        binding.srlSheetList.setNoMoreData(!state.haveMore)
        adapter.update(state.data)
        (binding.rvSheetList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(state.position, state.offset)
    }

    private fun getTagIndex(tag: String): Int {
        for (i in stateList.indices) {
            if (tag == stateList[i].tag) {
                return i
            }
        }
        return -1
    }

    private class RecyclerViewState(var tag: String) {
        var position: Int = 0
        var offset = 0
        var data = ArrayList<SongSheetItem>()
        var page = -1
        var haveMore = true
    }
}