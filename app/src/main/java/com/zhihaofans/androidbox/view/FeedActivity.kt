package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
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
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
        initFeed(0, true)
    }

    private fun initFeed(index: Int, firstRun: Boolean = false) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        when (index) {
            0 -> {
                var cache = newsBox.getCache()
                if (cache == null) {
                    var siteIndex = 0
                    var channelIndex = 0
                    var page = 0
                    val siteList = newsBox.getSiteList()
                    if (!firstRun) {
                        //TODO:选择新闻站点
                    }
                    val thisSite = siteList[siteIndex]
                    if (!firstRun) {
                        //TODO:选择新闻站点的频道
                    }
                    val channels = newsBox.getChannel(thisSite.id)
                    doAsync {
                        cache = newsBox.getCache(thisSite.id, channels[channelIndex].id, page)
                        uiThread { _ ->
                            if (cache == null) {
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

