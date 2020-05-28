package com.web.common.util

import java.io.UnsupportedEncodingException

object PinYin {
    private val index = intArrayOf(1601, 1600 + 0x15, 1800 + 0x21, 2000 + 0x4e, 2200 + 0x4a, 2300 + 0x02, 2400 + 0x21, 2500 + 0x5e, 2700 + 0x57, 3100 + 0x06, 3200 + 0x0c, 3400 + 0x48, 3600 + 0x23, 3700 + 0x16, 3700 + 0x1e, 3800 + 0x3a, 4000 + 0x1b, 4000 + 0x56, 4300 + 0x5a, 4500 + 0x3a, 4600 + 0x54, 4900 + 0x19, 5200 + 0x31)
    private val letter = "ABCDEFGHJKLMNOPQRSTWXYZ"

    @JvmStatic
    /**
     * 获取中文gb2312字符在字符表中的位置
     * @param chr gb2312字符，后扩充的字符一律按照
     * @return
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    private fun getCode(chr: String): Int {
        val b = chr.toByteArray(charset("gbk"))
        var res = 0
        if (b.size == 2) {
            res = (b[0] + 96) * 100//**区号*100
            res += b[1] + 96//**位号
        } else if (b.size == 1) {
            res = b[0] / 16 * 100 + b[0] % 16
        }
        return res
    }

    /**
     * 获取中文首字母没有在库内的就返回本身【一般是字母、数字】
     */
    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun getFirstChar(chr: String): Char {
        val code = getCode(chr)
        for (i in index.indices) {
            if (code < index[i]) {
                return if (i > 0)
                    letter[i - 1]
                else
                    chr.elementAt(0)
            }
        }
        return chr.elementAt(0)
    }

    @JvmStatic
    fun isChinese(word:Char):Boolean{
        return word.toInt() in 0x4E00..0x9FA5
    }

    @JvmStatic
    fun isEnglish(word: Char):Boolean{
        return word.toInt() in 'a'.toInt()..'z'.toInt() || word.toInt() in 'A'.toInt()..'Z'.toInt()
    }
}