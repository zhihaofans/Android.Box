package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.removeAllItems
import com.zhihaofans.androidbox.mod.FeedMod
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.snackbar
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick


class FeedActivity : AppCompatActivity() {
    private var nowTabPosition = 0
    private val newsBox = FeedMod.News()
    private val appBox = FeedMod.App()
    private val sysUtil = SystemUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(toolbar)
        init()
        fab.setOnClickListener { view ->
            when (nowTabPosition) {
                0 -> {
                    val newsCache = newsBox.getCache()
                    if (newsCache == null) {
                        snackbar(coordinatorLayout_feed, "空白订阅数据")
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
                            0 -> updateFeed(1, FeedMod.App.Update(1)) // Add
                            1 -> updateFeed(1, FeedMod.App.Update(2)) // Delete
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
            override fun onTabSelected(tab: TabLayout.Tab) {
                title = tab.text ?: getString(R.string.text_feed)
                if (nowTabPosition != tab.position) {
                    nowTabPosition = tab.position
                    initFeed(nowTabPosition)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun init() {
        newsBox.init(this@FeedActivity)
        appBox.init(this@FeedActivity)
        snackbar(coordinatorLayout_feed, "初始化中")
        initFeed(0)
    }

    private fun initFeed(index: Int, data: Any? = null, noCache: Boolean = false) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
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
                        uiThread { _ ->
                            if (cache == null) {
                                loadingProgressBar.dismiss()
                                snackbar(coordinatorLayout_feed, "空白数据")
                            } else {
                                initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                            }
                        }
                    }
                } else {
                    initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                }
            }
            1 -> {
                val appFeeds = appBox.initAppList()
                try {
                    if (appFeeds.size == 0) {
                        Logger.d("appFeeds.size=0")
                        loadingProgressBar.dismiss()
                        snackbar("列表空白")
                    } else {
                        initListView(loadingProgressBar, FeedMod.App.AppList(appFeeds))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    loadingProgressBar.dismiss()
                    snackbar("应用下载初始化失败，请尝试清空应用数据")
                }
            }
            else -> {
                listView_feed.removeAllItems()
                loadingProgressBar.dismiss()
                snackbar(coordinatorLayout_feed, "不支持")
            }
        }
    }

    private fun updateFeed(index: Int, data: Any) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        when (index) {
            0 -> {
                val update = data as FeedMod.News.Update
                var cache = newsBox.getCache()
                if (cache == null) {
                    snackbar(coordinatorLayout_feed, "空白订阅数据")
                } else {
                    when (update.type) {
                        0 -> {
                            doAsync {
                                cache = newsBox.refreshCache()
                                uiThread { _ ->
                                    if (cache == null) {
                                        loadingProgressBar.dismiss()
                                        snackbar(coordinatorLayout_feed, "空白数据")
                                    } else {
                                        initListView(loadingProgressBar, newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                                    }
                                }
                            }
                        }
                        1 -> {
                            //TODO:更改 page
                            val page = update.data as Int
                            Logger.d("updateFeed->page:$page")
                            doAsync {
                                cache = newsBox.changePage(page)
                                uiThread { _ ->
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
                        2 -> {
                            //TODO:更改 site
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
                        else -> loadingProgressBar.dismiss()
                    }
                }
            }
            else -> {
                loadingProgressBar.dismiss()
            }
        }
    }

    private fun initListView(progressDialog: ProgressDialog, data: Any) {
        listView_feed.removeAllItems()
        when (tabLayout.selectedTabPosition) {
            0 -> {
                val newsList = data as FeedMod.News.ListView
                listView_feed.init(this@FeedActivity, newsList.titleList)
                listView_feed.onItemClick { _, _, index, _ ->
                    sysUtil.browse(this@FeedActivity, newsList.urlList[index], newsList.titleList[index])
                }
            }
            1 -> {
                doAsync { }
                val appFeeds = (data as FeedMod.App.AppList).data
                listView_feed.init(this@FeedActivity, appFeeds.map { it.name })
                listView_feed.onItemClick { _, _, index, _ ->
                    val clickedApp = appFeeds[index]
                    alert {
                        title = clickedApp.name
                        message = getString(R.string.text_app_version) + ": ${clickedApp.version}\n" + getString(R.string.text_app_lastupdatetime) + ": ${clickedApp.updateTime}"
                        negativeButton(R.string.text_download) { _ ->
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
            }
        }
        progressDialog.dismiss()
        snackbar(coordinatorLayout_feed, "加载完毕")
    }

    private fun downloadFile(url: String, fileName: String) {
        when {
            url.isEmpty() -> snackbar("下载失败：下载地址空白")
            fileName.isEmpty() -> snackbar("下载失败：文件名空白")
            else -> {
                val filePath = appBox.getSavePath() + fileName
                val loadingProgressBarDownload = progressDialog(message = filePath, title = "Downloading...")
                loadingProgressBarDownload.setCancelable(false)
                loadingProgressBarDownload.setCanceledOnTouchOutside(false)
                loadingProgressBarDownload.show()
                sysUtil.download(url, filePath, object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        loadingProgressBarDownload.setTitle("Pending...")
                    }

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                        loadingProgressBarDownload.setTitle("Connected")

                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        if (totalBytes > 0) {
                            loadingProgressBarDownload.max = totalBytes
                            loadingProgressBarDownload.progress = soFarBytes
                        } else {
                            loadingProgressBarDownload.max = 0
                            loadingProgressBarDownload.progress = 1
                        }
                    }

                    override fun blockComplete(task: BaseDownloadTask?) {}

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                        loadingProgressBarDownload.setTitle("Retry")
                        loadingProgressBarDownload.setMessage("Times: $retryingTimes")
                    }

                    override fun completed(task: BaseDownloadTask) {
                        loadingProgressBarDownload.dismiss()
                        alert {
                            title = "下载完成"
                            message = "文件路径:" + task.targetFilePath
                            positiveButton(R.string.text_copy) {
                                ClipboardUtils.copy(this@FeedActivity, task.targetFilePath)
                                toast("复制成功")
                            }
                            negativeButton(R.string.text_open) {
                            }
                        }.show()

                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        loadingProgressBarDownload.dismiss()
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        e.printStackTrace()
                        Logger.d("Download error\nfileName:" + task.filename)
                        loadingProgressBarDownload.dismiss()
                        snackbar("下载失败")
                    }

                    override fun warn(task: BaseDownloadTask) {}
                })
            }
        }
    }

    private fun snackbar(msg: String) {
        snackbar(coordinatorLayout_feed, msg)
    }
}

