package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.database.AppUpdate
import com.zhihaofans.androidbox.database.FileList
import com.zhihaofans.androidbox.gson.CoolapkAppInfo
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.gson.GithubReleaseItemAsset
import com.zhihaofans.androidbox.util.JsoupUtil
import io.paperdb.Paper
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

/**
 * Created by zhihaofans on 2018/7/25.
 */
class AppDownMod {

    class SiteParser {
        private val g = Gson()
        private var mcontext: Context? = null
        private val sites: List<Map<String, String>> = mutableListOf(
                mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases", "version" to "1"),// Github releases
                mutableMapOf("id" to "COOLAPK_WEB", "name" to "Coolapk v1", "version" to "1")// Github releases
        )

        fun init(context: Context): Context? {
            this.mcontext = context
            return this.mcontext
        }

        fun getSites(): List<Map<String, String>> {
            return sites
        }

        fun getAppUpdates(site: String, idOne: String, idTwo: String? = null): MutableList<AppUpdate>? {
            val siteId = getSites().map { it["id"] }
            val resultList: MutableList<AppUpdate>
            when (site) {
                siteId[0] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            val githubReleaseResult = githubRelease(idOne, idTwo!!)
                            resultList = githubReleaseResult.map {
                                this.appUpdate(if (it.name.isNullOrEmpty()) it.tag_name else it.name.toString(),
                                        idOne, idTwo, site, it.body, it.published_at, it.html_url, this.fileList("GITHUB_RELEASES", it.assets)
                                )
                            }.toMutableList()
                            return resultList
                        }
                    }
                }
                siteId[1] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            /*
                            val coolapkReleaseResult = coolapkRelease(idOne)
                            resultList = coolapkReleaseResult.map {
                                this.appUpdate(if (it.name.isNullOrEmpty()) it.tag_name else it.name.toString(),
                                        idOne, idTwo, site, it.body, it.published_at, it.html_url, this.fileList("GITHUB_RELEASES", it.assets)
                                )
                            }.toMutableList()
                            */
                            return mutableListOf()
                        }
                    }
                }
                else -> return null
            }
        }

        private fun appUpdate(appDownFeed: AppDownFeed): AppUpdate {
            return this.appUpdate(appDownFeed.name, appDownFeed.id_one,
                    appDownFeed.id_two, appDownFeed.site,
                    appDownFeed.name, appDownFeed.updateTime,
                    appDownFeed.updateTime, appDownFeed.fileList)
        }

        private fun appUpdate(name: String, idOne: String, idTwo: String?, site: String, description: String?,
                              updateTime: String, webUrl: String = "", fList: MutableList<FileList>?): AppUpdate {
            return AppUpdate(name, idOne, idTwo, site, description ?: "", updateTime, webUrl, fList
                    ?: mutableListOf())
        }

        private fun fileList(site: String, list: MutableList<*>): MutableList<FileList>? {
            val fList = mutableListOf<FileList>()
            return when (site) {
                "GITHUB_RELEASES" -> {
                    list.map {
                        val thisItem = it as GithubReleaseItemAsset
                        fList.add(FileList(
                                thisItem.id.toString(),
                                thisItem.name,
                                thisItem.browser_download_url,
                                thisItem.download_count,
                                thisItem.updated_at,
                                thisItem.size
                        ))
                    }
                    fList
                }
                else -> null
            }
        }

        private fun githubRelease(author: String, project: String): List<GithubReleaseItem> {
            val site = sites[0]["id"].toString()
            val apiUrl = "https://api.github.com/repos/$author/$project/releases"
            val releasesList = mutableListOf<GithubReleaseItem>()
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl)
            val request = requestBuilder.build()
            val call = client.newCall(request)
            return try {
                val response = call.execute()
                if (response.body() == null) {
                    releasesList
                } else {
                    val jsonData = response.body()!!.string()
                    val type = object : TypeToken<ArrayList<GithubReleaseItem>>() {}.type
                    val githubRelease: List<GithubReleaseItem> = g.fromJson(jsonData, type)
                    githubRelease
                }
            } catch (e: IOException) {
                e.printStackTrace()
                releasesList
            }
        }

        private fun coolapkRelease(packageName: String): CoolapkAppInfo? {
            val appUpdates = mutableListOf<AppUpdate>()
            val doc = Jsoup.connect("https://www.coolapk.com/apk/").get()
            val jsoupUtil = JsoupUtil(doc)
            val title = jsoupUtil.title()
            return if (title == "出错了") {
                val a = jsoupUtil.html("p.detail_app_title")
                val appName = a.substring(0, a.indexOf("<span class=\"list_app_info\">"))
                val appVersion = jsoupUtil.text("p.detail_app_title -> span.list_app_info")
                //TODO:CoolapkAppInfo(appName,appVersion)
                null
            } else {
                null
            }
        }
    }

    class DataBase {
        private val dataBaseName = "app_down"
        private var book = Paper.book(dataBaseName)
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
            Logger.d(book.path)
            return book.read("feeds", mutableListOf())
        }

        fun updateFeedList(feeds: List<AppDownFeed>): Boolean {
            book = book.write("feeds", feeds)
            return getAppFeeds() == feeds
        }

        fun addFeed(name: String, appUpdate: AppUpdate): Boolean {
            val dataBase = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBase")
            dataBase.add(appDownFeed(name, appUpdate))
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

        fun appDownFeed(name: String, appUpdate: AppUpdate): AppDownFeed {
            return AppDownFeed(getAppFeeds().size, name, appUpdate.idOne, appUpdate.idTwo, appUpdate.site, appUpdate.name, appUpdate.updateTime, appUpdate.fileList)
        }
    }
}