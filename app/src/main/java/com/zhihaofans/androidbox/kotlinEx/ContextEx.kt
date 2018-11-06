package com.zhihaofans.androidbox.kotlinEx

import android.content.Context
import com.zhihaofans.androidbox.R

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-06 15:55

 */
fun Context.appName(): String = this.getString(R.string.app_name)
