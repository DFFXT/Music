package com.web.common.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

object PermissionManager {
    @JvmStatic
    fun requestIOPermission(activity:Activity):Boolean{
        val permissions= arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val res=activity.checkCallingPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(res==PackageManager.PERMISSION_DENIED){
            activity.requestPermissions(permissions, 0x666)
            return false
        }
        return true
    }
}