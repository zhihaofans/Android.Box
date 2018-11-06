package com.zhihaofans.androidbox.mod

import android.content.Context
import com.zhihaofans.androidbox.kotlinEx.isNotNull
import com.zhihaofans.androidbox.util.SharedPreferencesUtil

/**
 * Created by zhihaofans on 2018/11/4.
 */
class AppSettingMod {
    private val sharedPreferencesUtil = SharedPreferencesUtil()
    private var mContext: Context? = null

    // Setting
    var serverChanKey: String?
        get() = sharedPreferencesUtil.getString("SERVER_CHAN_KEY")
        set(key) {
            if (key.isNotNull()) sharedPreferencesUtil.putString("SERVER_CHAN_KEY", key!!)
        }
    var forceUseChromeCustomTabs: Boolean
        get() = forceUseChromeCustomTabs()
        set(value) {
            forceUseChromeCustomTabs(value)
        }
    var imageUrlOpenWithBuiltinViewer: Boolean
        get() = imageUrlOpenWithBuiltinViewer()
        set(value) {
            sharedPreferencesUtil.putBoolean("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER", value)
        }

    fun forceUseChromeCustomTabs(boolean: Boolean? = null): Boolean {
        val key = "BROWSER_USE_CHROME_CUSTOM_TABS"
        return if (boolean == null) {
            sharedPreferencesUtil.getBoolean(key) ?: false
        } else {
            sharedPreferencesUtil.putBoolean(key, boolean)
        }
    }

    fun imageUrlOpenWithBuiltinViewer(boolean: Boolean? = null): Boolean {
        val key = "IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER"
        return if (boolean == null) {
            sharedPreferencesUtil.getBoolean(key) ?: false
        } else {
            sharedPreferencesUtil.putBoolean(key, boolean)
        }
    }

    // Function
    fun init(context: Context): Boolean {
        mContext = context
        if (mContext == null) return false
        sharedPreferencesUtil.init(mContext!!)
        return true
    }


}