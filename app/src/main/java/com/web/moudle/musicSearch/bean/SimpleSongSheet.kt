package com.web.moudle.musicSearch.bean

import com.alibaba.fastjson.annotation.JSONField

data class SimpleSongSheet(
        val sheetId:String,
        val sheetName:String,
        val sheetIcon:String,
        val sheetCreator:String,
        val songCount:String,
        val playCount:String
)