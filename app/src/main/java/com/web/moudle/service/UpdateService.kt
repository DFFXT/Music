package com.web.moudle.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.content.FileProvider
import com.web.common.base.BaseActivity
import com.web.common.constant.Constant
import com.web.common.util.IOUtil
import com.web.common.util.ResUtil
import com.web.moudle.notification.FileDownloadNotification
import com.web.web.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class UpdateService : Service() {
    companion object {
        @JvmStatic
        fun downloadUpdateApk(context: Context, path: String) {
            val intent = Intent(context, UpdateService::class.java)
            intent.putExtra(BaseActivity.INTENT_DATA, path)
            context.startService(intent)
        }
    }

    //**更新保存地址
    private val savePath = Constant.LocalConfig.cachePath + "/update.apk"

    private lateinit var notification:FileDownloadNotification

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        notification=FileDownloadNotification(this)
        val path = intent.getStringExtra(BaseActivity.INTENT_DATA)
        update(path, savePath)

        return START_NOT_STICKY
    }

    private fun update(path: String, savePath: String) {
        GlobalScope.launch(Dispatchers.IO) {
            IOUtil.onlineDataToLocal(path, savePath,
                    progressCallBack = {progress,max->
                        notification.notify(ResUtil.getString(R.string.setting_updateTitle,ResUtil.getString(R.string.app_name)),progress*100/(max?:-1).toFloat())
                    },
                    stopCallback = {
                        notification.cancel()
                    })
            install(savePath)
        }

    }

    private fun install(filePath: String) {
        val apkFile = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(
                    this, "com.music.m", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        startActivity(intent)
        stopSelf()
    }


}