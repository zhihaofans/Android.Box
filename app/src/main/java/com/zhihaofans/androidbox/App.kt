package com.zhihaofans.androidbox

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.maning.librarycrashmonitor.MCrashMonitor
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.util.SystemUtil
import dev.DevUtils
import io.paperdb.Paper


/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    private var isDebug = false
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        isDebug = SystemUtil.isApkDebugable(this)
        Logger.d("Debug:$isDebug")
        MCrashMonitor.init(this, isDebug) { file ->
            //可以在这里保存标识，下次再次进入把日志发送给服务器
            if (isDebug) Logger.d("应用发生了错误，CrashMonitor回调:" + file.absolutePath)
        }
        Paper.init(this)
        Fresco.initialize(this)
        FileDownloader.setupOnApplicationOnCreate(this)
        DevUtils.init(applicationContext)
    }


}