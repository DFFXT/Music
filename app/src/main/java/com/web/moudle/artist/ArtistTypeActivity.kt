package com.web.moudle.artist

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.music.m.R
import com.music.m.databinding.ActivityArtistTypeBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.web.common.base.BaseActivity2
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.WindowUtil
import com.web.moudle.artist.adapter.ArtistListAdapter
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.artist.model.AllArtistViewModel

class ArtistTypeActivity:BaseActivity2<ActivityArtistTypeBinding>() {
    private var vm:AllArtistViewModel?=null
    override fun getLayoutId(): Int = R.layout.activity_artist_type

    private var areaCode=0
    private var sexCode=0

    private var page=0
    private val pageSize=30
    private val artistList=ArrayList<ArtistInfo>()
    private val adapter=ArtistListAdapter(artistList)

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        areaCode=intent.getIntExtra(CODE_AREA,0)
        sexCode=intent.getIntExtra(CODE_SEX,0)
        binding.topBar.setMainTitle(intent.getStringExtra(INTENT_DATA)!!)
        vm=ViewModelProviders.of(this)[AllArtistViewModel::class.java]

        vm!!.artistList.observe(this, Observer {
            binding.srlArtist
            binding.srlArtist.finishLoadMore()
            when(it.code){
                LiveDataWrapper.CODE_OK->{
                    artistList.addAll(it.value.artist)
                    adapter.notifyDataSetChanged()
                    page++
                }
                LiveDataWrapper.CODE_NO_MORE->{
                    artistList.addAll(it.value.artist)
                    binding.srlArtist.setNoMoreData(true)
                }
                LiveDataWrapper.CODE_ERROR->{

                }
                LiveDataWrapper.CODE_NO_DATA->{

                }
            }
        })

        binding.srlArtist.setRefreshFooter(ClassicsFooter(this))
        binding.srlArtist.setOnLoadMoreListener {
            vm?.getHotArtist(areaCode,sexCode,page*pageSize,pageSize)
        }
        binding.rvArtist.layoutManager=LinearLayoutManager(this)
        binding.rvArtist.adapter=adapter


        vm?.getHotArtist(areaCode,sexCode,page,pageSize)

    }

    companion object{
        private const val CODE_AREA="areaCode"
        private const val CODE_SEX="sexCode"
        @JvmStatic
        fun actionStart(ctx:Context,typeName:String,areaCode:Int,sexCode:Int){
            val intent= Intent(ctx,ArtistTypeActivity::class.java)
            intent.putExtra(INTENT_DATA,typeName)
            intent.putExtra(CODE_AREA,areaCode)
            intent.putExtra(CODE_SEX,sexCode)
            ctx.startActivity(intent)
        }
    }
}