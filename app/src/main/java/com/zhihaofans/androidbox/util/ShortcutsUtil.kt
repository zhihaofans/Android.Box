package com.zhihaofans.androidbox.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import com.orhanobut.logger.Logger


/**

 * @author: zhihaofans

 * @date: 2019-01-22 20:51

 */
class ShortcutsUtil(context: Context) {
    private val mContext = context
    fun addPinShortcut(id: String, intent: Intent, shortcutName: String): Boolean {
        val shortcutManager = mContext.getSystemService(ShortcutManager::class.java)
        return if (shortcutManager.isRequestPinShortcutSupported) {
            intent.action = Intent.ACTION_VIEW //action必须设置，不然报错
            val pinShortcutInfo = ShortcutInfo.Builder(mContext, id)
                    .setShortLabel(shortcutName)
                    .setIntent(intent)
                    .build()
            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)
            val successCallback = PendingIntent.getBroadcast(mContext, 0, pinnedShortcutCallbackIntent, 0)
            try {
                shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.intentSender)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            Logger.e("shortcutManager.isRequestPinShortcutSupported = false")
            false
        }
    }
}