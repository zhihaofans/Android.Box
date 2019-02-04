package com.zhihaofans.androidbox.mod

import android.content.Intent
import android.net.Uri
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

        fun getLauncherListWithBlackList(uri: Uri) {
            val mIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                newTask()
            }
            val newList = mutableListOf<AppIntentGson>()
            IntentUtil.getLaucherListOfIntent(mIntent).map {
                val browser2BrowserBlockData: Browser2BrowserBlockData? = blackList[it.packageName]
                when {
                    browser2BrowserBlockData == null -> newList.add(it)
                    browser2BrowserBlockData.className.hasNotChild(it) -> newList.add(it)
                    browser2BrowserBlockData.whiteListDomain !== null -> if (browser2BrowserBlockData.whiteListDomain.indexOf(uri.host!!) < 0) newList.add(it)


                }
                it
            }
        }
    }
}