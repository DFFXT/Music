package com.web.moudle.home.mainFragment.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusicInfoBox
import com.web.moudle.home.mainFragment.subFragment.bean.MusicTagBox
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItemBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainFragmentViewModel : ViewModel() {

    private val model = MainFragmentModel()
    val billboard = MutableLiveData<BillBoardList>()
    val songSheetList = MutableLiveData<SongSheetItemBox>()
    val musicTagList = MutableLiveData<MusicTagBox>()
    val tagMusicList = MutableLiveData<HomePageMusicInfoBox>()

    fun getBillboardList() {
        model.getBillBroad()
                .get(
                        onNext = {
                            billboard.value = it
                        }, onError = {
                    billboard.value = null
                }
                )
    }

    fun getSongSheetType(tag: String, offset: Int, pageSize: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            model.getSongSheetType(tag, offset, pageSize)
                    .get(onNext = {
                        it.tag = tag
                        songSheetList.value = it
                    }, onError = {
                        it.printStackTrace()
                    })
        }

    }

    fun getMusicTag() {
        model.getMusicTag()
                .get(
                        onNext = {
                            musicTagList.value=it
                        },
                        onError = {
                            it.printStackTrace()
                        }
                )
    }

    fun getTagMusic(tag:String,offset:Int,pageSize: Int){
        model.getTagMusic(tag,offset,pageSize)
                .get(
                        onNext = {
                            it.tag=tag
                            tagMusicList.value=it
                        },
                        onError = {
                            it.printStackTrace()
                        }
                )
    }
}