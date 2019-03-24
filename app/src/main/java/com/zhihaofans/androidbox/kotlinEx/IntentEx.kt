package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import android.content.Intent


/**
 * Created by zhihaofans on 2018/12/8.
 */
fun Intent?.isNull() = this == null

fun Intent.getPackageName(context: Context): String? {
    val packageManager = context.packageManager
    val packages = packageManager.queryIntentActivities(this, 0)
    for (res in packages) {
        return res.activityInfo.packageName
    }
    return null
}

val Intent.isActionSend: Boolean
    get() = this.action == Intent.ACTION_SEND
val Intent.isActionView: Boolean
    get() = this.action == Intent.ACTION_VIEW
val Intent.isTypeTextPlain: Boolean
    get() {
        return if (this.isActionSend) {
            this.type == "text/plain"
        } else {
            false
        }
    }

fun Intent.getTextPlain(): String? {
    return if (this.isTypeTextPlain) {
        this.getStringExtra(Intent.EXTRA_TEXT)
    } else {
        null
    }
}