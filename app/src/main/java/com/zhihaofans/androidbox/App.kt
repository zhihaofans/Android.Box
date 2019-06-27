package com.zhihaofans.androidbox

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.lxj.androidktx.AndroidKtxConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.simple.spiderman.SpiderMan
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
        ZLibrary.init(this)

        // SpiderMan
        SpiderMan.init(this)

        // Logger
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("Debug:${AppUtil.isDebug()}")

        // PaperDB
        Paper.init(this)

        // Fresco
        Fresco.initialize(this)

        // FileDownloader
        FileDownloader.setupOnApplicationOnCreate(this)

        // DevUtils
        DevUtils.init(this)

        // RXTool
        RxTool.init(this)

        // XUI
        XUI.init(this)
        XUI.debug(true)

        // SmartRefreshLayout
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> ClassicsHeader(context) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context).setDrawableSize(20f)
        }
        // DoraemonKit
        DoraemonKit.install(this)

        // AndroidKTX
        AndroidKtxConfig.init(this)
    }
}


