package com.web.moudle.music.player

import com.web.common.base.log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class RandomSystem {
    private val valueList = LinkedList<Number>()
    private val rowNumberList = LinkedList<Number>()
    private val random = Random(System.currentTimeMillis())
    fun addNumber(number: Number) {
        valueList.add(number)
        rowNumberList.add(number)
    }

    fun addIntRange(from: Int, length: Int) {
        for (i in from until length + from) {
            addNumber(i)
        }
    }

    fun getRandomNumber(): Number {
        if (rowNumberList.size == 0) {
            return 0
        } else if (valueList.size == 0) {
            valueList.addAll(rowNumberList)
        }
        val index = random.nextInt(valueList.size)
        return valueList.removeAt(index)
    }

    fun reset(id: Int) {
        valueList.clear()
        rowNumberList.clear()
    }
}