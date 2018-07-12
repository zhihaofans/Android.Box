package com.zhihaofans.androidbox.util

import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.FileProvider
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import br.com.goncalves.pugnotification.notification.PugNotification
import com.orhanobut.logger.Logger
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.FileUtils
import com.wx.android.common.util.PackageUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.GlobalSettingMod
import com.zhihaofans.androidbox.view.ImageViewActivity
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import java.io.File
import java.net.URI
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

    fun closeKeyborad(activity: Activity) {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    fun time2date(time: Long): String {
        Logger.d(time)
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(Date(time)) as String
    }

    fun chromeCustomTabs(context: Context, url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark))
        builder.setShowTitle(true)
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun browseWeb(context: Context, url: String, title: String = url) {
        val globalSetting = GlobalSettingMod()
        if (this.checkUrl(url) == null) {
            throw Exception("No a correct url.")
        }
        if (globalSetting.imageUrlOpenWithBuiltinViewer() && this.checkIfImageUrl(url)) {
            context.startActivity<ImageViewActivity>("image" to url, "title" to title)
        } else if (globalSetting.forceUseChromeCustomTabs()) {
            chromeCustomTabs(context, url)
        } else {
            context.browse(url)
        }

    }

    fun booleen2string(boolean: Boolean, trueString: String, falseString: String): String {
        return if (boolean) {
            trueString
        } else {
            falseString
        }
    }

    fun booleen2string(boolean: Boolean): String {
        return boolean.toString()
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

    fun checkIfImageUrl(imageUrl: String): Boolean {
        return when (FileUtils.getFileSuffix(imageUrl).toLowerCase()) {
            "jpg", "jpeg", "bmp", "webp", "gif" -> true
            else -> false
        }
    }


    fun viewGetFocusable(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    }

    fun isApkDebugable(context: Context): Boolean {
        try {
            val info = context.applicationInfo
            return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {

        }

        return false
    }

    fun checkUrl(url: String?): URI? {
        if (url.isNullOrEmpty()) return null
        return try {
            URI(url)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getUrlFromBiliShare(shareString: String): URI? {
        val biliScheme = listOf("http", "https", "bilibili")
        var checked = this.checkUrl(shareString)
        var result: String? = null
        Logger.d("getUrlFromBiliShare:1")
        if (checked !== null) {
            return checked
        } else {
            Logger.d("getUrlFromBiliShare:2")
            biliScheme.map {
                Logger.d("Scheme:$it")
                val _a = shareString.indexOf("$it://")
                if (_a >= 0) {
                    val _b = shareString.indexOf(" ", _a)
                    Logger.d("_a:$_a\n_b:$_b")
                    if (_b > _a) {
                        result = shareString.substring(_a, _b)
                    } else {
                        result = shareString.substring(_a, shareString.length - 1)
                    }
                    Logger.d("result:$result")
                    checked = this.checkUrl(result)
                    Logger.d("getUrlFromBiliShare:3")
                    return if (checked !== null) {
                        checked
                    } else {
                        null
                    }
                }
            }
            Logger.d("getUrlFromBiliShare:4")
            return if (result.isNullOrEmpty()) {
                null
            } else {
                this.checkUrl(result)
            }
        }
    }

    fun urlAutoHttps(url: String?): String? {
        return if (url.isNullOrEmpty()) {
            null
        } else if (url!!.startsWith("//")) {
            "https:$url"
        } else {
            url
        }
    }

    fun notifySimple(context: Context, title: String, message: String) {
        PugNotification.with(context)
                .load()
                .title(title)
                .message(message)
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build()
    }
}