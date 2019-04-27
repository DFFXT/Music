package com.web.moudle.artist.model


import com.web.moudle.artist.bean.ArtistBox
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import io.reactivex.Observable

class ArtistModel:BaseRetrofit() {

    fun getArtist(areaCode:Int,sexCode:Int,offset:Int,limit:Int): Observable<ArtistBox> {
        return obtainClass(NetApis.AllArtist::class.java)
                .getArtistList(areaCode,sexCode,offset,limit)
    }
}