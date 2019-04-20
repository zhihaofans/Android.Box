package com.zhihaofans.androidbox.mod

import io.zhihao.library.android.util.SharedPreferencesUtil

/**
 * Created by zhihaofans on 2019/4/20.
 */
class SettingMod {
    companion object {
        const val SETTING_TYPE_INT = "SETTING_TYPE_INT"
        const val SETTING_TYPE_STRING = "SETTING_TYPE_STRING"
        const val SETTING_TYPE_BOOLEAN = "SETTING_TYPE_BOOLEAN"
        private val settingDatebaseId = "com.zhihaofans.androidbox.setting"
        private val settingList = mutableMapOf<String, String>().apply {
            set("ACTIVITY_TOPHUB_BROWSER_LYNKET", SETTING_TYPE_BOOLEAN)
        }
        private val settingIdList = settingList.map { it.key }
        private val sp = SharedPreferencesUtil(settingDatebaseId)
        fun getSettingList() = settingList
        fun saveSetting(setId: String, setType: String, setData: Any): Boolean {
            return if (setId.isNotEmpty() && settingIdList.indexOf(setId) >= 0) {
                when (setType) {
                    SETTING_TYPE_BOOLEAN -> sp.putBoolean(setId, setData as Boolean)
                    SETTING_TYPE_STRING -> {
                        if ((setData as String).isEmpty()) {
                            false
                        } else {
                            sp.putString(setId, setData)
                        }
                    }
                    SETTING_TYPE_INT -> sp.putInt(setId, setData as Int)
                    else -> false
                }
            } else {
                false
            }
        }

        fun loadSetting(setId: String, setType: String): Any? {
            return if (setId.isNotEmpty() && settingIdList.indexOf(setId) >= 0) {
                when (setType) {
                    SETTING_TYPE_BOOLEAN -> sp.getBoolean(setId)
                    SETTING_TYPE_STRING -> sp.getString(setId, null)
                    SETTING_TYPE_INT -> sp.getInt(setId, -1)
                    else -> null
                }
            } else {
                null
            }
        }

        fun loadBooleanSetting(setId: String): Boolean? = loadSetting(setId, SETTING_TYPE_BOOLEAN) as Boolean?
        fun loadStringSetting(setId: String): String? = loadSetting(setId, SETTING_TYPE_STRING) as String?
        fun loadIntSetting(setId: String): Int? = loadSetting(setId, SETTING_TYPE_INT) as Int?
    }
}