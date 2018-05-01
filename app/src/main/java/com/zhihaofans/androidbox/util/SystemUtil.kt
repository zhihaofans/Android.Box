package com.zhihaofans.androidbox.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.FileProvider
import android.view.inputmethod.InputMethodManager
import com.orhanobut.logger.Logger
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.PackageUtils
import com.zhihaofans.androidbox.R
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

    fun closeKeyborad(context: Context, activity: Activity) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    fun time2date(time: Long): String {
        Logger.d(time)
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(Date(time)) as String
    }

    fun chromeCustomTabs(context: Context, url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark))
        customTabsIntent.launchUrl(context, Uri.parse(url))
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