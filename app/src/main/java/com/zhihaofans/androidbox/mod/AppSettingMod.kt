package com.zhihaofans.androidbox.mod

import android.content.Context
import io.zhihao.library.android.util.SharedPreferencesUtil

/**
 * Created by zhihaofans on 2018/11/4.
 */
class AppSettingMod {
    private val sharedPreferencesFileName = "android_box"
    private val sharedPreferencesUtil = SharedPreferencesUtil(sharedPreferencesFileName)
    private var mContext: Context? = null

    // Setting
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


}