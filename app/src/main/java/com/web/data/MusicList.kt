package com.web.data

class MusicList<T>(var title: String) : MutableList<T> by ArrayList()