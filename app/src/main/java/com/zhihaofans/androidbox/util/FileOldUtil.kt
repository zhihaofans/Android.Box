package com.zhihaofans.androidbox.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import dev.utils.app.AppUtils
import dev.utils.common.FileUtils
import io.zhihao.library.android.kotlinEx.remove
import java.io.File
import java.io.FileOutputStream

/**
 * Created by zhihaofans on 2019/2/21.
 */
class FileOldUtil {
    companion object {
        fun saveFile(filePath: String, content: String): Boolean {
            Logger.d("FileOldUtil.saveFile.filePath = $filePath")
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

        fun installApk(context: Context, filePath: String) = AppUtils.installApp(filePath, context.packageName + ".fileprovider")


        fun download(url: String, savePath: String, listener: FileDownloadListener) {
            Logger.d("com.zhihaofans.androidbox.download ->\nurl:$url\nsavePath:$savePath")
            FileDownloader.getImpl().create(url)
                    .setPath(savePath)
                    .setListener(listener).start()
        }


    }
}