package com.zhihaofans.androidbox.mod

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.util.isNullorEmpty

/**
 * Created by zhihaofans on 2018/8/26.
 */

class FeedMod {
    class News {
        private var mContext: Context? = null
        private var sites: NewsBoxMod.sites? = null
        private var cache: Cache? = null
        private var siteList = mutableListOf<SiteInfo>()
        private var siteListNew = mutableListOf<SiteInfoNew>()
        private var siteChannelList = mutableMapOf<String, MutableList<ChannelInfo>>()
        var page: Int
            get() {
                return if (cache == null) {
                    -1
                } else {
                    cache!!.nowPage
                }
            }
            set(value) {
                if (cache != null) {
                    this@News.getCache(cache!!.siteId, cache!!.siteChannelId, value)
                } else {
                    throw NullPointerException("Cache is null")
                }
            }

        fun init(context: Context) {
            mContext = context
            sites = NewsBoxMod.sites(mContext!!)
            siteList = sites!!.getSiteList().map {
                SiteInfo(it["id"]!!, it["name"]!!)
            }.toMutableList()
            siteList.map { site ->
                val thisSite = site.id
                val channelList = sites!!.getSiteChannelList(thisSite)!!.map {
                    ChannelInfo(it["channelId"]!!, it["channelName"]!!)
                }.toMutableList()
                siteListNew.add(SiteInfoNew(site.id, site.name, channelList))
                siteChannelList[thisSite] = channelList
            }
        }

        fun getSites(): NewsBoxMod.sites? {
            return sites
        }

        fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<NewsInfo>? {
            Logger.d("getNewsList:$siteId/$channelId/$page")
            val newsL = sites!!.getNewsList(siteId, channelId, page)
            return newsL?.map { NewsInfo(it["title"]!!, it["web_url"]!!) }?.toMutableList()

        }


        fun getSiteList(): MutableList<SiteInfo> {
            return siteList
        }

        fun getChannel(siteId: String): MutableList<ChannelInfo> {
            return siteChannelList[siteId]!!
        }

        fun getCache(): Cache? {
            return cache
        }

        fun getCache(oldCache: Cache): Cache? {
            return getCache(oldCache.siteId, oldCache.siteChannelId, oldCache.nowPage)
        }

        fun getCache(siteId: String, channelId: String, page: Int): Cache? {
            if (cache == null) {
                val newsList = getNewsList(siteId, channelId, page)
                return if (newsList.isNullorEmpty()) {
                    null
                } else {
                    var siteName = ""
                    var channelName = ""
                    sites!!.getSiteList().map {
                        if (it["id"] == siteId) {
                            siteName = it["id"]!!
                        }
                    }
                    sites!!.getSiteChannelList(siteId)!!.map {
                        if (it["channelId"] == channelId) {
                            channelName = it["channelId"]!!
                        }
                    }
                    cache = Cache(newsList!!, siteId, channelId, siteName, channelName, page)
                    cache
                }
            } else {
                return cache
            }
        }

        fun changePage(page: Int): Cache? {
            Logger.d("changerPage:$page")
            return if (cache == null) {
                null
            } else {
                cache = this@News.getCache(cache!!.siteId, cache!!.siteChannelId, page)
                cache
            }
        }

        fun refreshCache(): Cache? {
            return if (cache == null) {
                null
            } else {
                val oldCache = cache
                cache = null
                getCache(oldCache!!)
            }
        }

        data class Cache(
                var newsList: MutableList<NewsInfo>,
                var siteId: String,
                var siteChannelId: String,
                var siteName: String,
                var channelName: String,
                var nowPage: Int
        )

        data class SiteInfo(
                val id: String,
                val name: String
        )

        data class SiteInfoNew(
                val id: String,
                val name: String,
                val channel: MutableList<ChannelInfo>
        )

        data class ChannelInfo(
                val id: String,
                val name: String
        )

        data class NewsInfo(
                val title: String,
                val url: String
        )

        data class Init(
                val siteId: String,
                val channelId: String,
                val page: Int
        )

        data class Update(
                val type: Int,//0:refresh, 1:change page, 2.change site
                val data: Any? = null
        )
    }

    class App {
        private var mContext: Context? = null
        fun init(context: Context) {
            mContext = context
        }

        fun SiteParser(): AppDownMod.SiteParser? {
            return if (mContext == null) {
                null
            } else {
                val siteParser = AppDownMod.SiteParser()
                siteParser.init(mContext!!)
                siteParser
            }
        }
    }
}