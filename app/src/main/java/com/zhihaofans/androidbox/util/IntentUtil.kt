package com.zhihaofans.androidbox.util

import android.content.Intent
import com.zhihaofans.androidbox.gson.AppIntentGson
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils

class IntentUtil {
    companion object {
        fun getLaunchAppIntentWithClassName(packageName: String, className: String): Intent {
            return IntentUtils.getLaunchAppIntent(packageName).apply {
                setClassName(packageName, className)
            }
        }

        fun isIntentHasAppToLaunch(mIntent: Intent): Boolean {
            return mIntent.resolveActivity(AppUtils.getPackageManager()) != null
        }

        fun getLauncherListOfIntent(mIntent: Intent): List<AppIntentGson>? {
            val pm = AppUtils.getPackageManager()
            val activityList = pm.queryIntentActivities(mIntent, 0)
            return try {
                activityList.map {
                    AppIntentGson(it.activityInfo.packageName, it.activityInfo.name, it)
                }.toList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getChooseDirIntent(): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        fun getChooseFileIntent(fileType: String = "*/*"): Intent = Intent(Intent.ACTION_PICK).apply { type = fileType }
        fun getChooseImageFileIntent(): Intent = getChooseFileIntent("image/*")
    }
}