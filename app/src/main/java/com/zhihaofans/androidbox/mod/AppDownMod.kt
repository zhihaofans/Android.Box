package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.AppDownFeed
import com.zhihaofans.androidbox.data.AppInfo
import com.zhihaofans.androidbox.data.AppInfoResult
import io.paperdb.Paper

/**
 * Created by zhihaofans on 2018/7/25.
 */
class AppDownMod {

    class SiteParser {
        private var mcontext: Context? = null
        private var sites: List<Map<String, String>> = mutableListOf()
        fun init(context: Context): Context? {
            this.mcontext = context
            sites = mutableListOf(
                    mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases", "version" to "1"),// Github releases
                    mutableMapOf("id" to "COOLAPK_WEB", "name" to mcontext!!.getString(R.string.text_coolapk) + " v1", "version" to "1"),// CoolApk Web
                    mutableMapOf("id" to "FIRIM_V1", "name" to "Fir.im v1", "version" to "1"),// Fir.im v1 (Api)
                    mutableMapOf("id" to "WANDOUJIA_V1", "name" to mcontext!!.getString(R.string.text_wandoujia) + " v1", "version" to "1"),// Wandoujia v1
                    mutableMapOf("id" to "MYAPP", "name" to mcontext!!.getString(R.string.text_myapp) + " v1", "version" to "1"),// Tencent yingyongbao
                    //mutableMapOf("id" to "APKPURE", "name" to "ApkPure" + " v1", "version" to "1")// ApkPure
                    mutableMapOf("id" to "FIRIM_V2", "name" to "Fir.im v2", "version" to "1")// Fir.im v2 (RSSHub)
            )
            Logger.d(sites)
            return this.mcontext
        }

        fun getSites(): List<Map<String, String>> {
            return sites
        }

        fun getSiteIds(): List<String> {
            return sites.map { it["id"].toString() }.toMutableList()
        }

        fun getSiteNames(): List<String> {
            return sites.map { it["name"].toString() }.toMutableList()
        }

        fun getApp(site: String, idOne: String, idTwo: String? = null): AppInfoResult? {
            val i = getSiteIds().indexOf(site)
            Logger.d("getApp:$site/$idOne/$idTwo\nno:$i\n${getSiteIds()}")
            when (i) {
                0 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            return NewsSitesMod.githubReleaseMod(idOne, idTwo)
                        }
                    }
                }
                1 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return NewsSitesMod.CoolapkWeb(idOne)
                        }
                    }
                }
                2 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("Api token cannot empty")
                        else -> {
                            return NewsSitesMod.FirimApi(idOne, idTwo)
                        }
                    }
                }
                3 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return NewsSitesMod.WandoujiaWeb(idOne)
                        }
                    }
                }
                4 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return NewsSitesMod.MyAppWeb(idOne)
                        }
                    }
                }
                5 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return NewsSitesMod.FirimRsshub(idOne)
                        }
                    }
                }
            }
            return null
        }

    }


    class DataBase {
        private val dataBaseName = "app_down"
        private var book = Paper.book(dataBaseName)
        private val g = Gson()
        private fun write(file: String, dataBase: Any) {
            book.write(file, dataBase)
        }

        fun getAppfeedNameList(): MutableList<String> {
            val appfeedNames = mutableListOf<String>()
            getAppFeeds().map {
                appfeedNames.add(it.name)
            }
            return appfeedNames
        }

        fun getAppFeeds(): MutableList<AppDownFeed> {
            return book.read("feeds", mutableListOf())
        }

        fun updateFeedList(feeds: List<AppDownFeed>): Boolean {
            book = book.write("feeds", feeds)
            return getAppFeeds() == feeds
        }

        fun addFeed(appDownFeed: AppDownFeed): Boolean {
            val dataBase = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBase")
            dataBase.add(appDownFeed)
            this.write("feeds", dataBase)
            val dataBaseNew = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBaseNew")
            return dataBaseNew == dataBase
        }

        fun delFeed(feedNo: Int, appDownFeed: AppDownFeed): Boolean {
            val dataBase = this.getAppFeeds()
            if (dataBase.size <= feedNo) return false
            val delFeedItem = dataBase[feedNo]
            return if (delFeedItem == appDownFeed) {
                dataBase.removeAt(feedNo)
                Logger.d("Delete feed item")
                this.updateFeedList(dataBase)
            } else {
                Logger.e("Not this feed item")
                false
            }
        }

        fun importJson(json: String): Boolean {
            return try {
                val db = g.fromJson(json, Array<AppDownFeed>::class.java).toMutableList()
                this.updateFeedList(db)
            } catch (e: Exception) {
                false
            }
        }

        fun export2json(): String {
            val dataBase = getAppFeeds()
            return g.toJson(dataBase)
        }

    }

    class Other {
        fun appInfo2AppFeed(name: String, appInfo: AppInfo): AppDownFeed {
            return AppDownFeed(name, appInfo.id_one, appInfo.id_two, appInfo.site, "",
                    appInfo.version, appInfo.updateTime, appInfo.packageName, appInfo.webUrl, appInfo.fileList)
        }
    }
}