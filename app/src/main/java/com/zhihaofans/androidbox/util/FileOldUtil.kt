package com.zhihaofans.androidbox.util

import android.content.Context
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import dev.utils.app.AppUtils

/**
 * Created by zhihaofans on 2019/2/21.
 */
class FileOldUtil {
    companion object {
        fun installApk(context: Context, filePath: String) = AppUtils.installApp(filePath, context.packageName + ".fileprovider")
        fun download(url: String, savePath: String, listener: FileDownloadListener): Int {
            LogUtil.d("com.zhihaofans.androidbox.download ->\nurl:$url\nsavePath:$savePath")
            return FileDownloader.getImpl().create(url)
                    .setPath(savePath)
                    .setListener(listener).start()
        }


    }
}