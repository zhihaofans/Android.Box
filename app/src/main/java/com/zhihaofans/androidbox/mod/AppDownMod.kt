package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.database.AppFeed
import com.zhihaofans.androidbox.database.AppUpdate
import com.zhihaofans.androidbox.database.FileList
import com.zhihaofans.androidbox.gson.CoolapkAppInfo
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.gson.GithubReleaseItemAsset
import com.zhihaofans.androidbox.util.ConvertUtil
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
        private val convertUtil = ConvertUtil()
        private val g = Gson()
        private var mcontext: Context? = null
        private val sites: List<Map<String, String>> = mutableListOf(
                mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases", "version" to "1"),// Github releases
                mutableMapOf("id" to "COOLAPK_WEB", "name" to "Coolapk v1", "version" to "1")// Github releases
        )
        private val siteIds = sites.map { it["id"].toString() }
        private val siteNames = sites.map { it["id"].toString() }
        fun init(context: Context): Context? {
            this.mcontext = context
            return this.mcontext
        }

        fun getSites(): List<Map<String, String>> {
            return sites
        }

        fun getSiteIds(): List<String> {
            return siteIds
        }

        fun getSiteNames(): List<String> {
            return siteNames
        }

        fun getAppUpdates(site: String, idOne: String, idTwo: String? = null): MutableList<AppUpdate>? {
            val siteId = getSites().map { it["id"] }
            var resultList = mutableListOf<AppUpdate>()
            when (site) {
                siteId[0] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            val githubReleaseResult = githubRelease(idOne, idTwo!!)
                            resultList = githubReleaseResult.map {
                                this.appUpdate(if (it.name.isNullOrEmpty()) it.tag_name else it.name.toString(),
                                        idOne, idTwo, site, it.body, it.published_at, it.html_url,
                                        this.fileList("GITHUB_RELEASES", it.assets)
                                )
                            }.toMutableList()
                        }
                    }
                }
                siteId[1] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            val coolapkReleaseResult = coolapkRelease(idOne)
                            resultList.add(appUpdate(appFeed(coolapkReleaseResult!!)))
                        }
                    }
                }
                else -> return null
            }
            return resultList
        }

        private fun appFeed(coolapk: CoolapkAppInfo): AppFeed {
            return AppFeed(coolapk.name, coolapk.packageName, null, siteIds[1],
                    coolapk.version, coolapk.updateTime, coolapk.packageName, coolapk.webUrl,
                    mutableListOf(AppUpdate(
                            coolapk.version, coolapk.packageName, null, siteIds[1],
                            coolapk.packageName, coolapk.updateTime, coolapk.webUrl, coolapk.packageName,
                            fileList(siteIds[1], coolapk)
                    )
                    ))


        }

        private fun appUpdate(appDownFeed: AppDownFeed): AppUpdate {
            return this.appUpdate(appDownFeed.name, appDownFeed.id_one,
                    appDownFeed.id_two, appDownFeed.site,
                    appDownFeed.name, appDownFeed.updateTime,
                    appDownFeed.updateTime, appDownFeed.fileList)
        }

        private fun appUpdate(appFeed: AppFeed): AppUpdate {
            return this.appUpdate(appFeed.name, appFeed.id_one,
                    appFeed.id_two, appFeed.site,
                    appFeed.name, appFeed.updateTime,
                    appFeed.webUrl, appFeed.appUpdate[0].fileList)
        }

        private fun appUpdate(name: String, idOne: String, idTwo: String?, site: String, description: String?,
                              updateTime: String, webUrl: String = "", fList: MutableList<FileList>?): AppUpdate {
            return AppUpdate(name, idOne, idTwo, site, description
                    ?: "", updateTime, webUrl, null, fList
                    ?: mutableListOf())
        }

        private fun fileList(site: String, list: Any): MutableList<FileList> {
            val fList = mutableListOf<FileList>()
            return when (site) {
                siteIds[0] -> {
                    (list as MutableList<*>).map {
                        val thisItem = it as GithubReleaseItemAsset
                        fList.add(FileList(
                                thisItem.id.toString(),
                                thisItem.name,
                                thisItem.browser_download_url,
                                thisItem.download_count,
                                thisItem.updated_at,
                                thisItem.size, convertUtil.fileSizeInt2string(thisItem.size)
                        ))
                    }
                    fList
                }
                siteIds[1] -> {
                    val coolapk = list as CoolapkAppInfo
                    mutableListOf(FileList(coolapk.version, coolapk.version,
                            coolapk.downloadUrl, 0, coolapk.updateTime, 0, "未知大小"

                    ))
                }
                else -> fList
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
            val webUrl = "https://www.coolapk.com/apk/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val title = jsoupUtil.title()
            return if (title == "出错了") {
                val body = jsoupUtil.html("body")
                val a = jsoupUtil.html("p.detail_app_title")
                val b = body.indexOf("window.location.href = \"") + 24
                val appinfoString = jsoupUtil.html("div.apk_left_title > p.apk_left_title_info:last")
                val appInfos = appinfoString.split("<br>")
                val appName = a.substring(0, a.indexOf("<span class=\"list_app_info\">"))
                val appVersion = jsoupUtil.text("p.detail_app_title -> span.list_app_info")
                val downloadUrl = body.substring(b, body.indexOf("\"", b))
                val author = if (appInfos.size != 4) "" else appInfos[3]
                val updateTime = if (appInfos.size != 4) "" else appInfos[1]
                CoolapkAppInfo(packageName, appName, appVersion, downloadUrl, updateTime, author, webUrl)
            } else {
                null
            }
        }
    }

    class DataBase {
        private val dataBaseName = "app_down"
        private var book = Paper.book(dataBaseName)
        private var dataBasePath = book.path
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

        fun appDownFeed(name: String, appUpdate: AppUpdate): AppDownFeed {
            return AppDownFeed(getAppFeeds().size, name, appUpdate.idOne, appUpdate.idTwo,
                    appUpdate.site, appUpdate.name, appUpdate.updateTime, appUpdate.packageName, appUpdate.fileList)
        }
    }
}