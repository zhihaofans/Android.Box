package com.zhihaofans.androidbox.mod

import com.haoge.easyandroid.easy.EasySharedPreferences
import com.zhihaofans.androidbox.database.SettingSP

/**
 * Created by zhihaofans on 2018/6/10.
 */
class GlobalSettingMod {
    private val settingSP = EasySharedPreferences.load(SettingSP::class.java)
    // Force use Chrome Cust Tabs
    fun forceUseChromeCustomTabs(): Boolean {
        val b = settingSP.browser_use_chrome_custom_tabs
        forceUseChromeCustomTabs(b)
        return b
    }

    fun forceUseChromeCustomTabs(force: Boolean): Boolean {
        settingSP.browser_use_chrome_custom_tabs = force
        settingSP.apply {
            return this.browser_use_chrome_custom_tabs == force
        }
        return false
    }

    // Image link open with built-in picture viewer
    fun imageUrlOpenWithBuiltinViewer(): Boolean {
        val b = settingSP.image_url_open_with_builtin_viewer
        imageUrlOpenWithBuiltinViewer(b)
        return b
    }

    fun imageUrlOpenWithBuiltinViewer(boolean: Boolean): Boolean {
        settingSP.image_url_open_with_builtin_viewer = boolean
        settingSP.apply {
            return this.image_url_open_with_builtin_viewer == boolean
        }
        return false
    }
}
