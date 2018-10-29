package com.zhihaofans.androidbox.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.FileProvider
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
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
@SuppressLint("SimpleDateFormat")
class SystemUtil {
    companion object {
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
            Logger.d("com.zhihaofans.androidbox.download ->\nurl:$url\nsavePath:$savePath")
            FileDownloader.getImpl().create(url)
                    .setPath(savePath)
                    .setListener(listener).start()
        }

        fun downloadAndroid(context: Context, url: String, savePath: String, title: String = ""): Long {
            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDestinationInExternalPublicDir("dirType", savePath)
            request.setTitle(if (title.isEmpty()) {
                "下载中"
            } else {
                title
            })
            request.setDescription(savePath)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            val downloadId = downloadManager.enqueue(request)
            Logger.d("downloadAndroid\nurl:$url\nsaveTo:$savePath\ntitle:$title\ndownloadId:$downloadId")
            downloadManager
            return downloadId
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
            val p = m.getPackageInfo(context.packageName, 0).applicationInfo.dataDir
            return if (p.endsWith("/")) p else "$p/"
        }

        fun getPicturePath(): File {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }

        fun getPicturePathString(): String {
            val p = getPicturePath().path
            return if (p.endsWith("/")) p else "$p/"
        }

        fun getDownloadPath(): File {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        fun getDownloadPathString(): String {
            val p = getDownloadPath().path
            return if (p.endsWith("/")) p else "$p/"
        }


        fun openImageFile(context: Context, file: File): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            intent.setDataAndType(contentUri, "image/*")
            context.startActivity(intent)
            return intent
        }

        fun openImageFile(context: Context, file: String): Intent {
            return openImageFile(context, File(file))
        }

        fun listViewAdapter(context: Context, listData: List<String>): ArrayAdapter<String> {
            return ArrayAdapter(context, android.R.layout.simple_list_item_1, listData)
        }

        fun nowDate(full: Boolean = false): String {
            val formatter = SimpleDateFormat(if (full) "yyyy/MM/dd HH:mm:ss" else "yyyy/MM/dd")
            val curDate = Date()
            return formatter.format(curDate)
        }

        fun datePlus(day: String, Num: Int): String {
            Logger.d(day)
            val df = SimpleDateFormat(if (day.indexOf(":") >= 0) "yyyy/MM/dd HH:mm:ss" else "yyyy/MM/dd")
            var nowDate: Date? = null
            try {
                nowDate = df.parse(day)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val newDate2 = Date(nowDate!!.time + Num.toLong() * 24 * 60 * 60 * 1000)
            return df.format(newDate2)
        }

        fun installApk(context: Context, filePath: String) = context.startActivity(IntentUtils.getInstallAppIntent(filePath, context.packageName + ".fileprovider"))

        fun installApk1(context: Context, filePath: String) = AppUtils.installApp(filePath, context.packageName + ".fileprovider")

        fun installApk2(context: Context, filePath: String) {
            val apkFile = File(filePath)
            val intent = Intent(Intent.ACTION_VIEW)
            val apkUri: Uri = FileProvider.getUriForFile(context.applicationContext, context.packageName + ".fileprovider", apkFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            context.startActivity(intent)
        }


        @SuppressLint("Recycle")
        fun getRealFilePath(context: Context, uri: Uri?): String? {
            if (uri == null) return null
            val scheme: String? = uri.scheme
            var data: String? = null
            when (scheme) {
                null -> data = uri.path
                ContentResolver.SCHEME_FILE -> data = uri.path
                ContentResolver.SCHEME_CONTENT -> {
                    val cursor: Cursor? = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null, null)
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1) {
                                data = cursor.getString(index)
                            }
                        }
                        cursor.close()
                    }
                }
            }
            return data
        }

        fun httpPost4String(url: String, body: MutableMap<String, String> = mutableMapOf(), headers: MutableMap<String, String>? = null): String? {
            Logger.d("httpPost4String\nurl:$url\nbody:$body\nheader:$headers")
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
            body.map {
                requestBody.addFormDataPart(it.key, it.value)
            }
            val client = OkHttpClient.Builder().retryOnConnectionFailure(true).build()

            val requestBuilder = Request.Builder().url(url).post(requestBody.build())
            headers?.map {
                requestBuilder.addHeader(it.key, it.value)
            }
            val call = client.newCall(requestBuilder.build())
            Logger.d("httpPost4String")
            return try {
                val response = call.execute()
                val responseBody = response.body()
                Logger.d("response.code():${response.code()}")
                Logger.d("httpPost4String")
                if (responseBody == null) {
                    Logger.e("response.body() = null")
                    null
                } else {
                    val str = responseBody.string()
                    response.close()
                    Logger.d(str)
                    str
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }

    }
}