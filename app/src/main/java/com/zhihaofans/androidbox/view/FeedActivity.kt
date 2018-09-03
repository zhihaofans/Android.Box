package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
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
                        selector("", mutableListOf(getString(R.string.text_refresh))) { _, pos ->
                            when (pos) {
                                0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))
                            }
                        }
                    } else {
                        val menu = mutableListOf(
                                getString(R.string.text_refresh),
                                getString(R.string.text_next_page)
                        )
                        if (newsBox.page > 1) {
                            menu.add(1, getString(R.string.text_previous_page))
                        }
                        selector("", menu) { _, pos ->
                            if (menu.size == 3) {
                                when (pos) {
                                    1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, newsCache.nowPage--))
                                }
                            } else {
                                when (pos) {
                                    0 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(0))
                                    menu.size - 1 -> this@FeedActivity.updateFeed(0, FeedMod.News.Update(1, newsCache.nowPage++))
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
        snackbar(coordinatorLayout_feed, "初始化中")
        initFeed(0)
    }

    private fun initFeed(index: Int) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        when (index) {
            0 -> {
                var cache = newsBox.getCache()
                if (cache == null) {
                    val siteList = newsBox.getSiteList()
                    val thisSite = siteList[0]
                    doAsync {
                        cache = newsBox.getCache(thisSite.id, newsBox.getChannel(thisSite.id)[0].id, 1)
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

                        }
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

