package com.zhihaofans.androidbox.mod

import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.data.News
import com.zhihaofans.androidbox.gson.*
import com.zhihaofans.androidbox.kotlinEx.hasNotChild
import com.zhihaofans.androidbox.kotlinEx.isNotNullAndEmpty
import com.zhihaofans.androidbox.util.HttpUtil
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.jsoup.Jsoup


/**
 * Created by zhihaofans on 2018/6/3.
 */

class SiteInfoGankio {
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val g = Gson()
        var mPage = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            mPage = 1
        }
        val thisUrl: String = when (channelId) {
            ItemIdMod.FEED_GANK_IO_ALL -> {
                UrlMod.GANK_IO_ALL + mPage
            }
            ItemIdMod.FEED_GANK_IO_ANDROID -> {
                UrlMod.GANK_IO_ANDROID + mPage
            }
            ItemIdMod.FEED_GANK_IO_GIRL -> {
                UrlMod.GANK_IO_GIRL + mPage
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

class SiteInfoDgtle {
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val g = Gson()
        var mPage = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            mPage = 1
        }
        val thisUrl: String = when (channelId) {
            "dgtle_news" -> UrlMod.DGTLE_NEWS + mPage
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

class SiteInfoSspai {
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val g = Gson()
        var mPage = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            mPage = 1
        }
        when (channelId) {
            "sspai_article" -> {
                val thisUrl = UrlMod.SSPAI_ARTICLE + (mPage - 1) * 20
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

class SiteInfoRsshub {
    fun getNewsList(channelId: String): MutableList<News>? {
        val g = Gson()
        val headers = mutableMapOf(
                Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
        )
        val newsList = mutableListOf<News>()
        val thisUrl = when (channelId) {
            ItemIdMod.FEED_RSSHUB_DOUBAN_MOVIE_PLAYING -> UrlMod.RSSHUB_DOUBANMOVIEPLAYING
            ItemIdMod.FEED_RSSHUB_JUEJIN_TRENDING_ANDROID -> UrlMod.RSSHUB_JUEJINTRENDINGANDROID
            ItemIdMod.FEED_RSSHUB_BANGUMI_CALENDAR_TODAY -> UrlMod.RSSHUB_BANGUMITODAY
            ItemIdMod.FEED_RSSHUB_NEW_RSS -> UrlMod.RSSHUB_NEW_RSS
            ItemIdMod.FEED_RSSHUB_GUOKR_SCIENTIFIC -> UrlMod.RSSHUB_GUOKR_SCIENTIFIC
            else -> return null
        }
        if (thisUrl.isEmpty()) return null
        try {
            val newsListXml = HttpUtil.httpGetString(thisUrl, headers) ?: return null
            val newsListJson = XmlToJson.Builder(newsListXml).build().toString()
            val newsListData = g.fromJson(newsListJson, Rss2jsonNewGson::class.java)
            if (newsListData.rss == null) return null
            val newsListItemData = newsListData.rss.channel.item
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

class SiteInfoWanandroid {
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val g = Gson()
        var mPage = page
        val newsList = mutableListOf<News>()
        if (page < 1) {
            mPage = 1
        }
        when (channelId) {
            ItemIdMod.FEED_WANANDROID_INDEX -> {
                val thisUrl = UrlMod.WANANDROID_INDEX + "${mPage - 1}/json"
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

class SiteInfoZhihudaily {
    fun getNewsList(channelId: String): MutableList<News>? {
        val g = Gson()
        val newsList = mutableListOf<News>()
        when (channelId) {
            ItemIdMod.FEED_ZHIHU_DAILY -> {
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
                        Logger.d("SiteInfoZhihudaily.getNewsList.date:" + newsIndex.date)
                        val urlList = mutableListOf<String>()
                        newsIndex.top_stories.map {
                            val mUrl = UrlMod.ZHIHU_DAILY_WEB + it.id
                            if (urlList.hasNotChild(mUrl)) {
                                newsList.add(News(it.title, UrlMod.ZHIHU_DAILY_WEB + it.id))
                                urlList.add(mUrl)
                            }
                        }
                        newsIndex.stories.map {
                            val mUrl = UrlMod.ZHIHU_DAILY_WEB + it.id
                            if (urlList.hasNotChild(mUrl)) {
                                newsList.add(News(it.title, UrlMod.ZHIHU_DAILY_WEB + it.id))
                                urlList.add(mUrl)
                            }
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

class SiteInfoWeixinjingxuan {
    fun getNewsList(channelId: String, page: Int): MutableList<News>? {
        val g = Gson()
        val newsList = mutableListOf<News>()
        when (channelId) {
            ItemIdMod.FEED_JUHE_WEIXIN_JINGXUAN -> {
                val thisUrl = UrlMod.WEIXIN_JINGXUAN + page.toString()
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers)
                            ?: return null
                    if (newsListJson.startsWith("{") && newsListJson.endsWith("}")) {
                        val juheWeixin = g.fromJson(newsListJson, JuheWeixinGson::class.java)
                        if (juheWeixin.error_code != 0 || juheWeixin.result == null) return null
                        juheWeixin.result.list.map { item ->
                            newsList.add(News(item.title, item.url))
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


class SiteInfoTophubToday {
    fun getNewsList(channelId: String): MutableList<News>? {
        return when (channelId) {
            ItemIdMod.FEED_TOPHUB_TODAY_WEIBO -> topToday(UrlMod.TOPHUB_TODAY_WEIBO)
            ItemIdMod.FEED_TOPHUB_TODAY_JINRITOUTIAO -> topToday(UrlMod.TOPHUB_TODAY_JINRITOUTIAO)
            ItemIdMod.FEED_TOPHUB_TODAY_HUPUBUXINGJIE -> topToday(UrlMod.TOPHUB_TODAY_HUPUBUXINGJIE)
            ItemIdMod.FEED_TOPHUB_TODAY_ZHIHU_HOT -> topToday(UrlMod.TOPHUB_TODAY_ZHIHU_HOT)
            ItemIdMod.FEED_TOPHUB_TODAY_V2EX_HOT -> topToday(UrlMod.TOPHUB_TODAY_V2EX_HOT)
            else -> null
        }
    }

    private fun topToday(url: String): MutableList<News>? {
        val newsList = mutableListOf<News>()
        try {
            val doc = Jsoup.connect(url).get()
            if (doc.html().isNotNullAndEmpty()) {
                val newsListElement = doc.select("table.table")
                val newsItemList = newsListElement[0].select("tbody > tr > td.al > a")
                newsItemList.map { newsItem ->
                    val newsTitle = (newsList.size + 1).toString() + "." + newsItem.text()
                    val newsLink = newsItem.attr("href")
                    newsList.add(News(newsTitle, newsLink))
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
}