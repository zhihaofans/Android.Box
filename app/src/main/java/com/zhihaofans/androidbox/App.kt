package com.zhihaofans.androidbox

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.haoge.easyandroid.EasyAndroid
import com.maning.librarycrashmonitor.MCrashMonitor
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.zhihaofans.androidbox.util.SystemUtil


/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    private val TAG = "com.zhihaofans.androidbox"
    private val sysUtil = SystemUtil()
    private var isDebug = false
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        EasyAndroid.init(applicationContext)
        isDebug = sysUtil.isApkDebugable(this)
        Logger.d("Debug:$isDebug")
        MCrashMonitor.init(this, isDebug) { file ->
            //可以在这里保存标识，下次再次进入把日志发送给服务器
            if (isDebug) Logger.d(TAG + "CrashMonitor回调:" + file.absolutePath)
            MCrashMonitor.startCrashShowPage(this)
        }
        //新的更新方式
        buglyInit()
        Fresco.initialize(this)
        //FileDownloader.setupOnApplicationOnCreate(this)
    }

    private fun buglyInit() {
        Bugly.init(applicationContext, "a71e8c60bc", isDebug) //初始化
        Beta.enableNotification = true //设置在通知栏显示下载进度
        Beta.autoDownloadOnWifi = true //设置Wifi下自动下载
        Beta.enableHotfix = false //关闭热更新能力
    }

}