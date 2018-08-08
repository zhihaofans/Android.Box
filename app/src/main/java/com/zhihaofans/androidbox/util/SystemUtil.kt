package com.zhihaofans.androidbox.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.support.customtabs.CustomTabsIntent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import com.wx.android.common.util.FileUtils
import com.wx.android.common.util.PackageUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.GlobalSettingMod
import com.zhihaofans.androidbox.view.ImageViewActivity
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL


/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class SystemUtil {
    fun debug(context: Context): Boolean {
        return this.isApkDebugable(context)
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return PackageUtils.isInsatalled(context, packageName)
    }

    fun closeKeyborad(activity: Activity) {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    fun chromeCustomTabs(context: Context, url: String, title: String = url) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark))
        builder.setShowTitle(true)
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun browseWithoutSet(context: Context, url: String, title: String = url, imageView: Boolean = false, customTabs: Boolean = false) {
        if (imageView && this.checkIfImageUrl(url)) {
            context.startActivity<ImageViewActivity>("image" to url, "title" to title)
        } else if (customTabs) {
            this.chromeCustomTabs(context, url, title)
        } else {
            context.browse(url)
        }
    }

    fun browse(context: Context, url: String, title: String = url) {
        val globalSetting = GlobalSettingMod()
        if (this.checkUrl(url) == null) {
            throw Exception("No a correct url.")
        }
        if (globalSetting.imageUrlOpenWithBuiltinViewer() && this.checkIfImageUrl(url)) {
            context.startActivity<ImageViewActivity>("image" to url, "title" to title)
        } else if (globalSetting.forceUseChromeCustomTabs()) {
            this.chromeCustomTabs(context, url)
        } else {
            context.browse(url)
        }
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

    fun urlAutoHttps(url: String?, https: Boolean): String? {
        return if (url.isNullOrEmpty()) {
            null
        } else if (url!!.startsWith("//")) {
            if (https) {
                "https:$url"
            } else {
                "http:$url"
            }
        } else {
            url
        }
    }

    fun download(url: String, savePath: String, listener: FileDownloadListener) {
        FileDownloader.getImpl().create(url)
                .setPath(savePath)
                .setListener(listener).start()
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            if (src.isEmpty()) return null
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            e.printStackTrace()
            null
        }
    }

    fun getAppPrivateDirectory(context: Context): String? {
        val m = context.packageManager
        return m.getPackageInfo(context.packageName, 0).applicationInfo.dataDir
    }

    fun getPicturePath(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    }

    fun getPicturePathString(): String {
        return getPicturePath().path
    }

    fun getDownloadPath(): File {

        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    fun getDownloadPathString(): String {
        return getDownloadPath().path
    }

    fun openImageFile(context: Context, file: File): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "image/*")
        context.startActivity(intent)
        return intent
    }

    fun openImageFile(context: Context, file: String): Intent {
        return openImageFile(context, File(file))
    }

    fun listViewAdapter(context: Context, listData: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_list_item_1, listData)
    }

}