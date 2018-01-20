package com.zhihaofans.androidbox.util

import android.content.Context
import com.wx.android.common.util.*

/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class SystemUtil {
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return PackageUtils.isInsatalled(context, packageName)
    }
}