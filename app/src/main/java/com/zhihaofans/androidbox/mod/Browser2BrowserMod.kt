package com.zhihaofans.androidbox.mod

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.zhihaofans.androidbox.data.Browser2BrowserBlockData
import com.zhihaofans.androidbox.gson.AppIntentGson
import com.zhihaofans.androidbox.kotlinEx.hasNotChild
import com.zhihaofans.androidbox.util.IntentUtil
import org.jetbrains.anko.newTask

class Browser2BrowserMod {

    companion object {
        private val blackList = mutableMapOf(
                "com.sina.weibo" to Browser2BrowserBlockData(
                        listOf(
                                "com.sina.weibo.browser.WeiboBrowser"
                        ),
                        null),
                "com.netease.cloudmusic" to Browser2BrowserBlockData(
                        listOf(
                                "com.netease.cloudmusic.activity.RedirectActivity"
                        ),
                        null)
        )

        fun getLauncherListWithBlackList(uri: String): List<AppIntentGson> = getLauncherListWithBlackList(uri.toUri())
        fun getLauncherListWithBlackList(uri: Uri): List<AppIntentGson> {
            val mIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                newTask()
            }
            val newList = mutableListOf<AppIntentGson>()
            val launcherList = IntentUtil.getLauncherListOfIntent(mIntent) ?: return listOf()
            launcherList.map {
                val browser2BrowserBlockData: Browser2BrowserBlockData? = blackList[it.packageName]
                when {
                    browser2BrowserBlockData == null -> newList.add(it)
                    browser2BrowserBlockData.className.hasNotChild(it) -> newList.add(it)
                    browser2BrowserBlockData.whiteListDomain !== null -> if (browser2BrowserBlockData.whiteListDomain.indexOf(uri.host!!) < 0) newList.add(it)


                }
                it
            }
            return newList
        }
    }
}