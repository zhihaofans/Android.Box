package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.SystemUtil
import org.jetbrains.anko.browse
import java.net.URL

/**
 * @author: zhihaofans

 * @date: 2018-11-06 15:55

 */
fun Context.appName(): String = this.getString(R.string.app_name)

fun Context.logD(message: Any) = Logger.d(message)
fun Context.logE(message: String) = Logger.e(message)
fun Context.logI(message: String) = Logger.i(message)
fun Context.browser(url: String, title: String = url) = SystemUtil.browse(this, url, title)
fun Context.browse(url: URL, newTask: Boolean = false) = this.browse(url.toString(), newTask)