package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import android.content.Intent


/**
 * Created by zhihaofans on 2018/12/8.
 */
fun Intent.getPackageName(context: Context): String? {
    val packageManager = context.packageManager
    val packages = packageManager.queryIntentActivities(this, 0)
    for (res in packages) {
        return res.activityInfo.packageName
    }
    return null
}