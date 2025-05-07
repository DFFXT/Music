package com.web.moudle.home.local

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.music.m.R
import com.music.m.databinding.FragmentLocalBinding
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

class LocalFragment : BaseFragment<FragmentLocalBinding>() {
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
                binding.tvMusicNum.text = it.toString()
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
        val intent = Intent(context, NewPlayer::class.java)
        intent.action = ActionControlPlug.BIND
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        binding.topBar.setEndImageListener {
            SettingActivity.actionStart(context)
        }

        binding.layoutLocalBg.setOnClickListener {
            MusicActivity.actionStart(it.context)
        }

        binding.layoutRecent.setOnClickListener {
            RecentListenActivity.actionStart(it.context)
        }

        binding.ivSearch.setOnClickListener {
            SearchActivity.actionStart(context as Activity, HomePageActivity.searchCode)
        }
        binding.layoutPrefer.setOnClickListener {
            if (UserManager.isLogin()) {
                MySongSheetInfoActivity.actionStart(requireContext(), -1L)
            }
        }

        binding.layoutDownload.setOnClickListener {
            MusicDownLoadActivity.actionStart(it.context)
        }

        binding.layoutFastScan.setOnClickListener {
            ActionControlPlug.scan(requireContext())
        }

        binding.layoutCreateSongSheet.setOnClickListener {
            showCreatePop()
        }

        initData()
        binding.rvSongSheetlist.layoutManager = GridLayoutManager(context, 4)
        binding.rvSongSheetlist.addItemDecoration(GapItemDecoration(0, 10, 10, 10,
            remainBottomPadding = true, remainTopPadding = true, remainEndPadding = true, remainLeftPadding = true
        ))
        binding.rvSongSheetlist.adapter = adapter
        adapter.itemLongClick = { _, index ->
            showListPop(sheetList[index].id)
            true
        }
        adapter.itemClick = { _, index ->
            MySongSheetInfoActivity.actionStart(requireContext(), sheetList[index].id)
        }
    }

    private fun initData() {
        model.getMusicNum {
            binding.tvMusicNum?.text = it.toString()
        }

        if (UserManager.isLogin()) {
            binding.layoutPrefer.visibility = View.VISIBLE
        } else {
            binding.layoutPrefer.visibility = View.GONE
        }
        WWSongSheetModel.getLikeList {
            binding.tvPreferNum?.text = it.ids.size.toString()
        }

        model.getDownloadNum {
            binding.tvDownloadNum.text = it.toString()
        }

        model.getRecentMusicNum {
            binding.tvRecentListen.text = it.toString()
        }

        if (UserManager.isLogin()) {
            ImageLoad.load("").placeholder(R.drawable.def_user_icon).into(binding.ivUserIcon)
            binding.tvUserName.text = UserManager.getUserName()
            binding.ivUserIcon.setOnClickListener(null)
        } else {
            binding.ivUserIcon.setImageResource(R.drawable.def_user_icon)
            binding.tvUserName.text = ResUtil.getString(R.string.login)
            binding.ivUserIcon.setOnClickListener {
                LoginActivity.actionStart(it.context)
            }
        }
        if (UserManager.isLogin()) {
            WWSongSheetModel.getSongSheetList {
                sheetList.clear()
                sheetList.addAll(it)
                adapter.update(it)
            }
        } else {
            adapter.update(ArrayList())
        }
    }

    private fun showCreatePop() {
        if (createSheetPop == null) {
            createSheetPop = InputDialog(binding.root.context)
                .setTitle(ResUtil.getString(R.string.inputSongSheetName))
                .setHint(ResUtil.getString(R.string.songSheetName))
                .setConfirmListener { input ->
                    WWSongSheetModel.createSongSheet(input) { res ->
                        if (res.code == 200) {
                            createSheetPop?.dismiss()
                            initData()
                        } else {
                            MToast.showToast(requireContext(), R.string.createSongSheetFailed)
                            createSheetPop?.dismiss()
                        }
                    }
                }
        }
        createSheetPop?.showCenter(binding.root)
    }

    private fun showListPop(sheetId: Long) {
        if (listPop == null) {
            listPop = ListDialog(requireContext())
                .addItem(ResUtil.getString(R.string.delete), View.OnClickListener {
                    WWSongSheetModel.deleteSongSheet(sheetId) { res ->
                        if (res.code == 200) {
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