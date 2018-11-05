package com.zhihaofans.androidbox.util

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.orhanobut.logger.Logger

/**
 * Created by zhihaofans on 2018/8/28.
 */
class DownloadManagerUtil {
    fun downloadAndroid(context: Context, url: String, savePath: String, title: String = ""): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalPublicDir("dirType", savePath)
        request.setTitle(if (title.isEmpty()) {
            "下载中"
        } else {
            title
        })
        request.setMimeType("application/vnd.android.package-archive")
        request.setDescription(savePath)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val downloadId = downloadManager.enqueue(request)
        Logger.d("downloadAndroid\nurl:$url\nsaveTo:$savePath\ntitle:$title\ndownloadId:$downloadId")
        return downloadId
    }

    fun getDownloadStatus(context: Context, downloadId: Long) {
        var c: Cursor? = null
        val bytesAndStatus = mutableListOf(-1, -1, 0)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        try {
            c = downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            c?.close()
        }
    }

    class DownloadStatus(
            val finished: Boolean,
            val currentSize: Int,
            val totalSize: Int
    )
}