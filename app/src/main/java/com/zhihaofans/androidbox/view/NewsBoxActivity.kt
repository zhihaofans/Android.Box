package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.DgtleIndexGson
import com.zhihaofans.androidbox.gson.DgtleIndexListGson
import kotlinx.android.synthetic.main.activity_news_box.*
import kotlinx.android.synthetic.main.content_news_box.*
import okhttp3.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.io.IOException


class NewsBoxActivity : AppCompatActivity() {
    private var dgtleListIndex = mutableListOf<DgtleIndexListGson>()
    private var nowSite = "dgtle"
    private var nowPage = 1
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_box)
        setSupportActionBar(toolbar_newsbox)
        initSite()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }
        toolbar_newsbox.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_refresh -> initSite()
                R.id.menu_next_page -> {
                    nowPage++
                    initSite()
                }
                R.id.menu_previous_page -> {
                    if (nowPage > 1) {
                        nowPage--
                        initSite()

                    }
                }
            }
            true
        }
    }

    fun initSite() {
        Logger.d("loading($nowSite, $nowPage)")
        loading(nowSite, nowPage)
    }

    fun loading(thisSite: String, thisPage: Int) {
        var url = ""
        if (listView_news.adapter != null && !listView_news.adapter.isEmpty && listView_news.adapter.count > 0) listView_news.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        dgtleListIndex = mutableListOf()
        val client = OkHttpClient()
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址

        when (thisSite) {
            "dgtle" -> {
                url = "https://api.yii.dgtle.com/v2/index?token=&perpage=14&page=$thisPage"
                if (thisPage > 1) url += "&dateline=${System.currentTimeMillis()}"
                request.url(url).addHeader("content-type", "application/json; charset=UTF-8").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
            }
            else -> {
                Snackbar.make(coordinatorLayout_newsbox, "站点错误", Snackbar.LENGTH_SHORT).show()
                loadingProgressBar.dismiss()
                return
            }
        }
        Logger.d("url:$url")
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
                val g = Gson()
                val dgtleIndex = g.fromJson(responseStr, DgtleIndexGson::class.java)
                dgtleListIndex = dgtleIndex.list
                val listData = dgtleListIndex.map { "$nowPage${it.title}" }
                runOnUiThread {
                    loadingProgressBar.dismiss()
                    Snackbar.make(coordinatorLayout_newsbox, "OK", Snackbar.LENGTH_SHORT).show()
                    listView_news.adapter = ArrayAdapter<String>(this@NewsBoxActivity, android.R.layout.simple_list_item_1, listData)
                    listView_news.onItemClick { _, _, index, _ ->
                        val thisNews = dgtleListIndex[index]
                        browse("http://www.dgtle.com/thread-${thisNews.tid}-1-1.html")
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_newsbox, menu)
        return true
    }
}
