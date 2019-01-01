package com.zhihaofans.androidbox

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.maning.librarycrashmonitor.MCrashMonitor
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.smtt.sdk.QbSdk
import com.zhihaofans.androidbox.util.SystemUtil
import dev.DevUtils
import dev.utils.app.AppUtils
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
        initX5WebView()
    }

    private fun initX5WebView() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val cb = object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Logger.d(" onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {}
        }
        QbSdk.setDownloadWithoutWifi(!AppUtils.isAppDebug())

        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, cb)
    }

}