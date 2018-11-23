package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.*
import com.zhihaofans.androidbox.util.HttpUtil


/**
 * Created by zhihaofans on 2018/6/3.
 */

class siteInfo_gankio(_context: Context) {
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_GANK_IO_ALL,
                        "channelName" to context.getString(R.string.text_site_gank_io) + "-" + context.getString(R.string.text_all)
                ),
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_GANK_IO_ANDROID,
                        "channelName" to context.getString(R.string.text_site_gank_io) + "-" + context.getString(R.string.text_android)
                ),
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_GANK_IO_GIRL,
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
        val thisUrl: String = when (channelId) {
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
        } ?: return null

        val headers = mutableMapOf(
                "content-type" to "application/json, text/javascript, */*; q=0.01",
                "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
        )
        try {
            newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
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
        var _page = page
        val newsList = mutableListOf<MutableMap<String, String>>()
        if (page < 1) {
            _page = 1
        }
        val thisUrl: String = when (channelId) {
            "dgtle_news" -> {
                "https://api.yii.dgtle.com/v2/news?perpage=24&page=$_page"
            }
            else -> null
        } ?: return null

        val headers = mutableMapOf(
                "content-type" to "application/json, text/javascript, */*; q=0.01",
                "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"
        )
        try {
            val newsListJson = HttpUtil.httpGetString(thisUrl, headers)
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
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers)
                    //Logger.d("newsListJson:$newsListJson")
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

class siteInfo_rsshub(_context: Context) {

    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_V2EX,
                        "channelName" to context.getString(R.string.text_site_rsshub_v2ex_topics)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_DOUBANMOVIEPLAYING,
                        "channelName" to context.getString(R.string.text_site_rsshub_douban_movie_playing)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_JIKE,
                        "channelName" to context.getString(R.string.text_site_rsshub_jike_editors_choice)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_JUEJINTRENDINGANDROID,
                        "channelName" to context.getString(R.string.text_site_rsshub_juejin_trending_android)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_BANGUMITODAY,
                        "channelName" to context.getString(R.string.text_site_rsshub_bangumi_calendar_today)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_NEW_RSS,
                        "channelName" to context.getString(R.string.text_site_rsshub_new_rss)
                ),
                mutableMapOf(
                        "channelId" to UrlMod.RSSHUB_GUOKR_SCIENTIFIC,
                        "channelName" to context.getString(R.string.text_site_rsshub_guokr_scientific)
                )
        )
    }

    fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
        val headers = mutableMapOf(
                Pair("content-type", "application/json, text/javascript, */*; q=0.01"),
                Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
        )
        val newsListJson: String
        val newsList = mutableListOf<MutableMap<String, String>>()
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
            newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
            //Logger.d("newsListJson:$newsListJson")
            val newsListData = g.fromJson(newsListJson, RsshubGson::class.java)
            val newsListItemData = newsListData.items
            if (newsListItemData.size == 0) {
                return null
            }
            newsListItemData.map {
                Logger.d("newsItem:$it")
                newsList.add(mutableMapOf(
                        "title" to it.title,
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

class siteInfo_wanandroid(_context: Context) {
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_WANANDROID_INDEX,
                        "channelName" to context.getString(R.string.text_site_wanandroid)
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
            "wanandroid_index" -> {
                val thisUrl = "http://www.wanandroid.com/article/list/${_page - 1}/json"
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
                    Logger.d("newsListJson:$newsListJson")
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
                            newsList.add(mutableMapOf(
                                    "title" to it.title,
                                    "web_url" to it.link
                            ))
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
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to ItemNameMod.FEED_DIYCODE_NEWS,
                        "channelName" to ItemNameMod.FEED_DIYCODE_NEWS
                )
        )
    }

    fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
        val jsonParser = JsonParser()
        var _page = page
        val newsList = mutableListOf<MutableMap<String, String>>()
        if (page < 1) {
            _page = 1
        }
        when (channelId) {
            "diycode_news" -> {
                val thisUrl = "https://diycode.cc/api/v3/news.json?node_id=1&limit=20&offset=${20 * (_page - 1)}"
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
                        val newsItem = g.fromJson(it, DiycodeNewItemGson::class.java)
                        newsList.add(mutableMapOf(
                                "title" to newsItem.title,
                                "web_url" to newsItem.address
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

class siteInfoZhihuDaily(_context: Context) {
    private val g = Gson()
    private val context = _context
    fun getchannelList(): MutableList<MutableMap<String, String>> {
        return mutableListOf(
                mutableMapOf(
                        "channelId" to "zhihu_daily",
                        "channelName" to context.getString(R.string.text_site_zhihu_daily)
                )
        )
    }

    fun getNewsList(channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
        val newsList = mutableListOf<MutableMap<String, String>>()
        when (channelId) {
            "zhihu_daily" -> {
                val thisUrl = "https://news-at.zhihu.com/api/4/news/latest"
                val headers = mutableMapOf(
                        Pair("content-type", "application/json;charset=UTF-8"),
                        Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
                )
                Logger.d(thisUrl)
                try {
                    val newsListJson = HttpUtil.httpGetString(thisUrl, headers) ?: return null
                    Logger.d("newsListJson:$newsListJson")
                    if (newsListJson.startsWith("{") && newsListJson.endsWith("}")) {
                        val newsIndex = g.fromJson(newsListJson, ZhihuDailyGson::class.java)
                        Logger.d("siteInfoZhihuDaily.getNewsList.date:" + newsIndex.date)
                        newsIndex.top_stories.map {
                            newsList.add(mutableMapOf(
                                    "title" to it.title,
                                    "web_url" to "https://daily.zhihu.com/story/${it.id}"
                            ))
                        }
                        newsIndex.stories.map {
                            newsList.add(mutableMapOf(
                                    "title" to it.title,
                                    "web_url" to "https://daily.zhihu.com/story/${it.id}"
                            ))
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