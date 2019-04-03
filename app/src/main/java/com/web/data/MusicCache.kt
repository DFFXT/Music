package com.web.data

import com.web.moudle.net.proxy.bean.CacheFromTo
import org.litepal.crud.DataSupport
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

/**
 * 音乐缓存信息
 * musicPath在查询中使用，禁止重命名
 */
data class MusicCache(
        val musicPath:String,
        val cachePath:String,
        var cacheData:String
):DataSupport(){
    var id:Int=0
    fun addRange(from:Long,to:Long){
        val cacheData= calculateCacheRange(this)
        var res:List<CacheFromTo>
        var mFrom=from
        var mTo=to
        var combineOne=false
        var i=0
        while(i < cacheData.size){
            val f=cacheData[i].from
            val t=cacheData[i].to
            res= rangeAdd(f,t,mFrom,mTo)
            if(res.size==1){//**合并成一个区间了
                mFrom=res[0].from
                mTo=res[0].to

                if(i==cacheData.size-1){
                    cacheData.removeAt(i)
                    cacheData.add(i,res[0])
                    break
                }else{
                    cacheData.removeAt(i)
                    i--
                }

                combineOne=true
            }else if(res.size==2){//**没有和当前区间合并成一个区间
                if(combineOne){//**和前一个区间合并成立一个区间
                    cacheData.add(i,CacheFromTo(mFrom,mTo))
                    break
                }
                else if(res[0].from!=f){//**添加的区间在当前区间左边
                    cacheData.add(i,res[0])
                    break
                }
            }
            i++
        }
        //**初次写入
        if(cacheData.size==0){
            cacheData.add(CacheFromTo(from,to))
        }
        //**将对象区间写回字符串
        this.cacheData=""
        for(index in cacheData.indices){
            this.cacheData+=","+cacheData[index].from.toString()+"-"+cacheData[index].to.toString()
        }
        if(cacheData.size!=0){
            this.cacheData=this.cacheData.substring(1)
        }
        saveAsync().listen {}

    }
    companion object {
        @JvmStatic
        private val cachePattern= Pattern.compile("([0-9]{1,10})-([0-9]{1,10})")
        /**
         * 分析缓存数据缓存范围
         */
        @JvmStatic
        fun calculateCacheRange(musicCache: MusicCache):ArrayList<CacheFromTo>{
            val fromTo=ArrayList<CacheFromTo>()
            val cache=musicCache.cacheData
            val matcher= cachePattern.matcher(cache)
            while (matcher.find()){
                fromTo.add(CacheFromTo(
                        cache.substring(matcher.start(1),matcher.end(1)).toLong(),
                        cache.substring(matcher.start(2),matcher.end(2)).toLong()
                ))
            }
            return fromTo
        }

        /**
         * 两个区间进行合并
         * 返回新的集合
         */
        @JvmStatic
        private fun rangeAdd(from1: Long,to1:Long,from2:Long,to2:Long):List<CacheFromTo>{
            val res=ArrayList<CacheFromTo>()
            when {
                to1 in from2..to2 -> res.add(CacheFromTo(min(from1,from2),to2))
                to2 in from1..to1 -> res.add(CacheFromTo(min(from1,from2),to1))
                else -> {
                    res.add(CacheFromTo(min(from1,from2),min(to1,to2)))
                    res.add(CacheFromTo(max(from1,from2), max(to1,to2)))
                }
            }
            return res
        }
    }

}