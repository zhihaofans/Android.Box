package com.zhihaofans.androidbox.mod

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.data.AppDownFeed
import com.zhihaofans.androidbox.kotlinEx.isNullorEmpty
import com.zhihaofans.androidbox.util.SystemUtil

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
            siteList = sites!!.getOldVerSiteList().map {
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
            val newsL = sites!!.getOldVerNewsList(siteId, channelId, page)
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
            if (cache == null || siteId != cache!!.siteId || channelId != cache!!.siteChannelId || page != cache!!.nowPage) {
                val newsList = getNewsList(siteId, channelId, page)
                return if (newsList.isNullorEmpty()) {
                    null
                } else {
                    var siteName = ""
                    var channelName = ""
                    sites!!.getOldVerSiteList().map {
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
            Logger.d("changePage:$page")
            return when {
                cache == null -> null
                page == cache!!.nowPage -> cache
                else -> {
                    cache = this@News.getCache(cache!!.siteId, cache!!.siteChannelId, page)
                    cache
                }
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

        fun getListView(titleList: List<String>, urlList: List<String>): ListView {
            return ListView(titleList.toMutableList(), urlList.toMutableList())
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

        data class ListView(
                val titleList: MutableList<String>,
                val urlList: MutableList<String>
        )
    }

    class App {
        private var mContext: Context? = null
        private var siteParser: AppDownMod.SiteParser? = null
        private val dataBase = AppDownMod.DataBase()
        private var appFeeds = mutableListOf<AppDownFeed>()
        fun init(context: Context) {
            mContext = context
            siteParser = this@App.initSiteParser()
            initAppList()
        }

        fun initAppList(): MutableList<AppDownFeed> {
            appFeeds = dataBase.getAppFeeds()
            return appFeeds
        }

        fun getAppList(): AppList = AppList(appFeeds)

        fun fileNameList(clickedApp: AppDownFeed, fileIndex: Int, fileExt: String): MutableList<String> {
            val fileList = clickedApp.fileList
            val file = fileList[fileIndex]
            return mutableListOf(
                    clickedApp.name + fileExt,
                    clickedApp.name + "_" + clickedApp.version + fileExt,
                    clickedApp.site + "_" + clickedApp.name + fileExt,
                    clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + fileExt,
                    clickedApp.site + "_" + clickedApp.name + "_" + file.name,
                    clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + "_" + file.name,
                    clickedApp.packageName + fileExt,
                    clickedApp.packageName + "_" + clickedApp.version + fileExt,
                    clickedApp.site + "_" + clickedApp.packageName + fileExt,
                    clickedApp.site + "_" + clickedApp.packageName + "_" + clickedApp.version + fileExt,
                    clickedApp.site + "_" + clickedApp.id_one + "_" +
                            (if (clickedApp.id_two == null) "" else clickedApp.id_two + "_") + clickedApp.version + "_" + file.name
            ).map {
                if (it.endsWith(".apk")) {
                    it
                } else {
                    "$it.apk"
                }
            }.toMutableList()
        }

        fun getSavePath(): String = UrlMod.APP_DOWNLOAD_PATH


        fun importDB(dataBaseStr: String): Boolean {
            return if (mContext != null) {
                dataBase.importJson(dataBaseStr)
            } else {
                false
            }
        }

        fun exportDB(): String = dataBase.export2json()

        private fun initSiteParser(): AppDownMod.SiteParser? {
            return if (mContext == null) {
                null
            } else {
                val sp = AppDownMod.SiteParser()
                sp.init(mContext!!)
                sp
            }
        }

        data class AppList(
                val data: MutableList<AppDownFeed>
        )

        data class Update(
                val type: Int,//0:checkUpdate, 1:add, 2:del, 3:import database, 4:export database
                val data: Any? = null
        )
    }
}