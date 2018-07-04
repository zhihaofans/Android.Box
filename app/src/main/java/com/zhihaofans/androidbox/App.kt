package com.zhihaofans.androidbox

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.simple.spiderman.SpiderMan
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.wx.android.common.util.SharedPreferencesUtils


/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        SpiderMan.getInstance()
                .init(this)
                //设置是否捕获异常，不弹出崩溃框
                .setEnable(true)
                //设置是否显示崩溃信息展示页面
                .showCrashMessage(true)
                //是否回调异常信息，友盟等第三方崩溃信息收集平台会用到,
                .setOnCrashListener { t, ex, model ->
                    //CrashModel 崩溃信息记录，包含设备信息
                    ex.printStackTrace()
                }
        Logger.addLogAdapter(AndroidLogAdapter())
        SharedPreferencesUtils.init(mContext)
        //新的更新方式
        buglyInit()
        Fresco.initialize(this)
        FileDownloader.setupOnApplicationOnCreate(this)
    }

    private fun buglyInit() {
        Bugly.init(mContext, "a71e8c60bc", true) //初始化
        Beta.enableNotification = true //设置在通知栏显示下载进度
        Beta.autoDownloadOnWifi = true //设置Wifi下自动下载
        Beta.enableHotfix = false //关闭热更新能力
    }

    fun getContext(): Context {
        return mContext!!
    }
}