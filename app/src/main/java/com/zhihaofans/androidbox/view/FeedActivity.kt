package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.FeedMod
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.mod.UrlRedirectMod
import com.zhihaofans.androidbox.util.LogUtil
import com.zhihaofans.androidbox.util.NotificationUtil
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.removeAllItems
import io.zhihao.library.android.kotlinEx.snackbar
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.selector
import org.jetbrains.anko.uiThread


class FeedActivity : AppCompatActivity() {
    private var nowTabPosition = 0
    private val newsBox = FeedMod.News()
    private val appBox = FeedMod.App()

    private val notificationUtil = NotificationUtil()
    private var firstRun = true
    private var manualRefresh = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(toolbar_feed)
        init()
        /*
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
        */
    }


    private fun init() {
        notificationUtil.init(this)
        newsBox.init(this@FeedActivity)
        appBox.init(this@FeedActivity)
        snackbar(coordinatorLayout_feed, "初始化中")
        refreshLayout.setDisableContentWhenRefresh(true)
        refreshLayout.setDisableContentWhenLoading(true)
        refreshLayout.setOnRefreshListener {
            if (!manualRefresh) this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))
        }
        refreshLayout.setOnLoadMoreListener { mRefreshlayout ->
            val newsCache = newsBox.getCache()
            if (newsCache == null) {
                mRefreshlayout.finishLoadMore(false)
            } else {
                this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, newsCache.nowPage + 1))
            }
        }
        fab_feed.setOnClickListener {
            when (nowTabPosition) {
                0 -> {
                    val newsCache = newsBox.getCache()
                    val menu = mutableListOf(
                            getString(R.string.text_select_site),
                            getString(R.string.text_refresh)
                    )
                    if (newsCache == null) {
                        selector("", menu) { _, pos ->
                            when (pos) {
                                0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(2))//选择站点
                                1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))//刷新
                            }
                        }
                    } else {
                        if (newsCache.onlyOnePage) {
                            refreshLayout.setNoMoreData(true)
                        } else {
                            menu.add(getString(R.string.text_next_page))
                            if (newsBox.page > 1) {
                                menu.add(2, getString(R.string.text_previous_page))
                            }
                            refreshLayout.setNoMoreData(false)
                        }
                        selector("", menu) { _, pos ->
                            when (pos) {
                                0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(2))//选择站点
                                1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))//刷新
                                2 -> {
                                    val pageTemp = newsCache.nowPage + if (menu.size == 4) -1 else 1
                                    LogUtil.d("pageTemp:$pageTemp")
                                    this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, pageTemp))//上一页(非第一页时)、下一页
                                }
                                3 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, newsCache.nowPage + 1))//下一页
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
        refreshLayout.autoRefresh()
    }

    private fun initFeed(index: Int, data: Any? = null, noCache: Boolean = false) {
        listView_feed.removeAllItems()
        when (index) {
            0 -> {
                firstRun = false
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
                        LogUtil.d("newsInit")
                        cache = newsBox.getCache(newsInit.siteId, newsInit.channelId, newsInit.page)
                        uiThread {
                            if (cache == null) {
                                snackbar(coordinatorLayout_feed, "空白数据")
                                refreshLayout.finishRefresh(500, false)//传入false表示刷新失败
                                refreshLayout.finishLoadMore(false)
                            } else {
                                initListView(newsBox.getListView(cache!!.newsList.map { i -> i.title }, cache!!.newsList.map { i -> i.url }))
                            }
                        }
                    }
                } else {
                    initListView(newsBox.getListView(cache!!.newsList.map { i -> i.title }, cache!!.newsList.map { i -> i.url }))
                }
            }
            /*
        1 -> {
            val appFeeds = appBox.initAppList()
            try {
                if (appFeeds.size == 0) {
                    LogUtil.d("appFeeds.size=0")
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
                snackbar(coordinatorLayout_feed, "不支持")
                refreshLayout.finishRefresh(500, false)//传入false表示刷新失败
                refreshLayout.finishLoadMore(false)
            }
        }
    }

    private fun updateFeed(index: Int, data: Any) {
        when (index) {
            0 -> {
                if (firstRun) {
                    initFeed(0)//第一次加载时跳转至初始化
                } else {
                    val update = data as FeedMod.News.Update
                    var cache = newsBox.getCache()
                    when (update.type) {
                        0 -> {
                            doAsync {
                                if (cache == null) {
                                    snackbar(coordinatorLayout_feed, "空白订阅数据")
                                    refreshLayout.finishRefresh(500, false)
                                    refreshLayout.finishLoadMore(false)
                                } else {
                                    cache = newsBox.refreshCache()
                                    uiThread {
                                        if (cache == null) {
                                            snackbar(coordinatorLayout_feed, "空白数据")
                                            refreshLayout.finishRefresh(500, false)
                                            refreshLayout.finishLoadMore(false)
                                        } else {
                                            initListView(newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            manualRefresh = true
                            refreshLayout.autoRefresh()
                            val page = update.data as Int
                            LogUtil.d("updateFeed->page:$page")
                            doAsync {
                                if (cache == null) {
                                    snackbar(coordinatorLayout_feed, "空白订阅数据")
                                    refreshLayout.finishRefresh(500, false)
                                    refreshLayout.finishLoadMore(false)
                                } else {
                                    cache = newsBox.changePage(page)
                                    uiThread {
                                        if (cache == null) {
                                            snackbar(coordinatorLayout_feed, "空白数据")
                                            refreshLayout.finishRefresh(500, false)
                                            refreshLayout.finishLoadMore(false)
                                        } else {
                                            listView_feed.removeAllItems()
                                            initListView(newsBox.getListView(cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url }))
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            val siteList = newsBox.getSiteList()
                            selector(getString(R.string.text_select_site), siteList.map { it.name }) { _, i ->
                                val siteId = siteList[i].id
                                val channelList = newsBox.getChannel(siteId)
                                var channelId = channelList[0].id
                                if (channelList.size > 1) {
                                    selector(getString(R.string.text_channel), channelList.map { it.name }) { _, channelIndex ->
                                        channelId = channelList[channelIndex].id
                                        LogUtil.d("ChangeSite:$siteId/$channelId")
                                        manualRefresh = true
                                        refreshLayout.autoRefresh()
                                        initFeed(0, FeedMod.News.Init(siteId, channelId, 1), true)
                                    }
                                } else {
                                    manualRefresh = true
                                    refreshLayout.autoRefresh()
                                    LogUtil.d("ChangeSite:$siteId/$channelId")
                                    initFeed(0, FeedMod.News.Init(siteId, channelId, 1), true)
                                }
                            }
                        }
                        else -> {
                            refreshLayout.finishRefresh(500, false)
                            refreshLayout.finishLoadMore(false)
                        }
                    }
                }
            }
            1 -> {
                refreshLayout.finishRefresh(500, false)
                refreshLayout.finishLoadMore(false)
                // TODO:App updateFeed
            }
            else -> {

                refreshLayout.finishRefresh(500, false)
                refreshLayout.finishLoadMore(false)
            }

        }
    }

    private fun initListView(data: Any) {
        listView_feed.removeAllItems()
        manualRefresh = false
        val mNumber = 0
        when (mNumber) {
            0 -> {
                val newsList = data as FeedMod.News.ListView
                listView_feed.init(newsList.titleList)
                listView_feed.setOnItemClickListener { _, _, index, _ ->
                    val newUrl = UrlRedirectMod.urlRedirect(newsList.urlList[index]).toString()
                    if (OtherAppMod.browserByLynket(newUrl)) {
                        ToastUtil.success("启动成功")
                    } else {
                        ToastUtil.error("启动失败")
                        //browseWeb(newUrl, true)
                        SystemUtil.browse(this@FeedActivity, newUrl, newsList.titleList[index])
                    }
                }
                refreshLayout.finishRefresh(500, true)
                refreshLayout.finishLoadMore(0, true, newsBox.getCache()?.onlyOnePage ?: true)
            }
            /*1 -> {
                refreshLayout.finishRefresh(500, true)
                refreshLayout.finishLoadMore(false)
                //TODO:Feed -> App
                val appFeeds = (data as FeedMod.App.AppList).data
                listView_feed.init(this@FeedActivity, appFeeds.map { it.name })
                refreshLayout.finishRefresh(500, true)
                refreshLayout.finishLoadMore(500, true, true)
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

            }*/
        }
        coordinatorLayout_feed.snackbar("加载完毕")
    }
}

