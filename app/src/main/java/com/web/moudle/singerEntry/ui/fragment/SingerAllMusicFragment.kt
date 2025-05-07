package com.web.moudle.singerEntry.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseFragment
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.home.mainFragment.subFragment.adapter.HomePageMusicAdapter
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusic
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.singerEntry.model.SingerEntryViewModel
import com.web.moudle.singerEntry.ui.SingerEntryActivityNew
import com.music.m.R
import com.music.m.databinding.FragmentSingerAllMusicBinding

class SingerAllMusicFragment : BaseFragment<FragmentSingerAllMusicBinding>() {
    override var title = ResUtil.getString(R.string.music)
    override fun getLayoutId(): Int = R.layout.fragment_singer_all_music

    private lateinit var vm: SingerEntryViewModel

    private val adapter = HomePageMusicAdapter()
    private val list = ArrayList<HomePageMusic>()
    private var page = 0

    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[SingerEntryViewModel::class.java]

        vm.songList.observe(this, Observer {
            binding.srlAllMusic.finishLoadMore()
            when (it.code) {
                LiveDataWrapper.CODE_OK -> {
                    page++
                    list.addAll(it.value.songList!!.map { item ->
                        map(item)
                    })
                    adapter.update(list)
                    binding.srlAllMusic.setNoMoreData(it.value.haveMore == 0)
                }
            }
        })

        binding.rvAllMusic.layoutManager = LinearLayoutManager(context)
        binding.rvAllMusic.addItemDecoration(DrawableItemDecoration(0, 0, 0, 2,
            drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))
        binding.rvAllMusic.adapter = adapter
        adapter.update(list)
        adapter.itemClick = { item, _ ->
            MusicDetailActivity.actionStart(requireContext(), item.song_id)
        }

        binding.srlAllMusic.setOnLoadMoreListener {
            vm.getSongList(requireArguments().getString(ID)!!, page * pageSize, pageSize)
        }

        vm.getSongList(requireArguments().getString(ID)!!, 0, pageSize)
    }

    private fun map(item: SimpleMusicInfo): HomePageMusic {
        val res = HomePageMusic()
        res.title = item.musicName
        res.song_id = item.songId
        res.pic_big = item.picSmall
        res.has_mv = 0
        res.author = item.author
        res.album_title = item.albumTitle
        res.album_id = item.albumId
        return res
    }

    companion object {
        private const val pageSize = 20
        private const val ID = "id"

        @JvmStatic
        fun getInstance(uid: String): SingerAllMusicFragment {
            val b = Bundle()
            b.putString(ID, uid)
            val f = SingerAllMusicFragment()
            f.arguments = b
            return f
        }
    }
}