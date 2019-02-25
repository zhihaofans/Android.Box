package com.zhihaofans.androidbox.view

import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import com.google.android.material.tabs.TabLayout
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.removeAllItems
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.mod.FeedMod
import com.zhihaofans.androidbox.util.*
import dev.utils.app.DialogUtils
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.*


class FeedActivity : AppCompatActivity() {
    private var nowTabPosition = 0
    private val newsBox = FeedMod.News()
    private val appBox = FeedMod.App()
    private var clipboardUtil: ClipboardUtil? = null
    private val notificationUtil = NotificationUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(toolbar_feed)
        init()
        fab_feed.setOnClickListener {
            when (nowTabPosition) {
                0 -> {
                    val newsCache = newsBox.getCache()
                    if (newsCache == null) {
                        selector("", mutableListOf(getString(R.string.text_select_site), getString(R.string.text_refresh))) { _, pos ->
                            when (pos) {
                                0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(2))
                                1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))
                            }
                        }
                    } else {
                        val menu = mutableListOf(
                                getString(R.string.text_select_site),
                                getString(R.string.text_refresh),
                                getString(R.string.text_next_page)
                        )
                        if (newsBox.page > 1) {
                            menu.add(2, getString(R.string.text_previous_page))
                        }
                        selector("", menu) { _, pos ->
                            when (pos) {
                                0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(2))
                                1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))
                                2 -> {
                                    val pageTemp = newsCache.nowPage + if (menu.size == 4) -1 else 1
                                    Logger.d("pageTemp:$pageTemp")
                                    this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, pageTemp))
                                }
                                3 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, newsCache.nowPage + 1))
                            }
                        }
                    }
                }
                1 -> {
                    val fabAction = mutableListOf("添加订阅", getString(R.string.text_delete), "数据库操作")
                    selector(getString(R.string.title_activity_app_down), fabAction) { _: DialogInterface, i: Int ->
                        when (i) {
                            0 -> updateFeed(1, FeedMod.App.Update(1))
                            1 -> updateFeed(1, FeedMod.App.Update(2))
                            2 -> {
                                selector("数据库", listOf(getString(R.string.text_import), getString(R.string.text_export))) { _: DialogInterface, ii: Int ->
                                    when (ii) {
                                        0 -> updateFeed(1, FeedMod.App.Update(3))
                                        1 -> updateFeed(1, FeedMod.App.Update(4))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                title = tab.text ?: getString(R.string.text_feed)
                if (nowTabPosition != tab.position) {
                    nowTabPosition = tab.position
                    if (nowTabPosition == 0) initFeed(nowTabPosition)

                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
        })
    }


    private fun init() {
        clipboardUtil = ClipboardUtil(this)
        notificationUtil.init(this)
        newsBox.init(this@FeedActivity)
        appBox.init(this@FeedActivity)
        snackbar(coordinatorLayout_feed, "初始化中")
        initFeed(0)
    }

    private fun initFeed(index: Int, data: Any? = null, noCache: Boolean = false) {
        val loadingProgressBar = DialogUtils.createProgressDialog(this, "下载中...", "Please wait a bit…")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        listView_feed.removeAllItems()
        when (index) {
            0 -> {
                var cache = newsBox.getCache()
                if (cache == null || noCache) {
                    val newsInit: FeedMod.News.Init = if (data == null) {
                        val thisSite = newsBox.getSiteList()[0]
                        val channelList = newsBox.getChannel(thisSite.id)
                        FeedMod.News.Init(thisSite.id, channelList[0].id, 1)
                    } else {
                        data as FeedMod.News.Init
                    }
                    doAsync {
                        Logger.d("newsInit")
                        cache = newsBox.getCache(newsInit.siteId, newsInit.channelId, newsInit.page)
                        uiThread {
                            if (cache == null) {
                                loadingProgressBar.dismiss()
                                snackbar(coordinatorLayout_feed, "空白数据")
                            } else {
                                initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { i -> i.title }, cache!!.newsList.map { i -> i.url }))
                            }
                        }
                    }
                } else {
                    initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { i -> i.title }, cache!!.newsList.map { i -> i.url }))
                }
            }
            /*
        1 -> {
            val appFeeds = appBox.initAppList()
            try {
                if (appFeeds.size == 0) {
                    Logger.d("appFeeds.size=0")
                    XPopup.get(this).dismiss()
                    snackbar("列表空白")
                } else {
                    initListView(FeedMod.App.AppList(appFeeds))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                XPopup.get(this).dismiss()
                snackbar("应用下载初始化失败，请尝试清空应用数据")
            }
            }
            */
            else -> {
                listView_feed.removeAllItems()
                loadingProgressBar.dismiss()
                snackbar(coordinatorLayout_feed, "不支持")
            }
        }
    }

    private fun updateFeed(index: Int, data: Any) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "下载中...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        when (index) {
            0 -> {
                val update = data as FeedMod.News.Update
                var cache = newsBox.getCache()
                when (update.type) {
                    0 -> {
                        doAsync {
                            if (cache == null) {
                                snackbar(coordinatorLayout_feed, "空白订阅数据")
                            } else {

                                cache = newsBox.refreshCache()
                                uiThread {
                                    if (cache == null) {
                                        loadingProgressBar.dismiss()
                                        snackbar(coordinatorLayout_feed, "空白数据")
                                    } else {
                                        initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        val page = update.data as Int
                        Logger.d("updateFeed->page:$page")
                        doAsync {
                            if (cache == null) {
                                snackbar(coordinatorLayout_feed, "空白订阅数据")
                            } else {
                                cache = newsBox.changePage(page)
                                uiThread {
                                    if (cache == null) {
                                        loadingProgressBar.dismiss()
                                        snackbar(coordinatorLayout_feed, "空白数据")
                                    } else {
                                        listView_feed.removeAllItems()
                                        initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        loadingProgressBar.dismiss()
                        val siteList = newsBox.getSiteList()
                        selector(getString(R.string.text_select_site), siteList.map { it.name }) { _, i ->
                            val siteId = siteList[i].id
                            val channelList = newsBox.getChannel(siteId)
                            var channelId = channelList[0].id
                            if (channelList.size > 1) {
                                selector(getString(R.string.text_channel), channelList.map { it.name }) { _, channelIndex ->
                                    channelId = channelList[channelIndex].id
                                    Logger.d("ChangeSite:$siteId/$channelId")
                                    initFeed(0, FeedMod.News.Init(siteId, channelId, 1), true)
                                }
                            } else {
                                Logger.d("ChangeSite:$siteId/$channelId")
                                initFeed(0, FeedMod.News.Init(siteId, channelId, 1), true)
                            }
                        }
                    }
                    else -> {
                        loadingProgressBar.dismiss()
                    }
                }
            }
            1 -> {
                loadingProgressBar.dismiss()
                // TODO:App updateFeed
            }
            else -> loadingProgressBar.dismiss()

        }
    }

    private fun initListView(loadingProgressBar: ProgressDialog, data: Any) {
        listView_feed.removeAllItems()
        when (tabLayout.selectedTabPosition) {
            0 -> {
                val newsList = data as FeedMod.News.ListView
                listView_feed.init(this@FeedActivity, newsList.titleList)
                listView_feed.setOnItemClickListener { _, _, index, _ ->
                    SystemUtil.browse(this@FeedActivity, newsList.urlList[index], newsList.titleList[index])
                }
                loadingProgressBar.dismiss()
            }
            1 -> {
                loadingProgressBar.dismiss()
                //TODO:Feed -> App
                doAsync { }
                val appFeeds = (data as FeedMod.App.AppList).data
                listView_feed.init(this@FeedActivity, appFeeds.map { it.name })
                listView_feed.setOnItemClickListener { _, _, index, _ ->
                    val clickedApp = appFeeds[index]
                    alert {
                        title = clickedApp.name
                        message = getString(R.string.text_app_version) + ": ${clickedApp.version}\n" + getString(R.string.text_app_lastupdatetime) + ": ${clickedApp.updateTime}"
                        negativeButton(R.string.text_download) {
                            val acts = mutableListOf("浏览器打开", "下载")
                            if (clickedApp.fileList.isEmpty()) acts.removeAt(1)
                            selector("", acts) { _, act: Int ->
                                when (act) {
                                    0 -> browse(clickedApp.webUrl)
                                    1 -> {
                                        XXPermissions.with(this@FeedActivity)
                                                .permission(Permission.Group.STORAGE)
                                                .request(object : OnPermission {
                                                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                                                        if (isAll) {
                                                            val fileList = clickedApp.fileList
                                                            selector(getString(R.string.text_download), fileList.map { it.name }) { _: DialogInterface, fileIndex: Int ->
                                                                val file = fileList[fileIndex]
                                                                val fileExt = ".apk"
                                                                val fileNameList = mutableListOf(
                                                                        clickedApp.name + fileExt,
                                                                        clickedApp.name + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + file.name,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + "_" + file.name,
                                                                        clickedApp.packageName + fileExt,
                                                                        clickedApp.packageName + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.packageName + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.packageName + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.id_one + "_" +
                                                                                (if (clickedApp.id_two == null) "" else clickedApp.id_two + "_") + clickedApp.version + "_" + file.name
                                                                ).map {
                                                                    if (it.endsWith(".apk")) {
                                                                        it
                                                                    } else {
                                                                        "$it.apk"
                                                                    }
                                                                }
                                                                selector("文件名格式", fileNameList) { _, fileNameIndex: Int ->
                                                                    var fileName = fileNameList[fileNameIndex]
                                                                    if (!fileName.endsWith(".apk")) fileName += ".apk"
                                                                    alert {
                                                                        title = getString(R.string.text_download) + "?"
                                                                        customView {
                                                                            verticalLayout {
                                                                                val input = editText(fileName)
                                                                                positiveButton(R.string.text_download) {
                                                                                    val url = file.url
                                                                                    downloadFile(url, input.text.toString())
                                                                                }
                                                                            }
                                                                        }
                                                                    }.show()

                                                                }
                                                            }
                                                        } else {
                                                            snackbar("未授权储存权限，无法下载")
                                                        }
                                                    }

                                                    override fun noPermission(denied: List<String>, quick: Boolean) {
                                                        snackbar("未授权储存权限，无法下载")
                                                    }
                                                }
                                                )
                                    }
                                }
                            }

                        }
                        positiveButton(R.string.text_check_update) {
                            updateFeed(1, FeedMod.App.Update(0))
                        }
                    }.show()
                }
                loadingProgressBar.dismiss()
            }
        }
        snackbar(coordinatorLayout_feed, "加载完毕")
    }

    private fun downloadFile(url: String, fileName: String) {
        when {
            url.isEmpty() -> snackbar("下载失败：下载地址空白")
            fileName.isEmpty() -> snackbar("下载失败：文件名空白")
            else -> {
                val filePath = appBox.getSavePath() + fileName
                val notification = notificationUtil.createProgress("正在下载", fileName)
                FileUtil.download(url, filePath, object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {

                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        if (totalBytes > 0 && notification !== null) {
                            notificationUtil.setProgressNotificationLength(notification, soFarBytes, totalBytes)
                        }
                    }

                    override fun blockComplete(task: BaseDownloadTask?) {}

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                        coordinatorLayout_feed.snackbar("Retry,Times: $retryingTimes")
                        if (notification !== null) {
                            notificationUtil.delete(notification.notificationId)
                        }
                    }

                    override fun completed(task: BaseDownloadTask) {
                        if (notification !== null) notificationUtil.delete(notification.notificationId)
                        val stackBuilder = TaskStackBuilder.create(this@FeedActivity)
                        val resultPendingIntent = stackBuilder.apply {
                            addNextIntent(IntentUtil.getInstallIntent(this@FeedActivity, task.targetFilePath))
                        }.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                        if (resultPendingIntent == null) {
                            notificationUtil.create("错误！", "创建安装通知失败")
                        } else {
                            notificationUtil.createIntent("下载完毕", "点击安装", resultPendingIntent, true)
                        }
                        alert {
                            title = "下载完成"
                            message = "文件路径:" + task.targetFilePath
                            positiveButton(R.string.text_copy) {
                                clipboardUtil?.copy(task.targetFilePath)
                                toast("复制成功")
                            }
                            negativeButton(R.string.text_open) {
                                try {
                                    FileUtil.installApk1(this@FeedActivity, task.targetFilePath)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    snackbar("安装失败")
                                }
                            }
                        }.show()

                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        coordinatorLayout_feed.snackbar("paused")
                        if (notification !== null) {
                            notificationUtil.delete(notification.notificationId)
                        }

                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        e.printStackTrace()
                        Logger.d("Download error\nfileName:" + task.filename)
                        if (notification !== null) {
                            notificationUtil.delete(notification.notificationId)
                        }
                        notificationUtil.create("下载失败", task.filename)
                    }

                    override fun warn(task: BaseDownloadTask) {}
                })
            }
        }
    }

    private fun snackbar(msg: String) {
        coordinatorLayout_feed.snackbar(msg)
    }
}

