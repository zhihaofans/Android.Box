package com.zhihaofans.androidbox.mod

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.zhihaofans.androidbox.kotlinEx.getBooleanOrFalse
import com.zhihaofans.androidbox.kotlinEx.getStringOrNull
import com.zhihaofans.androidbox.util.SharedPreferencesUtil

/**
 * Created by zhihaofans on 2018/11/4.
 */
class AppSettingMod {
    private val sharedPreferencesUtil = SharedPreferencesUtil()
    private var sharedPreferences: SharedPreferences? = null
    private var mContext: Context? = null

    // Setting
    var serverChanKey: String?
        get() {
            if (sharedPreferences == null) return null
            return sharedPreferences!!.getStringOrNull("SERVER_CHAN_KEY")
        }
        set(key) {
            if (sharedPreferences != null) {
                sharedPreferences!!.edit {
                    putString("SERVER_CHAN_KEY", key)
                }
            }
        }
    var forceUseChromeCustomTabs: Boolean
        get() {
            if (sharedPreferences == null) return false
            return sharedPreferences!!.getBooleanOrFalse("BROWSER_USE_CHROME_CUSTOM_TABS")
        }
        set(value) {
            if (sharedPreferences != null) {
                sharedPreferences!!.edit {
                    putBoolean("BROWSER_USE_CHROME_CUSTOM_TABS", value)
                }
            }
        }
    var imageUrlOpenWithBuiltinViewer: Boolean
        get() {
            if (sharedPreferences == null) return false
            return sharedPreferences!!.getBooleanOrFalse("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER")
        }
        set(value) {
            if (sharedPreferences != null) {
                sharedPreferences!!.edit {
                    putBoolean("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER", value)
                }
            }
        }


    // Function
    fun init(context: Context): Boolean {
        mContext = context
        if (mContext == null) return false
        sharedPreferences = sharedPreferencesUtil.init(mContext!!)
        if (sharedPreferences == null) return false
        return true
    }


}