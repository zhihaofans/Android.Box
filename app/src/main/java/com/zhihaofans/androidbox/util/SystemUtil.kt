package com.zhihaofans.androidbox.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.orhanobut.logger.Logger
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.PackageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class SystemUtil {
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return PackageUtils.isInsatalled(context, packageName)
    }

    fun time2date(time: Int): String {
        Logger.d(time)
        return time2date(time.toLong())
    }

    fun time2date(time: Long): String {
        Logger.d(time)
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(Date(time)) as String
    }

    fun fileSize2String(fs: Int): String {
        var result = fs.toFloat()
        var times = 0
        while (result >= 1024) {
            result /= 1024
            times++
        }
        val units = mutableListOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB", "CB")
        val sizeUnit = if (times >= units.size) "???" else units[times]
        return "$result $sizeUnit"
    }

    fun installApk(context: Context, filePath: String) {
        //https://www.cnblogs.com/newjeremy/p/7294519.html
        val apkFile = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Logger.w("版本大于 N ，开始使用 fileProvider 进行安装")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, AppUtils.getPackageName(context) + ".fileprovider", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            Logger.w("正常进行安装")
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }
}