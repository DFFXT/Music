package com.web.moudle.artist

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseActivity
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.GapItemDecoration
import com.web.moudle.artist.adapter.ArtistTypeAdapter
import com.web.moudle.artist.adapter.HotArtistAdapter
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.artist.model.AllArtistViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_all_artist.*

class AllArtistActivity:BaseActivity() {
    private var vm:AllArtistViewModel?=null

    private val adapter:HotArtistAdapter=HotArtistAdapter()

    override fun getLayoutId(): Int = R.layout.activity_all_artist

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        vm=ViewModelProviders.of(this)[AllArtistViewModel::class.java]
        vm!!.artistList.observe(this, Observer {
            adapter.update(it.value.artist)
        })

        rv_hotArtist.layoutManager=LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val gap=ViewUtil.dpToPx(10f)
        rv_hotArtist.addItemDecoration(GapItemDecoration(left = gap,right = gap,remianLeftPadding = true,remainEndPadding = true))
        rv_hotArtist.adapter=adapter

        val list=ArrayList<ArtistInfo>()
        for(i in 0..5){
            list.add(ArtistInfo())
        }
        adapter.update(list)


        rv_artistType.layoutManager=LinearLayoutManager(this)

        rv_artistType.addItemDecoration(DrawableItemDecoration(bottom = 2,drawable = getDrawable(R.drawable.recycler_divider)))


        rv_artistType.adapter=ArtistTypeAdapter(vm!!.getArtistTypeList())



        vm!!.getHotArtist(0,0,0,48)
    }


    companion object{
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,AllArtistActivity::class.java))
        }
    }
}