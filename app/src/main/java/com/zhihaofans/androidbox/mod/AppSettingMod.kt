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
        get() = sharedPreferencesUtil.getBoolean("BROWSER_USE_CHROME_CUSTOM_TABS") ?: false
        set(value) {
            sharedPreferencesUtil.putBoolean("BROWSER_USE_CHROME_CUSTOM_TABS", value)
        }
    var imageUrlOpenWithBuiltinViewer: Boolean
        get() = sharedPreferencesUtil.getBoolean("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER") ?: false
        set(value) {
            sharedPreferencesUtil.putBoolean("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER", value)
        }


    // Function
    fun init(context: Context): Boolean {
        mContext = context
        if (mContext == null) return false
        return true
    }


}