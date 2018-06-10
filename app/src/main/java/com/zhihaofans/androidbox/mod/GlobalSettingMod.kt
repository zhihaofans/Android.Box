package com.zhihaofans.androidbox.mod

import com.wx.android.common.util.SharedPreferencesUtils

/**
 * Created by zhihaofans on 2018/6/10.
 */
class GlobalSettingMod {
    private val settingName = "setting"
    fun _getAll(): MutableMap<String, *>? {
        return SharedPreferencesUtils.getAll(settingName)
    }


    fun _get(settingId: String): String? {
        return SharedPreferencesUtils.getString(settingName, settingId)

    }

    fun _set(settingId: String, settingValve: String): Boolean {
        return SharedPreferencesUtils.put(settingName, settingId, settingValve)
    }

    fun _removeAll(areUsure: Boolean) {
        SharedPreferencesUtils.clear(settingName)
    }

    fun forceUseChromeCustomTabs(): Boolean {
        return if (_get("BROWSER_USE_CHROME_CUSTOM_TABS").isNullOrEmpty()) {
            forceUseChromeCustomTabs(false)
            false
        } else _get("BROWSER_USE_CHROME_CUSTOM_TABS") == "1"
    }

    fun forceUseChromeCustomTabs(force: Boolean): Boolean {
        return if (force) {
            _set("BROWSER_USE_CHROME_CUSTOM_TABS", "1")
        } else {
            _set("BROWSER_USE_CHROME_CUSTOM_TABS", "0")
        }
    }

}