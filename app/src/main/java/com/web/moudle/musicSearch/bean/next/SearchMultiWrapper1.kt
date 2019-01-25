package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchAlbumWrapper2
import com.web.moudle.musicSearch.bean.next.next.SearchSongSheetWrapper2
import com.web.moudle.musicSearch.bean.next.next.SearchSongWrapper2

data class SearchMultiWrapper1 (
        @JSONField(name = "song_info")
        val songInfo2: SearchSongWrapper2,
        @JSONField(name = "album_info")
        val albumInfo2:SearchAlbumWrapper2,
        @JSONField(name = "artist_info")
        val artistInfo2:SearchAlbumWrapper2,
        @JSONField(name = "playlist_info")
        val songSheetInfo2:SearchSongSheetWrapper2
)