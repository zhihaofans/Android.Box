package com.zhihaofans.androidbox

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
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
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
            override fun createRefreshHeader(context: Context, layout: RefreshLayout): RefreshHeader {
                return ClassicsHeader(context)
            }
        })
    }
}


