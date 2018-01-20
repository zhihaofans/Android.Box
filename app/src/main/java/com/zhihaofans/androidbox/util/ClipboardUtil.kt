package com.zhihaofans.androidbox.util

import android.content.*
import com.wx.android.common.util.ClipboardUtils

/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class ClipboardUtil(c: Context) {
    private val context = c
    // put==copy, get==paste

    fun copy(string: String) {
        ClipboardUtils.copy(context, string)
    }

    fun paste(): String {
        return ClipboardUtils.getText(context)
    }

    fun get(): String {
        return paste()
    }
}