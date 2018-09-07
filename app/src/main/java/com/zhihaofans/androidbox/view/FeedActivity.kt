package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.removeAllItems
import com.zhihaofans.androidbox.mod.FeedMod
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.snackbar
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.uiThread


class FeedActivity : AppCompatActivity() {
    private var nowTabPosition = 0
    private val newsBox = FeedMod.News()
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
        snackbar(coordinatorLayout_feed, "初始化中")
        initFeed(0)
    }

    private fun initFeed(index: Int, data: Any? = null, noCache: Boolean = false) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
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
                                initListView(loadingProgressBar, cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url })
                            }
                        }
                    }
                } else {
                    initListView(loadingProgressBar, cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url })
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
                                        initListView(loadingProgressBar, cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url })
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
                                        initListView(loadingProgressBar, cache!!.newsList.map { it.title }, cache!!.newsList.map { it.url })
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

    private fun initListView(progressDialog: ProgressDialog, titleList: List<String>, urlList: List<String>) {
        listView_feed.removeAllItems()
        listView_feed.init(this@FeedActivity, titleList)
        listView_feed.onItemClick { _, _, index, _ ->
            sysUtil.browse(this@FeedActivity, urlList[index], titleList[index])
        }
        progressDialog.dismiss()
        snackbar(coordinatorLayout_feed, "加载完毕")
    }
}

