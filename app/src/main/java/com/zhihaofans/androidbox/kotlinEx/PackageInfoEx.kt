package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**

 * @author: zhihaofans

 * @date: 2019-01-04 13:02

 */
fun PackageInfo.getAppName(mContext: Context) = this.getAppName(mContext.packageManager)

fun PackageInfo.getAppName(mPackageManager: PackageManager) = this.applicationInfo.loadLabel(mPackageManager).toString()