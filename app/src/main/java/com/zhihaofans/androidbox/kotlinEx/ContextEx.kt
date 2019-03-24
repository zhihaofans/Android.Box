package com.zhihaofans.androidbox.kotlinEx

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.SystemUtil
import org.jetbrains.anko.browse
import java.net.URL

/**
 * @author: zhihaofans

 * @date: 2018-11-06 15:55

 */
fun Context.appName(): String = this.getString(R.string.app_name)

fun Context.copy(text: String) {
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(this.packageName + ".text", text)
}

fun Context.paste(): String? {
    val clipManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (clipManager.hasPrimaryClip() || clipManager.primaryClipDescription != null || clipManager.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) return null
    val pasteData = clipManager.primaryClip!!.getItemAt(0).text
    if (pasteData.isNullOrEmpty()) return null
    return pasteData.toString()
}

fun Context.logD(message: Any) = Logger.d(message)
fun Context.logE(message: String) = Logger.e(message)
fun Context.logI(message: String) = Logger.i(message)
fun Context.browser(url: String, title: String = url) = SystemUtil.browse(this, url, title)
fun Context.browse(url: URL, newTask: Boolean = false) = this.browse(url.toString(), newTask)