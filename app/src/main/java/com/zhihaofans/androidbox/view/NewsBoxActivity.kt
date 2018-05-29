package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import com.orhanobut.logger.Logger
import com.wx.android.common.util.SharedPreferencesUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.NewsBoxMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_news_box.*
import kotlinx.android.synthetic.main.content_news_box.*
import okhttp3.CacheControl
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.uiThread


class NewsBoxActivity : AppCompatActivity() {
    private val sysUtil = SystemUtil()
    private val newsBoxMod = NewsBoxMod()
    private val sites = NewsBoxMod.sites(this@NewsBoxActivity)
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    private var nowPage = 1
    // 旧变量
    private var newsSites = mutableListOf<List<String>>()
    private var nowSite: List<String> = listOf()
    private var lastSiteId: String? = null
    private var lastSiteIndex = 0
    // 新变量
    private var siteChannelId = ""
    private var siteId = ""
    private var siteName = ""
    private var channelName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_box)
        setSupportActionBar(toolbar_newsbox)
        newsBoxMod.setContext(this@NewsBoxActivity)
        /*
        newsSites = newsBoxMod.sites()
        nowSite = newsSites[lastSiteIndex]
        SharedPreferencesUtils.init(this)
        lastSiteId = SharedPreferencesUtils.getString("NewsBoxSetting", "LastSiteId")
        if (lastSiteId.isNullOrEmpty()) {
            lastSiteIndex = SharedPreferencesUtils.getInt("NewsBoxSetting", "LastSiteIndex")
            lastSiteId = nowSite[0]
        }
        nowSite = newsSites[lastSiteIndex]
        */
        siteId = SharedPreferencesUtils.getString("NewsBox", "LAST_SITE_ID") ?: sites.getSiteList()[0]["id"].toString()
        siteChannelId = SharedPreferencesUtils.getString("NewsBox", "LAST_SITE_CHANNEL_ID") ?: sites.getSiteChannelList(siteId)!![0]["channelId"]!!
        saveSet()
        loading()
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            if (nowPage > 1) {
                val acts = listOf<String>(getString(R.string.text_select_site), getString(R.string.text_first_page), getString(R.string.text_previous_page), getString(R.string.text_next_page), getString(R.string.text_refresh))
                selector("", acts, { _, index ->
                    when (index) {
                        0 -> {
                            selectSite()
                        }
                        1 -> {
                            nowPage = 1
                            loading()
                        }
                        2 -> {
                            if (nowPage > 1) {
                                nowPage--
                                loading()
                            }
                        }
                        3 -> {
                            nowPage++
                            loading()
                        }
                        4 -> loading()
                    }
                })
            } else {
                val acts = listOf<String>(getString(R.string.text_select_site), getString(R.string.text_next_page), getString(R.string.text_refresh))
                selector("", acts, { _, index ->
                    when (index) {
                        0 -> {
                            selectSite()
                        }
                        1 -> {
                            nowPage++
                            loading()
                        }
                        2 -> loading()
                    }
                })
            }
        }
        toolbar_newsbox.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_refresh -> loading()
            }
            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Toolbar 菜单初始化
        menuInflater.inflate(R.menu.menu_newsbox, menu)
        return true
    }

    fun updateSiteInfo() {
        var thisSite = mutableMapOf<String, String>()
        var thisChannel = mutableMapOf<String, String>()
        sites.getSiteList().map {
            if (it["id"] == siteId) {
                thisSite = it
            }
        }
        sites.getSiteChannelList(siteId)!!.map {
            if (it["channelId"] == siteChannelId) {
                thisChannel = it
            }
        }
        siteName = thisSite["name"]!!
        channelName = thisChannel["channelName"]!!
    }

    fun saveSet() {
        SharedPreferencesUtils.put("NewsBox", "LAST_SITE_ID", siteId)
        SharedPreferencesUtils.put("NewsBox", "LAST_SITE_CHANNEL_ID", siteChannelId)
        Logger.d(SharedPreferencesUtils.getAll("NewsBox"))
        // 删除旧设置
        if (SharedPreferencesUtils.getAll("NewsBoxSetting").isNotEmpty()) {
            SharedPreferencesUtils.getAll("NewsBoxSetting").map {
                SharedPreferencesUtils.remove("NewsBoxSetting", it.key)
            }
        }
    }

    fun selectSite() {
        val siteList = sites.getSiteList()
        val siteIdList = siteList.map {
            it["id"]!!
        }
        val siteNameList = siteList.map {
            it["name"]!!
        }
        selector(getString(R.string.text_site), siteNameList, { _, i ->
            val siteIdTemp = siteIdList[i]
            val channelList = sites.getSiteChannelList(siteIdTemp)
            if (channelList == null) {
                Snackbar.make(coordinatorLayout_newsbox, "Site id error", Snackbar.LENGTH_SHORT).show()
            } else {
                selector(getString(R.string.text_channel), channelList.map { it["channelName"]!! }, { _, index ->
                    siteId = siteIdTemp
                    siteChannelId = channelList[index]["channelId"]!!
                    nowPage = 1
                    loading()
                })
            }
        })

    }

    fun listViewClearAll() {
        if (listView_news.adapter != null && !listView_news.adapter.isEmpty && listView_news.adapter.count > 0) listView_news.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
    }

    fun loading() {
        Logger.d("loading($siteId, $siteChannelId, $nowPage)")
        saveSet()
        updateSiteInfo()
        val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        listViewClearAll()
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        this@NewsBoxActivity.title = "$channelName - $nowPage"
        doAsync {
            val siteDataList = sites.getNewsList(siteId, siteChannelId, nowPage)
            uiThread {
                if (siteDataList == null) {
                    Snackbar.make(coordinatorLayout_newsbox, "错误：数据空白", Snackbar.LENGTH_SHORT).show()
                } else {
                    listView_news.adapter = ArrayAdapter<String>(this@NewsBoxActivity, android.R.layout.simple_list_item_1, siteDataList.map { it["title"] })
                    listView_news.onItemClick { _, _, index, _ ->
                        val clickedUrl: String = siteDataList[index]["web_url"].toString()
                        Logger.d(clickedUrl)
                        sysUtil.chromeCustomTabs(this@NewsBoxActivity, clickedUrl)
                    }
                }
                loadingProgressBar.dismiss()
            }
        }

    }
}
