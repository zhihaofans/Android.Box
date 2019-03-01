package com.zhihaofans.androidbox

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.simple.spiderman.SpiderMan
import com.vondear.rxtool.RxTool
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.util.SystemUtil
import dev.DevUtils
import io.paperdb.Paper


/**
 *
 * @author zhihaofans
 * @date 2018/1/9
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SpiderMan.init(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        val isDebug = SystemUtil.isApkDebugable(this)
        Logger.d("Debug:$isDebug")
        Paper.init(this)
        Fresco.initialize(this)
        FileDownloader.setupOnApplicationOnCreate(this)
        DevUtils.init(this)
        RxTool.init(this)
        XUI.init(this)
        XUI.debug(true)
    }


}