package com.web.moudle.artist

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.BaseActivity
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.WindowUtil
import com.web.moudle.artist.adapter.ArtistListAdapter
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.artist.model.AllArtistViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_artist_type.*

class ArtistTypeActivity:BaseActivity() {
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
        topBar.setMainTitle(intent.getStringExtra(INTENT_DATA))
        vm=ViewModelProviders.of(this)[AllArtistViewModel::class.java]

        vm!!.artistList.observe(this, Observer {
            when(it.code){
                LiveDataWrapper.CODE_OK->{
                    artistList.addAll(it.value.artist)
                    adapter.notifyDataSetChanged()
                    page++
                }
                LiveDataWrapper.CODE_NO_MORE->{
                    artistList.addAll(it.value.artist)
                    srl_artist.setNoMoreData(true)
                }
                LiveDataWrapper.CODE_ERROR->{

                }
                LiveDataWrapper.CODE_NO_DATA->{

                }
            }
        })

        srl_artist.setRefreshFooter(ClassicsFooter(this))
        srl_artist.setOnLoadMoreListener {
            vm?.getHotArtist(areaCode,sexCode,page*pageSize,pageSize)
        }
        rv_artist.layoutManager=LinearLayoutManager(this)
        rv_artist.adapter=adapter


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