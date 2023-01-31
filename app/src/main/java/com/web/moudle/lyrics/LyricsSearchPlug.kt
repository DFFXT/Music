package com.web.moudle.lyrics

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.get
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.tool.MToast
import com.web.common.util.IOUtil
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.data.Music
import com.web.misc.BasePopupWindow
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.PlayerConnection
import com.web.moudle.musicSearch.bean.next.SearchMusicWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.musicSearch.model.InternetMusicModel
import com.web.moudle.net.retrofit.SchedulerTransform
import com.music.m.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_pop_lyris_plug.view.*
import kotlinx.coroutines.*
import java.lang.Exception

class LyricsSearchPlug(ctx:FragmentActivity,connect: PlayerConnection):BasePopupWindow(ctx,
        LayoutInflater.from(ctx).inflate(R.layout.layout_pop_lyris_plug,null,false),
        (ViewUtil.screenWidth()*0.8f).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
        ){
    private var music:Music?=null
    private var musicNameKeyword=""
    private var artistNameKeyword=""
    private val model=InternetMusicModel()
    private val list=ArrayList<String>()
    private val rowList=ArrayList<SimpleMusicInfo>()
    private val adapter=SingleTextAdapter(list)
    init {
        enableWindowDark(false)
        setBackground(ColorDrawable(ResUtil.getColor(R.color.gray)))

        adapter.selectRender={holder,_->
            holder.itemView.setBackgroundColor(ResUtil.getColor(R.color.gray))
            (holder.itemView as TextView).setTextColor(ResUtil.getColor(R.color.themeColor))
        }
        adapter.commonRender={holder,_->
            holder.itemView.setBackgroundColor(0)
            (holder.itemView as TextView).setTextColor(ResUtil.getColor(R.color.textColor_3))
        }

        rootView.rv_lyrics.layoutManager=LinearLayoutManager(ctx)
        rootView.rv_lyrics.addItemDecoration(DrawableItemDecoration(0,0,0,2,
                RecyclerView.VERTICAL,ResUtil.getDrawable(R.drawable.recycler_divider)))
        rootView.rv_lyrics.adapter=adapter

        rootView.iv_search.setOnClickListener {
            musicNameKeyword=rootView.et_musicName.text.toString()
            artistNameKeyword=rootView.et_artist.text.toString()
            if(musicNameKeyword==""){
                return@setOnClickListener
            }
            rootView.rv_lyrics.showLoading()
            response(model.getSimpleMusic(musicNameKeyword,0))
        }
        rootView.button_cancel.setOnClickListener {
            dismiss()
        }
        rootView.button_confirm.setOnClickListener {
            if(adapter.selectIndex<0)return@setOnClickListener

            val job=GlobalScope.async(Dispatchers.IO) {
                IOUtil.onlineDataToLocal(rowList[adapter.selectIndex].lrcLink,music!!.lyricsPath)
            }
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    job.await()
                    connect.getPlayerInfo(null)
                }catch (e:Exception){
                    e.printStackTrace()
                    MToast.showToast(ctx,R.string.downloadFailed)
                }finally {
                    dismiss()
                }
            }


        }
    }

    private fun response(observable:Observable<SearchMusicWrapper1>){
        observable
                .compose(SchedulerTransform())
                .get(
                onNext = {res->
                    list.clear()
                    rowList.clear()
                    res.searchSongWrapper2.songList?.forEach {
                        if(it.lrcLink==""||artistNameKeyword!=""&&!it.author.contains(artistNameKeyword))return@forEach
                        list.add(it.musicName+" - "+it.author)
                        rowList.add(it)
                    }
                    adapter.reset()
                    adapter.notifyDataSetChanged()
                    if(list.size!=0){
                        rootView.rv_lyrics.showContent()
                    }else{
                        val drawable=ResUtil.getDrawable(R.drawable.icon_waring_white)
                        drawable.setTint(ResUtil.getColor(R.color.gray))
                        rootView.rv_lyrics.showError(ResUtil.getString(R.string.noData), drawable)
                    }

                },
                onError = {
                    it.printStackTrace()
                    rootView.rv_lyrics.showError()
                }
        )
    }


    fun showCenter(view: View,music:Music){
        if(this.music!=music){
            list.clear()
            this.music=music
            musicNameKeyword=music.musicName
            artistNameKeyword=music.singer
            rootView.et_musicName.setText(music.musicName)
            rootView.et_artist.setText(music.singer)
            view.post {
                rootView.rv_lyrics.showLoading()
            }

            response(model.getSimpleMusic(music.musicName,0))
        }
        showCenter(view)
    }

}