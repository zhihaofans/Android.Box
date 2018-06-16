package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.DgtleIndexGson
import com.zhihaofans.androidbox.gson.GankIoAllGson
import com.zhihaofans.androidbox.gson.PingwestForwardRecommendGson
import com.zhihaofans.androidbox.gson.SspaiArticleGson


/**
 * Created by zhihaofans on 2018/6/3.
 */

class siteInfo_gankio(_context: Context) {
    private val nbc = NewsBoxMod.newsBoxCommon()
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to "gank_io_all",
                        "channelName" to context.getString(R.string.text_site_gank_io) + "-" + context.getString(R.string.text_all)
                ),
                mutableMapOf(
                        "channelId" to "gank_io_android",
                        "channelName" to context.getString(R.string.text_site_gank_io) + "-" + context.getString(R.string.text_android)
                ),
                mutableMapOf(
                        "channelId" to "gank_io_girl",
                        "channelName" to context.getString(R.string.text_site_gank_io) + "-" + context.getString(R.string.text_gankio_girl)
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
            "gank_io_android" -> {
                "http://gank.io/api/data/Android/20/$_page"
            }
            "gank_io_girl" -> {
                "http://gank.io/api/data/福利/20/$_page"
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
            newsListJson = nbc.httpGet4String(thisUrl, headers)
            Logger.d("newsListJson:$newsListJson")
            val newsIndex = g.fromJson(newsListJson, GankIoAllGson::class.java)
            val newsListIndex = newsIndex.results
            if (newsIndex.error) {
                Logger.e("gankIndex.error:${newsIndex.error}")
                return null
            }
            newsListIndex.map {
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
    private val nbc = NewsBoxMod.newsBoxCommon()
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to "dgtle_news",
                        "channelName" to context.getString(R.string.text_site_dgtle_news)
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
            newsListJson = nbc.httpGet4String(thisUrl, headers)
            Logger.d("newsListJson:$newsListJson")
            val newsIndex = g.fromJson(newsListJson, DgtleIndexGson::class.java)
            val newsListIndex = newsIndex.list
            if (newsListIndex.size == 0) {
                return null
            }
            newsListIndex.map {
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
    private val nbc = NewsBoxMod.newsBoxCommon()
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
                    newsListJson = nbc.httpGet4String(thisUrl, headers)
                    Logger.d("newsListJson:$newsListJson")
                    val newsIndex = g.fromJson(newsListJson, SspaiArticleGson::class.java)
                    val newsListIndex = newsIndex.list
                    if (newsListIndex.size == 0) {
                        return null
                    }
                    newsListIndex.map {
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

class siteInfo_pingwest(_context: Context) {

    private val nbc = NewsBoxMod.newsBoxCommon()
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to "pingwest_forwarding_recommendation",
                        "channelName" to context.getString(R.string.text_site_pingwest_forwardrecommend)
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
            "pingwest_forwarding_recommendation" -> {
                val thisUrl = "http://no.pingwest.com/recommend?num=20&pagenum=$_page"
                val headers = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                try {
                    newsListJson = nbc.httpGet4String(thisUrl, headers)
                    Logger.d("newsListJson:$newsListJson")
                    val jsonArray = JsonParser().parse(newsListJson).asJsonArray
                    if (jsonArray.size() == 0) {
                        return null
                    }
                    jsonArray.map {
                        val newsItem = g.fromJson(it, PingwestForwardRecommendGson::class.java)
                        Logger.d("newsItem:$newsItem")
                        newsList.add(mutableMapOf(
                                "title" to newsItem.title,
                                "web_url" to newsItem.link
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

class siteInfo_allgl(_context: Context) {

    private val nbc = NewsBoxMod.newsBoxCommon()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to "allgl_all",
                        "channelName" to context.getString(R.string.text_site_allgl_all)
                )
        )
    }

    fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
        var _page = page
        val newsList = mutableListOf<MutableMap<String, String>>()
        if (page < 1) {
            _page = 1
        }
        when (channelId) {
            "allgl_all" -> {
                var thisUrl = "http://all.gl"
                if (_page >= 2) {
                    thisUrl += "/page/$_page/"
                }
                val headers = mutableMapOf(
                        Pair("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                try {
                    val doc = nbc.httpGet4Jsoup(thisUrl, headers)
                    val docHtml = doc.html()
                    Logger.d("docHtml:$docHtml")
                    if (docHtml.isEmpty()) {
                        Logger.e("docHtml.isEmpty()")
                        return null
                    }
                    val htmlMain = doc.getElementById("main")
                    if (htmlMain == null) {
                        Logger.e("htmlMain=null")
                        return null
                    }
                    val newsListClass = htmlMain.getElementsByClass("clickright-lite-grid")
                    if (newsListClass == null) {
                        Logger.e("newsListClass=null")
                        return null
                    } else if (newsListClass.size != 1) {
                        Logger.e("newsListClass.size!=1")
                        return null
                    }
                    val newsListData = newsListClass[0].select("li > article.item")
                    if (newsListData == null) {
                        Logger.e("newsList=null")
                        return null
                    }
                    if (newsListData.size == 0) {
                        Logger.e("newsList.size=0")
                        return null
                    }
                    newsListData.map {
                        val contentJsoup = it.select("div.item-content > h2 > a")
                        Logger.d("contentJsoup:$contentJsoup")
                        val newsItemUrl = contentJsoup.attr("href")
                        val newsItemTitle = contentJsoup.text()
                        newsList.add(mutableMapOf(
                                "title" to newsItemTitle,
                                "web_url" to newsItemUrl
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