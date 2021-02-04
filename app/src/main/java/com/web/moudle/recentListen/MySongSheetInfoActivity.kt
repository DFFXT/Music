package com.web.moudle.recentListen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseActivity
import com.web.common.base.get
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.RecentPlayMusic
import com.web.misc.ConfirmDialog
import com.web.misc.ToolsBar
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.music.player.bean.MusicWW
import com.web.moudle.music.player.model.WWSongSheetModel
import com.web.moudle.musicDownload.adpter.DownloadViewAdapter
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.moudle.musicEntry.model.DetailMusicViewModel
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.web.R
import kotlinx.android.synthetic.main.activity_recent_listen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.crud.DataSupport

class MySongSheetInfoActivity :BaseActivity(){


    private lateinit var adapter:DownloadViewAdapter
    private lateinit var list:List<DownloadMusic>
    private var sheetId: Long=0
    private var model:DetailMusicViewModel?=null
    private var toolsBar : ToolsBar?=null

    override fun getLayoutId(): Int = R.layout.activity_recent_listen

    private var connect:MusicPlay.Connect?=null

    private val connection=object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            connect= service as MusicPlay.Connect
        }
    }

    override fun initView() {
        sheetId=intent.getLongExtra(INTENT_DATA,0)
        model=ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
        model!!.detailMusic.observe(this, Observer {
            if(it.code==LiveDataWrapper.CODE_OK){
                connect?.play(MusicDetailActivity.map(it.value))
            }
        })
        topBar.setEndImageListener {
            connect?.addListToWait(list.map {
                map(it.internetMusicDetail)
            }, false)
        }
        adapter= DownloadViewAdapter(this,null)
        rv_recentList.layoutManager=LinearLayoutManager(this)
        rv_recentList.adapter=adapter


        adapter.setItemClickListener { v, position ->
            if(v.id==R.id.iv_play){
                model?.getDetail(list[position].internetMusicDetail.songId)
            }else{
                MusicDetailActivity.actionStart(this,list[position].internetMusicDetail.songId)
            }

        }
        adapter.setItemLongClickListener { _, _ ->
            initToolsBar()
            toolsBar?.show()
            true
        }
        rootView.showLoading(true)
        reload()
        val intent=Intent(this,MusicPlay::class.java)
        intent.action=MusicPlay.BIND
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
    }
    private fun reload(){

        if(sheetId!=-1L){
            WWSongSheetModel.getSongSheetInfo(sheetId){
                if(it.code==200){
                    topBar.setMainTitle(it.name)
                    list=it.songs.map {ww->
                        val de= map(ww)
                        DownloadMusic(de,DownloadMusic.DOWNLOAD_COMPLETE)
                    }
                    adapter.update(list)
                    rootView.showContent()
                }
            }
        }else{//**喜爱歌曲
            WWSongSheetModel.getLikeList {
                if(it.code==200){
                    topBar.setMainTitle(ResUtil.getString(R.string.sheet_like))
                    findLikeList(it.ids)
                }
            }

        }
    }

    private fun findLikeList(ids:List<Long>){
        var idStr=""
        ids.forEach {
            idStr+= ",$it"
        }
        if(idStr.length>1){
            idStr=idStr.substring(1)
        }
        BaseRetrofit().obtainClass(NetApis.Music::class.java)
                .musicInfo(idStr)
                .compose(SchedulerTransform())
                .get({res->
                    list=res.data.songList.map {
                        DownloadMusic(it,DownloadMusic.DOWNLOAD_COMPLETE)
                    }
                    adapter.update(list)
                    rootView.showContent()
                })
    }



    private fun initToolsBar(){
        if(toolsBar==null){
            toolsBar= ToolsBar(this)
            toolsBar!!.addItem(1,R.string.delete)
            toolsBar!!.addItem(2,R.string.selectAll)
            toolsBar!!.itemClick={id->
                if(id==1){
                    deleteRecords()
                    toolsBar?.close()
                }else if(id==2){
                    adapter.isSelectAll=!adapter.isSelectAll
                }
            }
            toolsBar!!.backClick={
                adapter.isSelect=false
            }
        }
    }

    private var dialog:ConfirmDialog?=null
    private fun confirmDelete(){
        if(dialog==null){
            dialog=ConfirmDialog(this)
                    .setMsg(ResUtil.getString(R.string.weatherDeleteAllRecord))
                    .setLeftText(ResUtil.getString(R.string.no))
                    .setRightText(ResUtil.getString(R.string.yes))
                    .setLeftListener {
                        it.dismiss()
                    }
                    .setRightListener {
                        deleteAll()
                        it.dismiss()
                    }
        }
        dialog?.showCenter(topBar)

    }

    private fun deleteRecords(){
        val list=adapter.getSelectList { downloadMusic, _ ->  downloadMusic.internetMusicDetail.songId}
        if(list.isNotEmpty()){
            if(sheetId==-1L){//**喜爱歌单删除
                WWSongSheetModel.removeAsLike(list[0].toLong()){
                    if(it){
                        reload()
                    }
                }
            }else{
                WWSongSheetModel.deleteFromSheet(sheetId,list[0].toLong()){
                    if(it.code==200){
                        reload()
                    }
                }
            }

        }

    }
    private fun deleteAll(){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                DataSupport.deleteAll(RecentPlayMusic::class.java)
            }
            reload()
        }
    }

    private fun map(item:InternetMusicDetail):InternetMusicForPlay{
        val res= InternetMusicForPlay(item.songName,item.artistName,item.songLink)

        res.imgAddress=item.singerIconSmall
        res.lrcLink=item.lrcLink
        res.song_id=item.songId
        res.album=item.albumName
        res.album_id=item.albumId
        res.duration=item.duration

        return res
    }



    private fun map(item:MusicWW):InternetMusicDetail{
        return InternetMusicDetail(
                songId = item.songId.toString(),
                albumId = "",
                albumName = item.album,
                artistName = item.artist,
                duration = 0,
                format = "",
                lrcLink = "",
                singerIconSmall = "",
                size = 0,
                songLink = "",
                songName = item.name
        )
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    companion object{
        @JvmStatic
        fun actionStart(ctx:Context,sheetId:Long){
            val intent=Intent(ctx,MySongSheetInfoActivity::class.java)
            intent.putExtra(INTENT_DATA,sheetId)
            ctx.startActivity(intent)
        }
    }
}