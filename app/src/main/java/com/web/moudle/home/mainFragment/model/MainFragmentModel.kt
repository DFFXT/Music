package com.web.moudle.home.mainFragment.model

import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusicInfoBox
import com.web.moudle.home.mainFragment.subFragment.bean.MusicTagBox
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItemBox
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.songSheetEntry.adapter.JSEngine
import io.reactivex.Observable

class MainFragmentModel:BaseRetrofit() {

    fun getBillBroad(): Observable<BillBoardList> {
        return obtainClass(NetApis.HomePage::class.java)
                .requestBillboardList()
                .compose(SchedulerTransform())
    }

    fun getSongSheetType(tag:String,offset:Int,pageSize:Int):Observable<SongSheetItemBox>{
        val param=JSEngine.getInstance().getSongSheetTypeParam(tag,offset,pageSize)
        return obtainClass(NetApis.HomePage::class.java)
                .requestSongSheetType(param.param,param.timestamp,param.sign)
                .compose(SchedulerTransform())
    }

    fun getMusicTag():Observable<MusicTagBox>{
        return obtainClass(NetApis.HomePage::class.java)
                .requestMusicTag()
                .compose(SchedulerTransform())
    }

    fun getTagMusic(tag:String,offset:Int,pageSize: Int):Observable<HomePageMusicInfoBox>{
        return obtainClass(NetApis.HomePage::class.java)
                .requestTagMusic(tag,offset,pageSize)
                .compose(SchedulerTransform())
    }
}