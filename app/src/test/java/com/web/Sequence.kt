package com.web

import com.web.common.base.ChineseComparator
import org.junit.Test

class Sequence{
    @Test
    fun seq(){
        val v="刘1".matches(("${ChineseComparator.chinese}|${ChineseComparator.code}").toRegex())
        val data=ArrayList<String>()
        data.add("刘1")
        data.add("金11")
        data.add("啊成")
        data.add("啊吧")
        data.add("龙111")
        data.sortWith(ChineseComparator)
        data.sortWith(Comparator { o1, o2 ->
            return@Comparator  o1.length-o2.length
        })
        System.out.println(ChineseComparator.compare("刘","金"))
        System.out.println(ChineseComparator.compare("a","b"))
        System.out.println(ChineseComparator.compare("银","龙"))
    }
}