package com.zhihaofans.androidbox.util

import android.content.Context
import android.content.Intent
import com.zhihaofans.androidbox.data.AppIntentData
import dev.utils.app.IntentUtils
import io.zhihao.library.android.util.SystemServiceUtil


class IntentUtil {
    companion object {

        fun getLauncherListOfIntent(mIntent: Intent): List<AppIntentData>? {
            val pm = SystemServiceUtil.getPackageManager()
            val activityList = pm.queryIntentActivities(mIntent, 0)
            return try {
                activityList.map {
                    AppIntentData(it.activityInfo.packageName, it.activityInfo.name, it)
                }.toList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getInstallIntent(context: Context, filePath: String): Intent = IntentUtils.getInstallAppIntent(filePath, context.packageName + ".fileprovider")


    }
}