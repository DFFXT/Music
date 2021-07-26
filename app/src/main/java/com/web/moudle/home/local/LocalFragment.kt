package com.web.moudle.home.local

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.web.common.base.BaseFragment
import com.web.common.base.PlayerObserver
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.data.Music
import com.web.misc.GapItemDecoration
import com.web.misc.InputDialog
import com.web.moudle.home.HomePageActivity
import com.web.moudle.home.local.adapter.SheetAdapter
import com.web.moudle.home.local.model.LocalModel
import com.web.moudle.login.LoginActivity
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.bean.SongSheetWW
import com.web.moudle.music.player.model.WWSongSheetModel
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.musicDownload.ui.MusicDownLoadActivity
import com.web.moudle.recentListen.MySongSheetInfoActivity
import com.web.moudle.recentListen.RecentListenActivity
import com.web.moudle.search.SearchActivity
import com.web.moudle.setting.ui.SettingActivity
import com.web.moudle.user.UserManager
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_local.view.*

class LocalFragment : BaseFragment() {
    private val model = LocalModel()
    private var createSheetPop:InputDialog?=null
    private var listPop:ListDialog?=null
    private val sheetList=ArrayList<SongSheetWW>()
    private val adapter=SheetAdapter()
    override fun getLayoutId(): Int = R.layout.fragment_local
    private var connect: IMusicControl?=null
    private var observer=object :PlayerObserver(){
        override fun onMusicListChange(list: MutableList<Music>?) {
            model.getMusicNum {
                rootView!!.tv_musicNum?.text = it.toString()
            }
        }
    }
    private val connection=object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            connect=service as IMusicControl
            connect?.addObserver(this@LocalFragment, observer)
        }
    }

    override fun initView(rootView: View) {
        val intent=Intent(context, NewPlayer::class.java)
        intent.action= ActionControlPlug.BIND
        context?.bindService(intent,connection,Context.BIND_AUTO_CREATE)
        rootView.topBar.setEndImageListener {
            SettingActivity.actionStart(context)
        }

        rootView.layout_localBg.setOnClickListener {
            MusicActivity.actionStart(context)
        }

        rootView.layout_recent.setOnClickListener {
            RecentListenActivity.actionStart(it.context)
        }

        rootView.iv_search.setOnClickListener {
            SearchActivity.actionStart(context as Activity, HomePageActivity.searchCode)
        }
        rootView.layout_prefer.setOnClickListener {
            if(UserManager.isLogin()){
                MySongSheetInfoActivity.actionStart(requireContext(),-1L)
            }

        }

        rootView.layout_download.setOnClickListener {
            MusicDownLoadActivity.actionStart(it.context)
        }

        rootView.layout_fastScan.setOnClickListener {
            ActionControlPlug.scan(requireContext())
        }

        rootView.layout_createSongSheet.setOnClickListener {
            showCreatePop()
        }

        initData()
        rootView.rv_songSheetlist.layoutManager=GridLayoutManager(context,4)
        rootView.rv_songSheetlist.addItemDecoration(GapItemDecoration(0,10,10,10,
                remainBottomPadding = true,remainTopPadding = true,remainEndPadding = true,remainLeftPadding = true))
        rootView.rv_songSheetlist.adapter=adapter
        adapter.itemLongClick={_,index->
            showListPop(sheetList[index].id)
            true
        }
        adapter.itemClick={_,index->
            MySongSheetInfoActivity.actionStart(requireContext(),sheetList[index].id)
        }

    }



    private fun initData(){
        model.getMusicNum {
            rootView!!.tv_musicNum?.text = it.toString()
        }

        if(UserManager.isLogin()){
            rootView?.layout_prefer?.visibility=View.VISIBLE
        }else{
            rootView?.layout_prefer?.visibility=View.GONE
        }
        WWSongSheetModel.getLikeList {
            rootView!!.tv_preferNum?.text = it.ids.size.toString()
        }
        /*model.getPreferNum {

        }*/

        model.getDownloadNum {
            rootView!!.tv_downloadNum.text = it.toString()
        }

        model.getRecentMusicNum {
            rootView!!.tv_recentListen.text = it.toString()
        }

        if(UserManager.isLogin()){
            ImageLoad.load("").placeholder(R.drawable.def_user_icon).into(rootView!!.iv_userIcon)
            rootView!!.tv_userName.text=UserManager.getUserName()
            rootView!!.iv_userIcon.setOnClickListener(null)
        }else{
            rootView!!.iv_userIcon.setImageResource(R.drawable.def_user_icon)
            rootView!!.tv_userName.text=ResUtil.getString(R.string.login)
            rootView!!.iv_userIcon.setOnClickListener {
                LoginActivity.actionStart(it.context)
            }
        }
        if(UserManager.isLogin()){
            WWSongSheetModel.getSongSheetList{
                sheetList.clear()
                sheetList.addAll(it)
                adapter.update(it)
            }
        }else{
            adapter.update(ArrayList())
        }
    }

    private fun showCreatePop(){
        if(createSheetPop==null){
            createSheetPop=InputDialog(rootView!!.context)
                    .setTitle(ResUtil.getString(R.string.inputSongSheetName))
                    .setHint(ResUtil.getString(R.string.songSheetName))
                    .setConfirmListener {input->
                        WWSongSheetModel.createSongSheet(input) {res->
                                    if(res.code==200){
                                        createSheetPop?.dismiss()
                                        initData()
                                    }else{
                                        MToast.showToast(requireContext(),R.string.createSongSheetFailed)
                                        createSheetPop?.dismiss()
                                    }
                                }
                    }
        }
        createSheetPop?.showCenter(rootView!!)
    }

    private fun showListPop(sheetId:Long){
        if(listPop==null){
            listPop=ListDialog(requireContext())
                    .addItem(ResUtil.getString(R.string.delete), View.OnClickListener{
                        WWSongSheetModel.deleteSongSheet(sheetId){res->
                            if(res.code==200){
                                initData()
                            }
                            listPop?.dismiss()
                        }
                    })
        }
        listPop?.show()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }




}