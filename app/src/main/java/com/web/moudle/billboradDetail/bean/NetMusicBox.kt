package com.web.moudle.billboradDetail.bean

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo

data class NetMusicBox (
        @JSONField(name = "song_list")
        val list:List<SimpleMusicInfo>,
        @JSONField(name = "billboard")
        val billboardInfo:BillBoardInfo
)