package com.zhihaofans.androidbox.util

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.kotlinEx.remove
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils
import dev.utils.common.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by zhihaofans on 2019/2/21.
 */
class FileUtil {
    companion object {
        fun saveFile(filePath: String, content: String): Boolean {
            Logger.d("FileUtil.saveFile.filePath = $filePath")
            return try {
                val fileName = FileUtils.getFileName(filePath)
                val saveTo = filePath.remove(fileName)
                FileUtils.saveFile(saveTo, fileName, content)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun getWallpaper(context: Context): Bitmap? {
            val wmInstance = WallpaperManager.getInstance(context)
            return if (wmInstance.isWallpaperSupported) wmInstance.drawable.toBitmap() else null
        }

        fun saveWallpaperPng(context: Context, filePath: String) = saveWallpaper(context, filePath, Bitmap.CompressFormat.PNG)

        fun saveWallpaper(context: Context, filePath: String, imageFormat: Bitmap.CompressFormat): Boolean {
            File(filePath).apply {
                if (!FileUtils.createOrExistsDir(this.parent)) return false
            }
            return getWallpaper(context)?.compress(imageFormat, 100, FileOutputStream(filePath))
                    ?: false
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

        fun getFileSize(path: String): Long {
            if (path.isEmpty()) {
                return -1
            }
            val file = File(path)
            return if (file.exists() && file.isFile) file.length() else -1
        }

        fun getOpenImageFileIntent(context: Context, file: File): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            intent.setDataAndType(contentUri, "image/*")
            return intent
        }

        fun openImageFile(context: Context, file: File): Intent {
            val intent = this.getOpenImageFileIntent(context, file)
            context.startActivity(intent)
            return intent
        }

        fun openImageFile(context: Context, file: String): Intent {
            return openImageFile(context, File(file))
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

        fun download(url: String, savePath: String, listener: FileDownloadListener) {
            Logger.d("com.zhihaofans.androidbox.download ->\nurl:$url\nsavePath:$savePath")
            FileDownloader.getImpl().create(url)
                    .setPath(savePath)
                    .setListener(listener).start()
        }

        fun downloadAndroid(context: Context, url: String, savePath: String, title: String = ""): Long {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
    }
}