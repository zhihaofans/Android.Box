package com.zhihaofans.androidbox

import android.app.Application
import android.content.Context
import com.didichuxing.doraemonkit.DoraemonKit
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
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
        ZLibrary.init(this)
        SpiderMan.init(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("Debug:${AppUtil.isDebug()}")
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
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context).setDrawableSize(20f)
        }
        DoraemonKit.install(this)
    }
}


