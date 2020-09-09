package com.web.moudle.home.mainFragment.subFragment

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseFragment
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.GapItemDecoration
import com.web.moudle.artist.adapter.ArtistTypeAdapter
import com.web.moudle.artist.adapter.HotArtistAdapter
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.artist.model.AllArtistViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_all_artist.view.*

class AllArtistFragment:BaseFragment() {
    override var title=ResUtil.getString(R.string.singer_tab)
    private lateinit var vm:AllArtistViewModel

    private val adapter:HotArtistAdapter=HotArtistAdapter()

    override fun getLayoutId(): Int = R.layout.activity_all_artist

    override fun initView(rootView:View) {
        vm=ViewModelProviders.of(this)[AllArtistViewModel::class.java]
        vm.artistList.observe(this, Observer {
            adapter.update(it.value?.artist)
        })

        rootView.rv_hotArtist.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        val gap=ViewUtil.dpToPx(10f)
        rootView.rv_hotArtist.addItemDecoration(GapItemDecoration(left = gap,right = gap,remainLeftPadding = true,remainEndPadding = true))
        rootView.rv_hotArtist.adapter=adapter

        val list=ArrayList<ArtistInfo>()
        for(i in 0..5){
            list.add(ArtistInfo())
        }
        adapter.update(list)


        rootView.rv_artistType.layoutManager=LinearLayoutManager(context)

        rootView.rv_artistType.addItemDecoration(DrawableItemDecoration(bottom = 2,drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))


        rootView.rv_artistType.adapter=ArtistTypeAdapter(vm.getArtistTypeList())



        vm.getHotArtist(0,0,0,48)
    }

}