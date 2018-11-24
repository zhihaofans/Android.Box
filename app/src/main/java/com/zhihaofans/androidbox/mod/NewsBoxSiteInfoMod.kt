package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.News
import com.zhihaofans.androidbox.gson.*
import com.zhihaofans.androidbox.util.HttpUtil


/**
 * Created by zhihaofans on 2018/6/3.
 */

class siteInfo_gankio(_context: Context) {
    private val g = Gson()
    private val context = _context

    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        var _page = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            _page = 1
        }
        val thisUrl: String = when (channelId) {
            ItemNameMod.FEED_GANK_IO_ALL -> {
                UrlMod.GANK_IO_ALL + _page
            }
            ItemNameMod.FEED_GANK_IO_ANDROID -> {
                UrlMod.GANK_IO_ANDROID + _page
            }
            ItemNameMod.FEED_GANK_IO_GIRL -> {
                UrlMod.GANK_IO_GIRL + _page
            }
            else -> null
        } ?: return null

        val headers = mutableMapOf(
                "content-type" to "application/json, text/javascript, */*; q=0.01",
                "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
        )
        try {
            val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
            val newsIndex = g.fromJson(newsListJson, GankIoAllGson::class.java)
            val newsListIndex = newsIndex.results
            if (newsIndex.error) {
                Logger.e("gankIndex.error:${newsIndex.error}")
                return null
            }
            newsListIndex.map {
                newsList.add(News(it.desc, it.url))
            }
            return newsList
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}

class siteInfo_dgtle(_context: Context) {
    private val g = Gson()
    private val context = _context
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        var _page = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            _page = 1
        }
        val thisUrl: String = when (channelId) {
            "dgtle_news" -> UrlMod.DGTLE_NEWS + _page
            else -> null
        } ?: return null

        val headers = mutableMapOf(
                "content-type" to "application/json, text/javascript, */*; q=0.01",
                "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
        )
        try {
            val newsListJson = HttpUtil.httpGetString(thisUrl, headers)
            val newsIndex = g.fromJson(newsListJson, DgtleIndexGson::class.java)
            val newsListIndex = newsIndex.list
            if (newsListIndex.size == 0) {
                return null
            }
            newsListIndex.map {
                newsList.add(News(if (it.date == null) {
                    it.message + it.subject
                } else {
                    it.subject
                }, "http://www.dgtle.com/thread-${it.tid}-1-1.html"))
            }
            return newsList
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}

class siteInfo_sspai(_context: Context) {
    private val g = Gson()
    private val context = _context

    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        var _page = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            _page = 1
        }
        when (channelId) {
            "sspai_article" -> {
                val thisUrl = UrlMod.SSPAI_ARTICLE + (_page - 1) * 20
                val headers = mutableMapOf(
                        Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers)
                    val newsIndex = g.fromJson(newsListJson, SspaiArticleGson::class.java)
                    val newsListIndex = newsIndex.list
                    if (newsListIndex.size == 0) {
                        return null
                    }
                    newsListIndex.map {
                        newsList.add(News(it.title, "https://www.sspai.com/post/${it.id}"))
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

class siteInfo_rsshub(_context: Context) {

    private val g = Gson()

    fun getNewsList(channelId: String): MutableList<News>? {
        val headers = mutableMapOf(
                Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
        )
        val newsList = mutableListOf<News>()
        val thisUrl = when (channelId) {
            ItemNameMod.FEED_RSSHUB_V2EX_TOPICS -> UrlMod.RSSHUB_V2EX
            ItemNameMod.FEED_RSSHUB_DOUBAN_MOVIE_PLAYING -> UrlMod.RSSHUB_DOUBANMOVIEPLAYING
            ItemNameMod.FEED_RSSHUB_JIKE_EDITOR_CHOICE -> UrlMod.RSSHUB_JIKE
            ItemNameMod.FEED_RSSHUB_JUEJIN_TRENDING_ANDROID -> UrlMod.RSSHUB_JUEJINTRENDINGANDROID
            ItemNameMod.FEED_RSSHUB_BANGUMI_CALENDAR_TODAY -> UrlMod.RSSHUB_BANGUMITODAY
            ItemNameMod.FEED_RSSHUB_NEW_RSS -> UrlMod.RSSHUB_NEW_RSS
            ItemNameMod.FEED_RSSHUB_GUOKR_SCIENTIFIC -> UrlMod.RSSHUB_GUOKR_SCIENTIFIC
            else -> return null
        }
        if (thisUrl.isEmpty()) return null
        try {
            val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
            val newsListData = g.fromJson(newsListJson, Rss2jsonGson::class.java)
            if (newsListData.status != "ok") return null
            val newsListItemData = newsListData.items
            if (newsListItemData.isNullOrEmpty()) return null
            newsListItemData.map {
                newsList.add(News(it.title, it.link))
            }
            return newsList
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

class siteInfo_wanandroid(_context: Context) {
    private val g = Gson()
    private val context = _context

    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        var _page = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            _page = 1
        }
        when (channelId) {
            ItemNameMod.FEED_WANANDROID_INDEX -> {
                val thisUrl = UrlMod.WANANDROID_INDEX + "${_page - 1}/json"
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null

                    if (newsListJson.startsWith("{") && newsListJson.endsWith("}")) {
                        val newsIndex = g.fromJson(newsListJson, WanandroidGson::class.java)
                        if (newsIndex.errorCode != 0) {
                            return null
                        }
                        val newsListIndex = newsIndex.data
                        if (newsListIndex.size == 0) {
                            return null
                        }
                        newsListIndex.datas.map {
                            newsList.add(News(it.title, it.link))
                        }
                    } else {
                        return null
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


class siteInfo_diycode(_context: Context) {
    private val g = Gson()
    private val context = _context

    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val jsonParser = JsonParser()
        var _page = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            _page = 1
        }
        when (channelId) {
            ItemNameMod.FEED_DIYCODE_NEWS -> {
                val thisUrl = UrlMod.DIYCODE_NEWS + "${20 * (_page - 1)}"
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
                    if (newsListJson.isEmpty()) return null
                    if (newsListJson.startsWith("{") && newsListJson.startsWith("}")) {
                        val error = g.fromJson(newsListJson, DiycodeNewErrorGson::class.java)
                        if (error.error != null) return null
                    }
                    val jsonAarray = jsonParser.parse(newsListJson).asJsonArray
                    if (jsonAarray.size() == 0) return null
                    jsonAarray.map {
                        g.fromJson(it, DiycodeNewItemGson::class.java).apply {
                            newsList.add(News(title, address))
                        }
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

class siteInfoZhihuDaily(_context: Context) {
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_ZHIHU_DAILY,
                        "channelName" to context.getString(R.string.text_site_zhihu_daily)
                )
        )
    }

    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val newsList = mutableListOf<News>()
        when (channelId) {
            ItemNameMod.FEED_ZHIHU_DAILY -> {
                val thisUrl = UrlMod.ZHIHU_DAILY
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
                    if (newsListJson.startsWith("{") && newsListJson.endsWith("}")) {
                        val newsIndex = g.fromJson(newsListJson, ZhihuDailyGson::class.java)
                        Logger.d("siteInfoZhihuDaily.getNewsList.date:" + newsIndex.date)
                        newsIndex.top_stories.map {
                            newsList.add(News(it.title, UrlMod.ZHIHU_DAILY_WEB + it.id))
                        }
                        newsIndex.stories.map {
                            newsList.add(News(it.title, UrlMod.ZHIHU_DAILY_WEB + it.id))
                        }
                        if (newsList.size == 0) {
                            return null
                        }
                    } else {
                        return null
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