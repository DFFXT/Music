package com.example.fluttermodule.host.artist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.flutter.facade.Flutter
import io.flutter.view.FlutterMain
import org.jsoup.Jsoup

class ArtistActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(this)
        super.onCreate(savedInstanceState)
        setContentView(Flutter.createView(this,lifecycle,"singerView"))
        parseSingerData("")
    }

    private fun parseSingerData(url:String){
        val doc=Jsoup.connect(url).get()
        val mainBody = doc.getElementsByClass("main_body")
        val personInfo = doc.getElementById("personInfo")
        val tab = doc.getElementsByClass("tab_info")
        val mainTable=doc.getElementsByClass("main_table")

        val artistIcon=personInfo.getElementsByClass("img_border")[0].getElementsByTag("img")[0].attr("src")


    }

}