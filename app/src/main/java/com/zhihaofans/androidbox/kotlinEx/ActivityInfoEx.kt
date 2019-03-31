package com.zhihaofans.androidbox.kotlinEx

import android.content.pm.ActivityInfo
import dev.utils.app.AppUtils

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-31 20:49

 */
val ActivityInfo.label
    get() = this.loadLabel(AppUtils.getPackageManager())