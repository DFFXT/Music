package com.web.common.base

import net.sourceforge.pinyin4j.PinyinHelper
import java.text.Collator
import java.util.*

/**
 * 字符串比较器
 */
object ChineseComparator:Comparator<String> {
    private const val chinese = "[\\u4e00-\\u9fa5+]"
    private const val code = "[a-zA-Z]"
    override fun compare(o1: String?, o2: String?): Int {
        val n1 = o1?:" "
        val n2 = o2?:" "
        val valid1 = n1.matches("$chinese|$code".toRegex())//**是否是中英文
        val valid2 = n2.matches("$chinese|$code".toRegex())
        if (valid1 && valid2) {//***中英文
            var c1 = n1
            var c2 = n2
            if (n1.matches(chinese.toRegex())) {//**中文
                c1 = PinyinHelper.toHanyuPinyinStringArray(n1[0])[0]
            }
            if (n2.matches(chinese.toRegex())) {
                c2 = PinyinHelper.toHanyuPinyinStringArray(n2[0])[0]
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