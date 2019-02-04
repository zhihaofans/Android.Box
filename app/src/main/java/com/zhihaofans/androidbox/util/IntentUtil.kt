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

        fun getLaucherListOfIntent(mIntent: Intent): List<AppIntentGson> {

        }
    }
}