package com.web.common.base

import net.sourceforge.pinyin4j.PinyinHelper
import java.text.Collator
import java.util.*
import kotlin.math.min

/**
 * 字符串比较器
 */
object ChineseComparator : Comparator<String> {
    const val chinese = "[\\u4e00-\\u9fa5+]"
    const val code = "[a-zA-Z]"
    override fun compare(o1: String?, o2: String?): Int {
        if (o1.isNullOrEmpty() && o2.isNullOrEmpty()) return 0
        if (o1.isNullOrEmpty()) return -1
        if (o2.isNullOrEmpty()) return 1
        var res: Int
        for (i in 0 until min(o1.length, o2.length)) {
            res = mCompare(o1.substring(i, i + 1), o2.substring(i, i + 1))
            if (res != 0) return res
        }
        return o1.length - o2.length
    }

    private fun mCompare(n1: String, n2: String): Int {
        if (n1.isEmpty() || n2.isEmpty()) return 0
        val valid1 = n1.matches("$chinese|$code".toRegex())//**是否是中英文
        val valid2 = n2.matches("$chinese|$code".toRegex())
        if (valid1 && valid2) {//***中英文
            var c1 = n1
            var c2 = n2
            if (n1.matches(chinese.toRegex())) {//**中文
                val py = PinyinHelper.toHanyuPinyinStringArray(n1[0])
                c1 = py?.getOrElse(0) { "*" } ?: "*"
            }
            if (n2.matches(chinese.toRegex())) {
                val py = PinyinHelper.toHanyuPinyinStringArray(n2[0])
                c2 = py?.getOrElse(0) { "*" } ?: "*"
            }
            return Collator.getInstance(Locale.CHINA).compare(c1, c2)
        } else if (valid1) {
            return -1
        } else if (valid2) {
            return 1
        } else {
            return n1[0] - n2[0]
        }
    }
}