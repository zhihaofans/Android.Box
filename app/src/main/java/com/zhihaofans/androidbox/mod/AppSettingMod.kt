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
    var imageUrlOpenWithBuiltinViewer: Boolean
        get() = imageUrlOpenWithBuiltinViewer()
        set(value) {
            imageUrlOpenWithBuiltinViewer(value)
        }
    var buildinX5Browser: Boolean
        get() = buildinX5Browser()
        set(value) {
            buildinX5Browser(value)
        }

    fun imageUrlOpenWithBuiltinViewer(boolean: Boolean? = null): Boolean {
        val key = "IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER"
        sharedPreferencesUtil.apply {
            return if (boolean == null) {
                getBoolean(key) ?: true
            } else {
                putBoolean(key, boolean)
            }
        }
    }

    fun buildinX5Browser(boolean: Boolean? = null): Boolean {
        val key = "BUILD_IN_X5_BROWSER"
        sharedPreferencesUtil.apply {
            return if (boolean == null) {
                getBoolean(key) ?: true
            } else {
                putBoolean(key, boolean)
            }
        }
    }

    // Browser2Browser

    var browser2BrowserDefault: String?
        get() = sharedPreferencesUtil.getString("BROWSER_TO_BROWSER_DEFAULT")
        set(key) {
            if (!key.isNullOrEmpty()) sharedPreferencesUtil.putString("BROWSER_TO_BROWSER_DEFAULT", key)
        }

    // Function
    fun init(context: Context): Boolean {
        mContext = context
        if (mContext == null) return false
        sharedPreferencesUtil.init(mContext!!)
        return true
    }


}