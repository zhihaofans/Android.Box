package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.DgtleIndexGson

/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null
    private val g = Gson()
    fun sites(): MutableList<List<String>> {
        return mutableListOf(
                listOf("dgtle_news", nowContext!!.getString(R.string.text_site_dgtle_news)),
                listOf("dgtle_forum", nowContext!!.getString(R.string.text_site_dgtle_forum))
        )
    }

    fun setContext(context: Context) {
        nowContext = context
    }

    fun siteInfo(siteId: String): MutableMap<String, Any> {
        val thisSiteInfo = mutableMapOf(
                Pair("api_url", "{page}"), Pair("news_item_web", "{news_item_web_id}"), Pair("yes", true), Pair("has_headers", false), Pair("headers", mutableMapOf<String, String>())
        )
        when (siteId) {
            "dgtle_news" -> {
                thisSiteInfo["api_url"] = "https://api.yii.dgtle.com/v2/news?perpage=24&page={page}"
                thisSiteInfo["news_item_web"] = "http://www.dgtle.com/thread-{news_item_web_id}-1-1.html"
                thisSiteInfo["has_headers"] = true
                thisSiteInfo["headers"] = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
            }
            "dgtle_forum" -> {
                thisSiteInfo["api_url"] = "https://api.yii.dgtle.com/v2/forum-thread/thread?perpage=24&typeid=0&page={page}"
                thisSiteInfo["news_item_web"] = "http://www.dgtle.com/thread-{news_item_web_id}-1-1.html"
                thisSiteInfo["has_headers"] = true
                thisSiteInfo["headers"] = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
            }
            else -> thisSiteInfo["yes"] = false
        }
        return thisSiteInfo
    }

    fun siteHtml2list(siteId: String, siteHtmlString: String): MutableMap<String, List<String>> {
        val returnList = mutableMapOf<String, List<String>>(Pair("title", listOf()), Pair("web_url", listOf()))
        when (siteId) {
            "dgtle_news", "dgtle_forum" -> {
                val dgtleIndex = g.fromJson(siteHtmlString, DgtleIndexGson::class.java)
                val dgtleListIndex = dgtleIndex.list
                returnList["title"] = dgtleListIndex.map {
                    if (it.date == null) it.message + it.subject
                    else it.subject
                }
                returnList["web_url"] = dgtleListIndex.map {
                    "http://www.dgtle.com/thread-${it.tid}-1-1.html"
                }
            }
        }
        return returnList
    }
}