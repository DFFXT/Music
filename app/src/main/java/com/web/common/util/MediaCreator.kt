package com.web.common.util

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.web.common.base.MyApplication
import java.io.File

object MediaCreator {
    //API 30上RELATIVE_PATH只能是  Pictures/xx或者DCIM/xxx
    private const val IMAGE_PATH = "Pictures/music"
    fun createImageUri(fileName: String): Uri {
        val uri = MyApplication.context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues().apply {
            val index = fileName.indexOfLast { it == ',' }
            val displayName = if (index <= 0) {
                fileName
            } else fileName.substring(index + 1)
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.TITLE, fileName)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, IMAGE_PATH)
            } else {
                val path = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + IMAGE_PATH
                val file = File(path)
                if (!file.exists()) {
                    file.mkdir()
                }
                put(MediaStore.Images.Media.DATA, path + File.separator + fileName)
            }
        }
        )
        return uri!!
    }
}