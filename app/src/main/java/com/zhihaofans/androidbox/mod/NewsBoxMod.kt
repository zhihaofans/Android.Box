package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.DgtleIndexGson
import com.zhihaofans.androidbox.gson.GankIoAllGson
import com.zhihaofans.androidbox.gson.SspaiArticleGson

/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null
    private val g = Gson()
    fun sites(): MutableList<List<String>> {
        return mutableListOf(
                listOf("dgtle_news", getString(R.string.text_site_dgtle_news)),
                listOf("dgtle_forum", getString(R.string.text_site_dgtle_forum)),
                listOf("sspai_article", getString(R.string.text_site_sspai_article)),
                listOf("gank_io_all", getString(R.string.text_site_gank_io))
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
                        Pair("user-agent", getString(R.string.setting_http_defaultua))
                )
            }
            "dgtle_forum" -> {
                thisSiteInfo["api_url"] = "https://api.yii.dgtle.com/v2/forum-thread/thread?perpage=24&typeid=0&page={page}"
                thisSiteInfo["news_item_web"] = "http://www.dgtle.com/thread-{news_item_web_id}-1-1.html"
                thisSiteInfo["has_headers"] = true
                thisSiteInfo["headers"] = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", getString(R.string.setting_http_defaultua))
                )
            }
            "sspai_article" -> {
                thisSiteInfo["api_url"] = "https://sspai.com/api/v1/articles?offset={pageMinus|1}0&limit=10&type=recommend_to_home&sort=recommend_to_home_at&include_total=false"
                thisSiteInfo["news_item_web"] = "http://www.dgtle.com/thread-{news_item_web_id}-1-1.html"
                thisSiteInfo["has_headers"] = true
                thisSiteInfo["headers"] = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", getString(R.string.setting_http_defaultua))
                )
            }
            "gank_io_all" -> {
                thisSiteInfo["api_url"] = "http://gank.io/api/data/all/20/{page}"
                thisSiteInfo["news_item_web"] = "{news_item_web_id}"
                thisSiteInfo["has_headers"] = true
                thisSiteInfo["headers"] = mutableMapOf(
                        Pair("user-agent", getString(R.string.setting_http_defaultua))
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
            "sspai_article" -> {
                val sspaiIndex = g.fromJson(siteHtmlString, SspaiArticleGson::class.java)
                val sspaiListIndex = sspaiIndex.list
                returnList["title"] = sspaiListIndex.map {
                    it.title
                }
                returnList["web_url"] = sspaiListIndex.map {
                    "https://www.sspai.com/post/${it.id}"
                }
            }
            "gank_io_all" -> {
                val gankIndex = g.fromJson(siteHtmlString, GankIoAllGson::class.java)
                val gankListIndex = gankIndex.results
                if (gankIndex.error) {
                    return returnList
                }
                returnList["title"] = gankListIndex.map {
                    it.desc
                }
                returnList["web_url"] = gankListIndex.map {
                    it.url
                }
            }
        }
        return returnList
    }

    fun getString(id: Int): String {
        if (nowContext == null) return ""
        return nowContext!!.getString(id)
    }

    fun pageRuleParser(oldUrl: String, page: Int): String {
        if (oldUrl.contains("{page}")) {
            return oldUrl.replace("{page}", "$page")

        } else if (oldUrl.contains("{pagePlus|") && oldUrl.contains("}")) {
            val addNumber: Int = oldUrl.split("|")[1]
                    .split("}")[0]
                    .toInt()
            return oldUrl.replace("{pagePlus|$addNumber}", "${page + addNumber}")

        } else if (oldUrl.contains("{pageMinus|") && oldUrl.contains("}")) {
            val minusNumber: Int = oldUrl.split("|")[1]
                    .split("}")[0]
                    .toInt()
            return oldUrl.replace("{pageMinus|$minusNumber}", "${page - minusNumber}")

        } else if (oldUrl.contains("{pageBy|") && oldUrl.contains("}")) {
            val minusNumber: Int = oldUrl.split("|")[1]
                    .split("}")[0]
                    .toInt()
            return oldUrl.replace("{pageBy|$minusNumber}", "${page * minusNumber}")

        } else if (oldUrl.contains("{pagePlaces|") && oldUrl.contains("}")) {
            val minusNumber: Int = oldUrl.split("|")[1]
                    .split("}")[0]
                    .toInt()
            return oldUrl.replace("{pagePlaces|$minusNumber}", "${page / minusNumber}")
        } else {
            return oldUrl
        }
    }

}