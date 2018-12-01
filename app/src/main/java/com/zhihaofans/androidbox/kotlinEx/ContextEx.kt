package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-06 15:55

 */
fun Context.appName(): String = this.getString(R.string.app_name)

fun Context.logd(message: Any) = Logger.d(message)
fun Context.loge(message: String) = Logger.e(message)
fun Context.logi(message: String) = Logger.i(message)