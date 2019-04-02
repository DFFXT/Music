package com.web

import com.web.data.MusicCache
import org.junit.Test
import java.util.regex.Pattern


class URLParamsTest {
    @Test
    fun test(){
        var index: Int
        val rowParams="abd=123&aa="
        var start=0
        var calculate=true
        while (calculate){
            index=rowParams.indexOf('=',start)
            var key:String
            if(index<=0){
                key=""
            }else{
                key=rowParams.substring(start,index)
            }

            val valueStart=index+1
            var value:String
            if(valueStart>=rowParams.length){

                break
            }
            val valueEnd=rowParams.indexOf('&',valueStart)
            if(valueEnd>=rowParams.length||valueEnd<0){
                value=rowParams.substring(valueStart)
                calculate=false
            }else{
                value=rowParams.substring(valueStart,valueEnd)
            }
            start=valueEnd+1
            System.out.println(key+" "+value)
            //params[key]=value
        }
    }
    private val cachePattern= Pattern.compile("([0-9]{1,10})-([0-9]{1,10})")
    @Test
    fun cacheTest(){
        val data="0-9,88-99099"
        val matcher=cachePattern.matcher(data)
        while (matcher.find()){
            System.out.println(data.subSequence(matcher.start(2),matcher.end(2)))
        }
    }

    @Test
    fun rangeTest(){
        val m=MusicCache("","","0-9,12-20,40-50,55,90")
        m.addRange(10,44)
        System.out.println(m.cacheData)
    }
}