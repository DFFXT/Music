package com.web.moudle.singerEntry.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.music.m.R
import com.music.m.databinding.FragmentSingerAllMusicBinding
import com.web.common.base.BaseFragment
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.home.mainFragment.subFragment.adapter.HomePageMusicAdapter
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusic
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.singerEntry.model.SingerEntryViewModel

class SingerAllAlbumFragment : BaseFragment<FragmentSingerAllMusicBinding>() {
    override var title = ResUtil.getString(R.string.album_tab)
    override fun getLayoutId(): Int = R.layout.fragment_singer_all_music

    private lateinit var vm: SingerEntryViewModel

    private val adapter = HomePageMusicAdapter()
    private val list = ArrayList<HomePageMusic>()
    private var page = 0

    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[SingerEntryViewModel::class.java]

        vm.albumList.observe(this, Observer {
            binding.srlAllMusic.finishLoadMore()
            when (it.code) {
                LiveDataWrapper.CODE_OK -> {
                    page++
                    list.addAll(it.value.albumList!!.map { item ->
                        map(item)
                    })
                    adapter.update(list)
                    binding.srlAllMusic.setNoMoreData(it.value.haveMore == 0)
                }
            }
        })

        binding.rvAllMusic.layoutManager = LinearLayoutManager(context)
        binding.rvAllMusic.addItemDecoration(
            DrawableItemDecoration(
                0, 0, 0, 2,
                drawable = ResUtil.getDrawable(R.drawable.recycler_divider)
            )
        )
        binding.rvAllMusic.adapter = adapter
        adapter.update(list)
        adapter.itemClick = { item, _ ->
            AlbumEntryActivity.actionStart(requireContext(), item.album_id)
        }

        binding.srlAllMusic.setOnLoadMoreListener {
            vm.getAlbumList(requireArguments().getString(ID)!!, page * pageSize, pageSize)
        }

        vm.getAlbumList(requireArguments().getString(ID)!!, 0, pageSize)
    }

    private fun map(item: SimpleAlbumInfo): HomePageMusic {
        val res = HomePageMusic()
        res.title = item.albumName
        res.song_id = item.albumId
        res.pic_big = item.albumImage
        res.has_mv = 0
        res.author = item.artistName
        res.album_title = ""
        res.album_id = item.albumId
        return res
    }

    companion object {
        private const val pageSize = 20
        private const val ID = "id"

        @JvmStatic
        fun getInstance(uid: String): SingerAllAlbumFragment {
            val b = Bundle()
            b.putString(ID, uid)
            val f = SingerAllAlbumFragment()
            f.arguments = b
            return f
        }
    }
}