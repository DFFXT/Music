package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

class SearchSug {
        @JSONField(name = "song")
        var musicSugList: ArrayList<MusicSug> = ArrayList()
        @JSONField(name = "album")
        var albumList: ArrayList<AlbumSug> = ArrayList()
        @JSONField(name = "artist")
        var artistList: ArrayList<ArtistSug> =ArrayList()

        var songSheetList: ArrayList<SongSheetSug> = ArrayList()
}
