package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.DgtleIndexGson
import com.zhihaofans.androidbox.gson.GankIoAllGson
import com.zhihaofans.androidbox.gson.SspaiArticleGson
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null
    private val g = Gson()

    class newsBoxCommon {
        fun httpGetString(url: String, headers: MutableMap<String, String>? = null): String {
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(url)
            if (headers != null) {
                headers.map {
                    requestBuilder.addHeader(it.key, it.value)
                }
            }
            val request = requestBuilder.build()
            val call = client.newCall(request)
            try {
                val response = call.execute()
                if (response.body() == null) {
                    return ""
                } else {
                    return response.body()!!.string()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }

        }
    }

    class sites(_context: Context) {
        private val context = _context
        fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            return when (siteId) {
                "sspai" -> {
                    siteInfo_sspai(context).getNewsList(channelId, page)
                }
                "dgtle" -> {
                    siteInfo_dgtle(context).getNewsList(channelId, page)
                }
                "gank.io" -> {
                    siteInfo_gankio(context).getNewsList(channelId, page)
                }
                else -> null
            }
        }

        fun getSiteList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "id" to "sspai",
                            "name" to context.getString(R.string.text_site_sspai)
                    ),
                    mutableMapOf(
                            "id" to "dgtle",
                            "name" to context.getString(R.string.text_site_dgtle)
                    ),
                    mutableMapOf(
                            "id" to "gank.io",
                            "name" to context.getString(R.string.text_site_gank_io)
                    )
            )
        }

        fun getSiteChannelList(siteId: String): MutableList<MutableMap<String, String>>? {
            return when (siteId) {
                "sspai" -> siteInfo_sspai(context).getchannelList()
                "dgtle" -> siteInfo_dgtle(context).getchannelList()
                "gank.io" -> siteInfo_gankio(context).getchannelList()
                else -> null
            }
        }
    }

    class siteInfo_gankio(_context: Context) {
        private val nbc = newsBoxCommon()
        private val g = Gson()
        private val context = _context
        fun getchannelList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "channelId" to "gank_io_all",
                            "channelName" to context.getString(R.string.text_site_gank_io)
                    )
            )
        }

        fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            var newsListJson = ""
            var _page = page
            val newsList = mutableListOf<MutableMap<String, String>>()
            if (page < 1) {
                _page = 1
            }
            val thisUrl: String? = when (channelId) {
                "gank_io_all" -> {
                    "http://gank.io/api/data/all/20/$_page"
                }
                else -> null
            }

            if (thisUrl == null) {
                return null
            }
            val headers = mutableMapOf(
                    "content-type" to "application/json, text/javascript, */*; q=0.01",
                    "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
            )
            try {
                newsListJson = nbc.httpGetString(thisUrl, headers)
                Logger.d("newsListJson:$newsListJson")
                val gankIndex = g.fromJson(newsListJson, GankIoAllGson::class.java)
                val gankListIndex = gankIndex.results
                if (gankIndex.error) {
                    Logger.e("gankIndex.error:${gankIndex.error}")
                    return null
                }
                gankListIndex.map {
                    newsList.add(mutableMapOf(
                            "title" to it.desc
                            ,
                            "web_url" to it.url
                    ))
                }
                return newsList
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }

    class siteInfo_dgtle(_context: Context) {
        private val nbc = newsBoxCommon()
        private val g = Gson()
        private val context = _context
        fun getchannelList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "channelId" to "dgtle_news",
                            "channelName" to context.getString(R.string.text_site_dgtle_news)
                    ),
                    mutableMapOf(
                            "channelId" to "dgtle_forum",
                            "channelName" to context.getString(R.string.text_site_dgtle_forum)
                    )
            )
        }

        fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            var newsListJson = ""
            var _page = page
            val newsList = mutableListOf<MutableMap<String, String>>()
            if (page < 1) {
                _page = 1
            }
            val thisUrl: String? = when (channelId) {
                "dgtle_news" -> {
                    "https://api.yii.dgtle.com/v2/news?perpage=24&page=$_page"
                }
                "dgtle_forum" -> {
                    "https://api.yii.dgtle.com/v2/forum-thread/thread?perpage=24&typeid=0&page=$_page"
                }
                else -> null
            }

            if (thisUrl == null) {
                return null
            }
            val headers = mutableMapOf(
                    "content-type" to "application/json, text/javascript, */*; q=0.01",
                    "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
            )
            try {
                newsListJson = nbc.httpGetString(thisUrl, headers)
                Logger.d("newsListJson:$newsListJson")
                val dgtleIndex = g.fromJson(newsListJson, DgtleIndexGson::class.java)
                val dgtleListIndex = dgtleIndex.list
                if (dgtleListIndex.size == 0) {
                    return null
                }
                dgtleListIndex.map {
                    newsList.add(mutableMapOf(
                            "title" to
                                    if (it.date == null) {
                                        it.message + it.subject
                                    } else {
                                        it.subject
                                    }
                            ,
                            "web_url" to "http://www.dgtle.com/thread-${it.tid}-1-1.html"
                    ))
                }
                return newsList
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }


    class siteInfo_sspai(_context: Context) {
        private val nbc = newsBoxCommon()
        private val g = Gson()
        private val context = _context
        fun getchannelList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "channelId" to "sspai_article",
                            "channelName" to context.getString(R.string.text_site_sspai_article)
                    )
            )
        }

        fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            var newsListJson = ""
            var _page = page
            val newsList = mutableListOf<MutableMap<String, String>>()
            if (page < 1) {
                _page = 1
            }
            when (channelId) {
                "sspai_article" -> {
                    val thisUrl = "https://sspai.com/api/v1/articles?offset=" + (_page - 1) * 20 + "&limit=20&type=recommend_to_home&sort=recommend_to_home_at&include_total=false"
                    val headers = mutableMapOf(
                            Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                            Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                    )
                    try {
                        newsListJson = nbc.httpGetString(thisUrl, headers)
                        Logger.d("newsListJson:$newsListJson")
                        val sspaiIndex = g.fromJson(newsListJson, SspaiArticleGson::class.java)
                        val sspaiListIndex = sspaiIndex.list
                        if (sspaiListIndex.size == 0) {
                            return null
                        }
                        sspaiListIndex.map {
                            newsList.add(mutableMapOf(
                                    "title" to it.title,
                                    "web_url" to "https://www.sspai.com/post/${it.id}"
                            ))
                        }
                        return newsList
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return null
                    }
                }
                else -> return null
            }
        }
    }

    fun site(): MutableList<List<String>> {
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