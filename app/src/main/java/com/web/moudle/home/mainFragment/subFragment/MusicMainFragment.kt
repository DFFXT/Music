package com.web.moudle.home.mainFragment.subFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
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
import com.web.moudle.singerEntry.ui.SingerEntryActivityNew
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_song_sheet.*
import kotlinx.android.synthetic.main.fragment_song_sheet.view.*
import kotlinx.android.synthetic.main.layout_music_tag.view.*

class MusicMainFragment : BaseFragment() {
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
    private var page = 0


    override fun initView(rootView: View) {

        vm = ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        vm.musicTagList.observe(this, Observer { tags ->
            tagData = tags
            rv_sheetType.layoutManager = LinearLayoutManager(context)
            tagAdapter.update(tags.tags)
            rv_sheetType.adapter = tagAdapter
            rootView.srl_sheetList.showLoading()
            currentTag = tags.tagMap[tags.tags[0]]!![0].title
            vm.getTagMusic(currentTag, 0, pageSize)
        })

        vm.tagMusicList.observe(this, Observer {
            if (currentTag != it.tag) return@Observer
            rootView.srl_sheetList.showContent()
            rootView.srl_sheetList.finishLoadMore()

            mainData.addAll(it.taginfo.songlist)
            adapter.update(mainData)
            rootView.srl_sheetList.setNoMoreData(it.taginfo.havemore == 0)
        })


        layoutManager = LinearLayoutManager(context)
        rootView.rv_SheetList.layoutManager = layoutManager
        rootView.rv_SheetList.addItemDecoration(DrawableItemDecoration(0, 0, 0, 2, drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))
        rootView.rv_SheetList.adapter = adapter
        adapter.itemClick={item,_->
            MusicDetailActivity.actionStart(context!!,item.song_id)
        }

        rootView.srl_sheetList.setRefreshFooter(ClassicsFooter(context))
        rootView.srl_sheetList.setOnLoadMoreListener {
            vm.getTagMusic(currentTag, page * pageSize, pageSize)
        }



        rootView.rv_sheetType.adapter = tagAdapter
        tagAdapter.itemClickListener = { v, index ->
            showPopTag(v!!, tagData.tagMap[tagData.tags[index]]!!)
        }


        vm.getMusicTag()

    }

    private var popWindow: BasePopupWindow? = null
    private val tabViewList = ArrayList<TextView>()
    private fun showPopTag(v: View, tags: List<MusicTag>) {
        if (popWindow == null) {
            popWindow = BasePopupWindow(context!!, LayoutInflater.from(context).inflate(R.layout.layout_music_tag, null, false))
        }
        val layout = popWindow!!.rootView.flexBoxLayout_tag as ViewGroup
        val childCount = layout.childCount

        layout.removeAllViews()

        for (i in childCount until tags.size) {
            createTagView(tags[i].title)
        }
        tags.forEachIndexed { index, it ->
            tabViewList[index].text = it.title
            layout.addView(tabViewList[index])
        }
        popWindow?.show(v)
    }

    private val padding = ViewUtil.dpToPx(6f)
    private fun createTagView(tag: String): TextView {
        val tab = TextView(context)
        tab.text = tag
        tab.elevation = ViewUtil.dpToFloatPx(1f)
        tab.setBackgroundResource(R.drawable.border_1dp)
        tab.setPadding(padding * 2, padding / 2, padding * 2, padding / 2)
        val lp = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(padding, padding, padding, padding)
        tab.layoutParams = lp
        tab.setOnClickListener {
            it as TextView
            page = 0
            mainData.clear()
            currentTag = it.text.toString()
            rootView?.srl_sheetList?.showLoading()
            vm.getTagMusic(currentTag, 0, pageSize)
            popWindow?.dismiss()
        }
        tabViewList.add(tab)
        return tab
    }


}