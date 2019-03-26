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
    @JvmStatic
    fun requestRecordPermission(activity: Activity):Boolean{
        return requestPermission(activity, arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    @JvmStatic
    fun requestAllPermission(activity: Activity):Boolean{
        return requestPermission(activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS))
    }
    @JvmStatic
    private fun requestPermission(activity: Activity,permissions:Array<String>):Boolean{
        var res=0
        permissions.forEach {
            res= res or activity.checkCallingPermission(it)
        }
        if(res!=PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(permissions, 0x666)
            return false
        }
        return true
    }
}