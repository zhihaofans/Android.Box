package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.database.AppInfo
import com.zhihaofans.androidbox.database.AppInfoResult
import com.zhihaofans.androidbox.database.FileList
import com.zhihaofans.androidbox.gson.FirimApiDownloadToken
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.util.ConvertUtil
import com.zhihaofans.androidbox.util.JsoupUtil
import com.zhihaofans.androidbox.util.SystemUtil
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
        private val s = Site()
        private var mcontext: Context? = null
        private val sites: List<Map<String, String>> = mutableListOf(
                mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases", "version" to "1"),// Github releases
                mutableMapOf("id" to "COOLAPK_WEB", "name" to "Coolapk v1", "version" to "1")// Github releases
        )
        private val siteIds = sites.map { it["id"].toString() }
        private val siteNames = sites.map { it["name"].toString() }
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

        fun getApp(site: String, idOne: String, idTwo: String? = null): AppInfo? {
            when (site) {
                siteIds[0] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            return s.Github(idOne, idTwo!!)
                        }
                    }
                }
                siteIds[1] -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return s.CoolapkV1(idOne)
                        }
                    }
                }
            }
            return null
        }

    }

    class Site {
        private val g = Gson()
        private val convertUtil = ConvertUtil()
        fun Github(author: String, project: String): AppInfo? {
            val apiUrl = "https://api.github.com/repos/$author/$project/releases"
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl)
            val request = requestBuilder.build()
            val call = client.newCall(request)
            return try {
                val response = call.execute()
                if (response.body() == null) {
                    null
                } else {
                    val jsonData = response.body()!!.string()
                    val type = object : TypeToken<List<GithubReleaseItem>>() {}.type
                    val github: List<GithubReleaseItem> = g.fromJson(jsonData, type)
                    if (github.isEmpty()) {
                        null
                    } else {
                        val lastRelease = github[0]
                        Logger.d(lastRelease)

                        AppInfo(author, project, project, "GITHUB_RELEASES", if (lastRelease.name.isNullOrEmpty()) lastRelease.tag_name else (lastRelease.name
                                ?: lastRelease.tag_name), convertUtil.githubUtc2Local(lastRelease.published_at), null,
                                lastRelease.html_url, lastRelease.assets.map {
                            FileList(
                                    it.name,
                                    it.browser_download_url,
                                    it.download_count.toString(),
                                    it.updated_at,
                                    convertUtil.fileSizeInt2string(it.size)
                            )
                        }.toMutableList()
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        fun CoolapkV1(packageName: String): AppInfo? {
            val webUrl = "https://www.coolapk.com/apk/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val sysUtil = SystemUtil()
            Logger.d(doc.html())
            val title = jsoupUtil.title()
            return if (title != "出错了") {
                val body = jsoupUtil.html("body")
                val a = jsoupUtil.html("p.detail_app_title")
                val b = body.indexOf("window.location.href = \"") + 24
                val c = jsoupUtil.html("p.apk_topba_message").split(" / ")
                val appInfos = jsoupUtil.html("div.apk_left_title > p.apk_left_title_info", 1).split("<br>")
                //Logger.d("appInfos:size:${appInfos.size}\n$appInfos")
                val appName = a.substring(0, a.indexOf("<span class=\"list_app_info\">"))
                val appVersion = jsoupUtil.text("p.detail_app_title > span.list_app_info")
                val appSize = c[0]
                val downloadUrl = body.substring(b, body.indexOf("\"", b))
                //val author = if (appInfos.size != 4) "" else appInfos[3].split("：")[1]
                val rawTime = if (appInfos.size != 4) "" else appInfos[appInfos.size - 3].split("：")[1]
                var newTime = if (appInfos.size != 4) "" else rawTime.replace("-", "/")
                if (newTime.endsWith("天前")) newTime = sysUtil.datePlus(sysUtil.nowDate(), -(newTime.substring(0, newTime.length - 2).toIntOrNull()
                        ?: 0))
                Logger.d("rawTime:$rawTime\nnewTime:$newTime")
                val updateTime = if (newTime.isEmpty()) rawTime else newTime
                val downCount = c[1]
                AppInfo(packageName, null, appName, "COOLAPK_WEB", appVersion, updateTime, packageName, webUrl,
                        mutableListOf(FileList(appVersion, downloadUrl, downCount, updateTime, appSize))
                )
            } else {
                null
            }
        }

        fun FirimV1(appId: String, apiToken: String): AppInfoResult {
            val tokenUrl = "http://api.fir.im/apps/$appId/download_token?api_token=$apiToken"
            val apiUrl = "http://api.fir.im/apps/$appId?api_token=$apiToken"
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(tokenUrl)
            val request = requestBuilder.build()
            val call = client.newCall(request)
            val result = AppInfoResult(false, "", -1, null)
            if (appId.isEmpty() || apiToken.isEmpty()) {
                result.code = 1
                result.message = "Error: App id or Api token is empty"
                return result
            }
            try {
                val response = call.execute()
                if (response.body() == null) {
                    result.code = 1
                    result.message = "Error: response.body is null"
                } else {
                    val jsonData = response.body()!!.string()
                    result.message = "Error:Gson to firimV1"
                    val firimV1 = g.fromJson(jsonData, FirimApiDownloadToken::class.java)
                    result.message = ""
                    if (firimV1.code != null) {
                        result.code = 2
                        result.message = "Error! Code:${firimV1.code}"
                        val errors = firimV1.errors.exception
                        var errorIndex = 0
                        errors.map {
                            result.message += when (errorIndex) {
                                0 -> ", message:$it"
                                else -> "|$it"
                            }
                            errorIndex++
                            it
                        }
                    } else {
                        val downloadToken = firimV1.download_token
                        if (downloadToken.isNullOrEmpty()) {
                            result.code = 3
                            result.message = "Error: Download token is null or empty"
                        } else {
                            val downloadUrl = "http://download.fir.im/apps/$appId/install?download_token=$downloadToken"
                            result.result = AppInfo(appId, apiToken, project, "FIRIM_V1", if (lastRelease.name.isNullOrEmpty()) lastRelease.tag_name else (lastRelease.name
                                    ?: lastRelease.tag_name), convertUtil.githubUtc2Local(lastRelease.published_at), null,
                                    lastRelease.html_url, lastRelease.assets.map {
                                FileList(
                                        it.name,
                                        it.browser_download_url,
                                        it.download_count.toString(),
                                        it.updated_at,
                                        convertUtil.fileSizeInt2string(it.size)
                                )
                            }.toMutableList()
                            )
                            result.success = true
                            result.code = 0
                            result.message = "Success"
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                result.code = -1
                if (result.message.isNotEmpty()) {
                    result.message = "Error:IOException"
                }
            } finally {
                return result
            }
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
            return AppDownFeed(name, appInfo.id_one, appInfo.id_two, appInfo.site, appInfo.version, appInfo.updateTime, appInfo.packageName, appInfo.webUrl, appInfo.fileList)
        }
    }
}