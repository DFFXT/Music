package com.web.moudle.home.mainFragment.subFragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.music.m.R
import com.music.m.databinding.FragmentSongSheetBinding
import com.music.m.databinding.LayoutMusicTagBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.web.common.base.BaseFragment
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.misc.DrawableItemDecoration
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.moudle.home.mainFragment.subFragment.adapter.HomePageMusicAdapter
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusic
import com.web.moudle.home.mainFragment.subFragment.bean.MusicTag
import com.web.moudle.home.mainFragment.subFragment.bean.MusicTagBox
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.moudle.musicEntry.ui.MusicDetailActivity

class MusicMainFragment : BaseFragment<FragmentSongSheetBinding>() {
    override var title = ResUtil.getString(R.string.music)
    private val pageSize = 20
    private lateinit var vm: MainFragmentViewModel
    override fun getLayoutId(): Int = R.layout.fragment_song_sheet


    private lateinit var layoutManager: LinearLayoutManager
    private val mainData = ArrayList<HomePageMusic>()
    private val adapter = HomePageMusicAdapter()
    private val tagAdapter = SingleTextAdapter(null)
    private lateinit var tagData: MusicTagBox
    private var currentTag = ""
    private var currentCategoryIndex = 0//当前标签分类index
    private var nextCategoryIndex = 0//点击的标签分类index
    private var page = 0


    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        vm.musicTagList.observe(this, Observer { tags ->
            tagData = tags
            binding.rvSheetType.layoutManager = LinearLayoutManager(context)
            tagAdapter.update(tags.tags)
            binding.rvSheetType.adapter = tagAdapter
            binding.srlSheetList.showLoading()
            currentTag = tags.tagMap[tags.tags[0]]!![0].title
            vm.getTagMusic(currentTag, 0, pageSize)
        })

        vm.tagMusicList.observe(this, Observer {
            if (currentTag != it.tag) return@Observer
            binding.srlSheetList.showContent()
            binding.srlSheetList.finishLoadMore()

            mainData.addAll(it.taginfo.songlist)
            adapter.update(mainData)
            binding.srlSheetList.setNoMoreData(it.taginfo.havemore == 0)
        })

        tagAdapter.selectIndex = currentCategoryIndex
        tagAdapter.selectRender = { h, _ ->
            (h.itemView as TextView).setTextColor(ResUtil.getColor(R.color.themeColor))
            h.itemView.setBackgroundColor(ResUtil.getColor(R.color.gray))
        }
        tagAdapter.commonRender = { h, _ ->
            (h.itemView as TextView).setTextColor(ResUtil.getColor(R.color.textColor_6))
            h.itemView.setBackgroundColor(0)
        }

        layoutManager = LinearLayoutManager(context)
        binding.rvSheetList.layoutManager = layoutManager
        binding.rvSheetList.addItemDecoration(DrawableItemDecoration(0, 0, 0, 2, drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))
        binding.rvSheetList.adapter = adapter
        adapter.itemClick = { item, _ ->
            MusicDetailActivity.actionStart(requireContext(), item.song_id)
        }

        binding.srlSheetList.setRefreshFooter(ClassicsFooter(context))
        binding.srlSheetList.setOnLoadMoreListener {
            vm.getTagMusic(currentTag, page * pageSize, pageSize)
        }

        binding.rvSheetType.adapter = tagAdapter
        tagAdapter.itemClickListener = { v, index ->
            nextCategoryIndex = index
            showPopTag(v!!, tagData.tagMap[tagData.tags[index]]!!)
        }

        vm.getMusicTag()
    }

    private var popWindow: BasePopupWindow<LayoutMusicTagBinding>? = null
    private val tabViewList = ArrayList<TextView>()
    private fun showPopTag(v: View, tags: List<MusicTag>) {
        if (popWindow == null) {
            popWindow = BasePopupWindow(requireContext(), LayoutInflater.from(context).inflate(R.layout.layout_music_tag, null, false))
            popWindow?.dismissCallback = {
                tagAdapter.selectIndex = currentCategoryIndex
                tagAdapter.notifyItemChanged(currentCategoryIndex)
                tagAdapter.notifyItemChanged(nextCategoryIndex)
            }
        }
        val layout = popWindow!!.binding.flexBoxLayoutTag as ViewGroup
        val childCount = layout.childCount

        layout.removeAllViews()

        for (i in childCount until tags.size) {
            createTagView(tags[i].title)
        }
        tags.forEachIndexed { index, it ->
            tabViewList[index].text = it.title
            if (currentTag == tags[index].title) {
                tabViewList[index].setBackgroundColor(ResUtil.getColor(R.color.themeColor))
                tabViewList[index].setTextColor(Color.WHITE)
            } else {
                tabViewList[index].setBackgroundResource(R.drawable.border_1dp)
                tabViewList[index].setTextColor(ResUtil.getColor(R.color.textColor_6))
            }
            layout.addView(tabViewList[index])
        }
        popWindow?.show(v)
    }

    private val padding = ViewUtil.dpToPx(6f)
    private fun createTagView(tag: String): TextView {
        val tab = TextView(context)
        tab.text = tag
        tab.elevation = ViewUtil.dpToFloatPx(1f)
        tab.setPadding(padding * 2, padding / 2, padding * 2, padding / 2)
        val lp = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(padding, padding, padding, padding)
        tab.layoutParams = lp
        tab.setOnClickListener {
            it as TextView
            page = 0
            mainData.clear()
            currentTag = it.text.toString()
            binding.srlSheetList.showLoading()
            vm.getTagMusic(currentTag, 0, pageSize)
            currentCategoryIndex = nextCategoryIndex
            popWindow?.dismiss()
        }
        tabViewList.add(tab)
        return tab
    }


}