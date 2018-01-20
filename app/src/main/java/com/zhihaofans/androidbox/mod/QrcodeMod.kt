package com.zhihaofans.androidbox.mod

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.zhihaofans.androidbox.util.SystemUtil

/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class QrcodeMod {
    private var activity: Activity = Activity()
    var isInstallQrPlugin = false
    private val sys = SystemUtil()
    fun setActivity(activity: Activity) {
        this@QrcodeMod.activity = activity
    }

    fun getInstalledPlugin(context: Context): Int {
        if (sys.isAppInstalled(context, "mark.qrcode")) return 1
        if (sys.isAppInstalled(context, "org.noear.scan")) return 2
        return -1
    }

    fun scan(qrcodeFrame: Int) {
        //0:调用自带(未完成) 1:调用mark.qrcode 2:调用org.noear.scan
        when (qrcodeFrame) {
            1 -> {
                val intent = Intent("mark.qrcode.SCAN")
                intent.setClassName("mark.qrcode", "mark.qrcode.CaptureActivity")
                try {
                    activity.startActivityForResult(intent, 0) // 0:Qrcode
                    isInstallQrPlugin = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    isInstallQrPlugin = false
                    throw RuntimeException(e)
                }
            }
            2 -> {
                val intent = Intent("org.noear.scan.H5_SCAN")
                intent.setClassName("org.noear.scan", "org.noear.scan.CaptureActivity")
                try {
                    activity.startActivityForResult(intent, 0) // 0:Qrcode
                    isInstallQrPlugin = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    isInstallQrPlugin = false
                    throw RuntimeException(e)
                }
            }
        }
    }
}