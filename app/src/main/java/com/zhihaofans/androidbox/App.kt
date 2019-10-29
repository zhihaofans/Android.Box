package com.zhihaofans.androidbox

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit
import com.downloader.PRDownloader
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.lxj.androidktx.AndroidKtxConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.vondear.rxtool.RxTool
import com.xuexiang.xui.XUI
import dev.DevUtils
import io.paperdb.Paper
import io.zhihao.library.android.ZLibrary
import io.zhihao.library.android.util.AppUtil


/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // AndroidLibrary
        ZLibrary.init(applicationContext)

        // Logger
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("Debug:${AppUtil.isDebug()}")

        // PaperDB
        Paper.init(applicationContext)

        // Fresco
        Fresco.initialize(applicationContext)

        // FileDownloader
        FileDownloader.setupOnApplicationOnCreate(this)

        // DevUtils
        DevUtils.init(applicationContext)

        // RXTool
        RxTool.init(applicationContext)

        // XUI
        XUI.init(this)
        XUI.debug(true)

        // DoraemonKit
        DoraemonKit.install(this)

        // AndroidKTX
        AndroidKtxConfig.init(applicationContext)

        // PRDownloader
        PRDownloader.initialize(applicationContext)
    }
}


