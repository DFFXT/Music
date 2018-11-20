package com.web.moudle.db

import android.database.sqlite.SQLiteDatabase
import com.web.common.base.MyApplication

class SqlLiteHelper {
    private lateinit var db:SQLiteDatabase
    private val mainTable="mainTable"
    fun connect () {
        db=SQLiteDatabase.openOrCreateDatabase(MyApplication.context.getDatabasePath("playList"),null)
    }
    fun insertRecordToPlayList(name:String,musicId:Int){
        db.execSQL("insert into $name (musicId) values($musicId)")
    }
    fun createPlayList(name:String){
        createTable()
        insertAPlayList(name,name)
        db.execSQL("create table if not exists $name (id int,musicId int)")

    }
    private fun createTable(){
        db.execSQL("create table if not exists mainTable (id int,tableName String,playListName)")
    }
    private fun insertAPlayList(tableName:String,playListName:String){
        db.execSQL("insert into $mainTable(tableName) values($tableName,$playListName)")
    }
}