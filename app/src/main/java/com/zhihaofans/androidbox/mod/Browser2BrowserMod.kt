package com.zhihaofans.androidbox.mod

import android.content.Intent
import android.net.Uri
import com.zhihaofans.androidbox.data.AppIntentData
import com.zhihaofans.androidbox.data.Browser2BrowserBlackListData
import com.zhihaofans.androidbox.util.IntentOldUtil
import com.zhihaofans.androidbox.util.LogUtil
import io.zhihao.library.android.kotlinEx.hasNotChild
import org.jetbrains.anko.newTask

class Browser2BrowserMod {

    companion object {
        private val blackList = mutableMapOf(
                "com.zhihaofans.androidbox" to Browser2BrowserBlackListData(listOf("com.zhihaofans.androidbox.view.Browser2BrowserActivity"), null),
                "com.sina.weibo" to Browser2BrowserBlackListData(listOf("com.sina.weibo.browser.WeiboBrowser"), null),
                "com.netease.cloudmusic" to Browser2BrowserBlackListData(listOf("com.netease.cloudmusic.activity.RedirectActivity"), null),
                "com.taobao.taobao" to Browser2BrowserBlackListData(listOf("com.taobao.browser.BrowserActivity"), null)
        )

        fun getLauncherListWithBlackList(uri: String): List<AppIntentData> = getLauncherListWithBlackList(Uri.parse(uri))
        fun getLauncherListWithBlackList(uri: Uri): List<AppIntentData> {
            val mIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                newTask()
            }
            val newList = mutableListOf<AppIntentData>()
            val launcherList = IntentOldUtil.getLauncherListOfIntent(mIntent) ?: return listOf()
            LogUtil.d("launcherList:$launcherList")
            launcherList.map { appIntentGson ->
                val browser2BrowserBlockData: Browser2BrowserBlackListData? = blackList[appIntentGson.packageName]
                when {
                    browser2BrowserBlockData == null -> newList.add(appIntentGson)
                    browser2BrowserBlockData.className.hasNotChild(appIntentGson.className) -> newList.add(appIntentGson)
                    browser2BrowserBlockData.whiteListDomain !== null -> if (browser2BrowserBlockData.whiteListDomain.indexOf(uri.host!!) < 0) newList.add(appIntentGson)
                }
                appIntentGson
            }
            return newList
        }
    }
}