package com.zhihaofans.androidbox.kotlinEx

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import dev.utils.app.UriUtils

/**

 * @author: zhihaofans

 * @date: 2019-02-24 23:32

 */
fun Uri.getRealPath(ctx: Context): String {
    return UriUtils.getFilePathByUri(ctx, this)
}

fun Uri.isDownloadsDocument(): Boolean {
    return this.authority == "com.android.providers.downloads.documents" || this.authority == "com.android.providers.downloads.documents"
}

private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun Uri.getFilePathByUri(context: Context): String? {
    return if (ContentResolver.SCHEME_CONTENT == this.scheme && DocumentsContract.isDocumentUri(context, this) && this.isDownloadsDocument()) {
        val id = DocumentsContract.getDocumentId(this)
        if (id.startsWith("raw:")) {
            id.replaceFirst("raw:".toRegex(), "")
        } else {
            getDataColumn(context, this, null, null)
        }
    } else {
        UriUtils.getFilePathByUri(context, this)
    }

}
