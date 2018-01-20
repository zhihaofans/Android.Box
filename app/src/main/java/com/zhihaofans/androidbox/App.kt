package com.zhihaofans.androidbox

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())

    }
}