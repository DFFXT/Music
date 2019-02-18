package com.web.moudle.billboradDetail

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.web.common.base.*
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.billboradDetail.adapter.NetMusicListAdapter
import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.billboradDetail.model.NetMusicListViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_net_music_list.*

class NetMusicListActivity:BaseActivity() {
    override fun getLayoutId(): Int =R.layout.activity_net_music_list
    private var type:Int=0
    private lateinit var title:String

    private lateinit var model:NetMusicListViewModel


    @SuppressLint("SetTextI18n")
    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        type=intent.getIntExtra(LIST_TYPE,-1)
        title=intent.getStringExtra(INTENT_DATA)
        model=ViewModelProviders.of(this)[NetMusicListViewModel::class.java]
        model.netMusicList.observe(this,Observer<NetMusicBox>{
            if(it==null){
                rootView.showError()
                return@Observer
            }

            tv_title.text=title
            tv_updateTime.text=ResUtil.getString(R.string.updateTime,it.billboardInfo.update_date)
            if(it.billboardInfo.billboard_songnum.toInt()>100){
                it.billboardInfo.billboard_songnum="100"
            }
            tv_musicCount.text="共${it.billboardInfo.billboard_songnum}首"
            rv_netMusicList.adapter=NetMusicListAdapter(it.list)
            collapseToolbarLayout.setBackgroundColor(Color.parseColor(it.billboardInfo.bg_color.replace("0x","#")))
            val textColor=Color.parseColor(it.billboardInfo.color.replace("0x","#"))
            tv_title.setTextColor(textColor)
            tv_updateTime.setTextColor(textColor)
            tv_musicCount.setTextColor(textColor)
            topBar.setTint(textColor)
            topBar.setMainTitleColor(textColor)
            rootView.showContent()
        })
        rv_netMusicList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_netMusicList.addItemDecoration(DrawableItemDecoration(left = 10,top = 10,right = 10,bottom = 10,
                orientation =  LinearLayout.VERTICAL,drawable = getDrawable(R.drawable.recycler_divider)))
        if(type==-1){
            model.requestRecommend()
        }else{
            model.requestList(type)
        }


        appBarLayout.addOnOffsetChangedListener(object :BaseAppBarLayoutOffsetChangeListener(){
            override fun offsetChanged(state: Int, position: Int) {
                when(state){
                    BaseAppBarLayoutOffsetChangeListener.STATE_COLLAPSE->{
                        topBar.setMainTitle(title)
                                .alpha=1f
                    }
                    BaseAppBarLayoutOffsetChangeListener.STATE_EXPANDING->{
                        var alpha=1-(appBarLayout.totalScrollRange-position)/appBarLayout.totalScrollRange.toFloat()
                        if(alpha<0.5f) alpha=0f
                        topBar.setMainTitle(title)
                                .alpha=alpha*0.5f
                    }
                }
            }

        })
        rootView.showLoading(true)
    }



    companion object {
        const val LIST_TYPE="type"
        @JvmStatic
        fun actionStart(ctx:Context,title:String,type:Int){
            val intent= Intent(ctx,NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA,title)
            intent.putExtra(LIST_TYPE,type)
            ctx.startActivity(intent)
        }
        @JvmStatic
        fun actionStart(ctx: Context,title:String){
            val intent= Intent(ctx,NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA,title)
            ctx.startActivity(intent)
        }
    }

}
enum class NetMusicType constructor(type:Int){
    TYPE_HOT(1),
    TYPE_NEW(1),
    TYPE_NETWORK(1),
    TYPE_ORIGINAL(1),
    TYPE_U(1),
    TYPE_VIDEO(1),
    TYPE_CLASSIC(1),
    TYPE_FOREIGN(1),
}