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

    // Force use Chrome Cust Tabs
    fun forceUseChromeCustomTabs(): Boolean {
        return if (_get("BROWSER_USE_CHROME_CUSTOM_TABS").isNullOrEmpty()) {
            forceUseChromeCustomTabs(true)
            true
        } else _get("BROWSER_USE_CHROME_CUSTOM_TABS") == "1"
    }

    fun forceUseChromeCustomTabs(force: Boolean): Boolean {
        return if (force) {
            _set("BROWSER_USE_CHROME_CUSTOM_TABS", "1")
        } else {
            _set("BROWSER_USE_CHROME_CUSTOM_TABS", "0")
        }
    }

    // Image link open with built-in picture viewer
    fun imageUrlOpenWithBuiltinViewer(): Boolean {
        return if (_get("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER").isNullOrEmpty()) {
            imageUrlOpenWithBuiltinViewer(true)
            true
        } else _get("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER") == "1"
    }

    fun imageUrlOpenWithBuiltinViewer(boolean: Boolean): Boolean {
        return if (boolean) {
            _set("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER", "1")
        } else {
            _set("IMAGE_URL_OPEN_WITH_BUILTIN_VIEWER", "0")
        }
    }
}