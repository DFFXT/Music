package com.web.moudle.music.player

fun <T> List<T>.findIndexFirst(from: Int, to: Int, predicate: (T, index: Int) -> Boolean): Int {
    if (from < 0 || to > size || from>to) return -1
    for (i in from until to) {
        if (predicate(get(i), i)) {
            return i
        }
    }
    return -1
}

fun <T> List<T>.findIndexLast(from: Int, to: Int, predicate: (T, index: Int) -> Boolean): Int {
    if (from<0 || to > size || from > to) return -1
    for (i in to downTo from) {
        if (predicate(get(i), i)) {
            return i
        }
    }
    return -1
}