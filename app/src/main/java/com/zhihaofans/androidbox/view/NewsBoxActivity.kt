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
import com.zhihaofans.androidbox.gson.DgtleIndexListGson
import com.zhihaofans.androidbox.mod.NewsBoxMod
import kotlinx.android.synthetic.main.activity_news_box.*
import kotlinx.android.synthetic.main.content_news_box.*
import okhttp3.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.selector
import java.io.IOException


class NewsBoxActivity : AppCompatActivity() {
    private var dgtleListIndex = mutableListOf<DgtleIndexListGson>()
    private val newsBoxMod = NewsBoxMod()
    private var newsSites = mutableListOf<List<String>>()
    private var nowSite: List<String> = listOf()
    private var nowPage = 1
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    private var lastSiteId: String? = null
    private var lastSiteIndex = 0
    private var lastSitePage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_box)
        setSupportActionBar(toolbar_newsbox)
        newsBoxMod.setContext(this@NewsBoxActivity)
        newsSites = newsBoxMod.sites()
        SharedPreferencesUtils.init(this)
        lastSiteId = SharedPreferencesUtils.getString("NewsBoxSetting", "LastSiteId")
        if (lastSiteId.isNullOrEmpty()) {
            lastSiteIndex = SharedPreferencesUtils.getInt("NewsBoxSetting", "LastSiteIndex")
            lastSitePage = SharedPreferencesUtils.getInt("NewsBoxSetting", "LastSitePage")
            lastSiteId = nowSite[0]
            nowPage = lastSitePage
        }
        nowSite = newsSites[lastSiteIndex]
        saveSet()
        loading()
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            if (nowPage > 1) {
                val acts = listOf<String>(getString(R.string.text_first_page), getString(R.string.text_previous_page), getString(R.string.text_next_page), getString(R.string.text_refresh))
                selector("", acts, { _, index ->
                    when (index) {
                        0 -> {
                            nowPage = 1
                            loading()
                        }
                        1 -> {
                            if (nowPage > 1) {
                                nowPage--
                                loading()
                            }
                        }
                        2 -> {
                            nowPage++
                            loading()
                        }
                        3 -> loading()
                    }
                })
            } else {
                val acts = listOf<String>(getString(R.string.text_next_page), getString(R.string.text_refresh))
                selector("", acts, { _, index ->
                    when (index) {
                        0 -> {
                            nowPage++
                            loading()
                        }
                        1 -> loading()
                    }
                })
            }
        }
        toolbar_newsbox.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_site_set -> {
                    val sites_name: List<String> = newsSites.map { it[1] }
                    selector("", sites_name, { _, index ->
                        lastSiteIndex = index
                        lastSiteId = newsSites[index][0]
                        selectSite()
                    })
                }
            }
            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_newsbox, menu)
        return true
    }

    fun saveSet() {
        SharedPreferencesUtils.put("NewsBoxSetting", "LastSiteId", lastSiteId)
        SharedPreferencesUtils.put("NewsBoxSetting", "LastSiteIndex", lastSiteIndex)
        SharedPreferencesUtils.put("NewsBoxSetting", "LastSitePage", lastSitePage)
        Logger.d(SharedPreferencesUtils.getAll("NewsBoxSetting"))
    }

    fun selectSite() {
        nowSite = newsSites[lastSiteIndex]
        nowPage = 1
        loading()
    }

    fun listViewClearAll() {
        if (listView_news.adapter != null && !listView_news.adapter.isEmpty && listView_news.adapter.count > 0) listView_news.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
    }

    fun loading() {
        Logger.d("loading($nowSite, $nowPage)")
        lastSitePage = nowPage
        saveSet()
        val thisSiteId = nowSite[0]
        val thisSiteName = nowSite[1]
        val thisSiteInfo = newsBoxMod.siteInfo(thisSiteId)
        var url: String = thisSiteInfo["api_url"] as String
        val client = OkHttpClient()
        val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        if (!(thisSiteInfo["yes"] as Boolean)) {
            Snackbar.make(coordinatorLayout_newsbox, "站点错误", Snackbar.LENGTH_SHORT).show()
            return
        }
        listViewClearAll()
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        //if (thisPage > 1) url += "&page=$thisPage"
        url = newsBoxMod.pageRuleParser(url, nowPage)
        request.url(url)
        Logger.d("url:$url")
        if (thisSiteInfo["has_headers"] as Boolean) {
            (thisSiteInfo["headers"] as MutableMap<*, *>).map {
                request.addHeader(it.key as String, it.value as String)
            }
        }
        this@NewsBoxActivity.title = "$thisSiteName - $nowPage"
        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    loadingProgressBar.dismiss()
                    Snackbar.make(coordinatorLayout_newsbox, "获取信息失败", Snackbar.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body()
                var responseStr = ""
                if (resBody != null) {
                    responseStr = resBody.string()
                }
                Logger.d(responseStr)
                val siteDataList = newsBoxMod.siteHtml2list(thisSiteId, responseStr)
                val listData = siteDataList["title"]
                val urlList = siteDataList["web_url"]
                runOnUiThread {
                    loadingProgressBar.dismiss()
                    if (listData == null || urlList == null) {
                        Snackbar.make(coordinatorLayout_newsbox, "错误：数据空白", Snackbar.LENGTH_SHORT).show()
                    } else if (listData.size != urlList.size) {
                        Snackbar.make(coordinatorLayout_newsbox, "错误：数据列表长度不相等 (${listData.size}/${urlList.size})", Snackbar.LENGTH_SHORT).show()
                    } else {
                        listView_news.adapter = ArrayAdapter<String>(this@NewsBoxActivity, android.R.layout.simple_list_item_1, listData)
                        listView_news.onItemClick { _, _, index, _ -> browse(urlList[index]) }
                        Snackbar.make(coordinatorLayout_newsbox, "OK", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}
