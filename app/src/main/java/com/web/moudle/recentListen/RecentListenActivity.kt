package com.web.moudle.recentListen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseActivity
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.RecentPlayMusic
import com.web.misc.ConfirmDialog
import com.web.misc.ToolsBar
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.musicDownload.adpter.DownloadViewAdapter
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.music.m.R
import kotlinx.android.synthetic.main.activity_recent_listen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.crud.DataSupport

class RecentListenActivity :BaseActivity(){


    private lateinit var adapter:DownloadViewAdapter
    private lateinit var list:List<DownloadMusic>
    private var toolsBar : ToolsBar?=null

    override fun getLayoutId(): Int = R.layout.activity_recent_listen

    private var connect: IMusicControl ?=null

    private val connection=object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            connect= service as? IMusicControl
        }
    }

    override fun initView() {

        topBar.setEndImageListener(View.OnClickListener {
            /*connect?.addListToWait(list.map {
                map(it.internetMusicDetail)
            }, false)*/
            MToast.showToast(this,"不支持？")
        })
        adapter= DownloadViewAdapter(this,null)
        rv_recentList.layoutManager=LinearLayoutManager(this)
        rv_recentList.adapter=adapter


        adapter.setItemClickListener { v, position ->
            if(v.id==R.id.iv_play){
                ActionControlPlug.play(this,map(list[position].internetMusicDetail))
            }else{
                MusicDetailActivity.actionStart(this,list[position].internetMusicDetail.songId)
            }

        }
        adapter.setItemLongClickListener { _, _ ->
            initToolsBar()
            toolsBar?.show()
            true
        }

        reload()
        val intent=Intent(this, NewPlayer::class.java)
        intent.action= ActionControlPlug.BIND
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
    }
    private fun reload(){
        GlobalScope.launch(Dispatchers.IO) {
            val mList=DataSupport.findAll<RecentPlayMusic>(RecentPlayMusic::class.java)
            list=mList.map {
                DownloadMusic(map(it),DownloadMusic.DOWNLOAD_COMPLETE)
            }
            withContext(Dispatchers.Main){
                adapter.update(list)
            }
        }
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
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                val delList=adapter.getSelectList { downloadMusic, _ ->  downloadMusic.internetMusicDetail.songId}
                delList.forEach {
                    DataSupport.deleteAll(RecentPlayMusic::class.java,"songId=?",it)
                }
            }
            reload()
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

    private fun map(item:RecentPlayMusic):InternetMusicDetail{
        return InternetMusicDetail(
                songId = item.songId,
                albumId = item.albumId,
                albumName = item.albumName,
                artistName = item.artist,
                duration = item.duration,
                format = "",
                lrcLink = item.lrcLink,
                singerIconSmall = item.imageLink,
                size = 0,
                songLink = item.path,
                songName = item.musicName
        )
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    companion object{
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,RecentListenActivity::class.java))
        }
    }
}