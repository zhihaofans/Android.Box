package com.zhihaofans.androidbox.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


/**
 * Created by zhihaofans on 2018/11/4.
 */
class SharedPreferencesUtil {
    private var sharedPreferences: SharedPreferences? = null
    private var mContext: Context? = null
    private val sharedPreferencesFileName = "android_box"
    fun init(context: Context): SharedPreferences {
        this@SharedPreferencesUtil.mContext = context
        sharedPreferences = mContext!!.getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE)
        return sharedPreferences!!
    }
}