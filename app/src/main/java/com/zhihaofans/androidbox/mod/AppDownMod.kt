package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.AppDownFeed
import com.zhihaofans.androidbox.data.AppInfo
import com.zhihaofans.androidbox.data.AppInfoResult
import com.zhihaofans.androidbox.data.FileList
import com.zhihaofans.androidbox.gson.FirimApiLatestUpdate
import com.zhihaofans.androidbox.gson.FirimApiLatestUpdateError
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.kotlinEx.find
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
        private var sites: List<Map<String, String>> = mutableListOf()
        fun init(context: Context): Context? {
            this.mcontext = context
            sites = mutableListOf(
                    mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases", "version" to "1"),// Github releases
                    mutableMapOf("id" to "COOLAPK_WEB", "name" to mcontext!!.getString(R.string.text_coolapk) + " v1", "version" to "1"),// CoolApk Web
                    mutableMapOf("id" to "FIRIM_V1", "name" to "Fir.im v1", "version" to "1"),// Fir.im v1 (Api)
                    mutableMapOf("id" to "WANDOUJIA_V1", "name" to mcontext!!.getString(R.string.text_wandoujia) + " v1", "version" to "1"),// Wandoujia v1
                    mutableMapOf("id" to "MYAPP", "name" to mcontext!!.getString(R.string.text_myapp) + " v1", "version" to "1"),// Tencent yingyongbao
                    mutableMapOf("id" to "APKPURE", "name" to "ApkPure" + " v1", "version" to "1")// ApkPure
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
                            return s.Github(idOne, idTwo ?: "")
                        }
                    }
                }
                1 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return s.CoolapkV1(idOne)
                        }
                    }
                }
                2 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("Api token cannot empty")
                        else -> {
                            return s.FirimV1(idOne, idTwo ?: "")
                        }
                    }
                }
                3 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return s.WandoujiaV1(idOne)
                        }
                    }
                }
                4 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return s.MyApp(idOne)
                        }
                    }
                }
                5 -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Package name cannot empty")
                        else -> {
                            return s.ApkPure(idOne)
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
        private val defaultAppResult = AppInfoResult(false, "", -1, null)
        val sysUtil = SystemUtil()
        fun Github(author: String, project: String): AppInfoResult {
            val result = defaultAppResult
            if (author.isEmpty() && project.isEmpty()) {
                result.message = "错误，author或project为空"
            } else {
                val apiUrl = "https://api.github.com/repos/$author/$project/releases"
                val client = OkHttpClient()
                val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl)
                val request = requestBuilder.build()
                val call = client.newCall(request)
                try {
                    val response = call.execute()
                    if (response.body() == null) {
                        result.message = ""
                    } else {
                        val jsonData = response.body()!!.string()
                        val type = object : TypeToken<List<GithubReleaseItem>>() {}.type
                        val github: List<GithubReleaseItem> = g.fromJson(jsonData, type)
                        if (github.isEmpty()) {
                            result.message = "错误：github.isEmpty"
                        } else {
                            val lastRelease = github[0]
                            Logger.d(lastRelease)
                            result.result = AppInfo(author, project, project, "GITHUB_RELEASES", author, if (lastRelease.name.isNullOrEmpty()) lastRelease.tag_name else (lastRelease.name
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
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
            return result
        }

        fun CoolapkV1(packageName: String): AppInfoResult {
            val webUrl = "https://www.coolapk.com/apk/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val result = defaultAppResult
            Logger.d(doc.html())
            val title = jsoupUtil.title()
            if (title != "出错了") {
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
                result.result = AppInfo(packageName, null, appName, "COOLAPK_WEB", "", appVersion, updateTime, packageName, webUrl,
                        mutableListOf(FileList(appVersion, downloadUrl, downCount, updateTime, appSize))
                )
                result.success = true
                result.code = 0
            } else {
                result.message = "错误，找不到应用，服务器返回信息：$title"
            }
            return result
        }

        fun FirimV1(pageageName: String, apiToken: String): AppInfoResult {
            val apiUrl = "https://api.fir.im/apps/latest/$pageageName?type=android&api_token=$apiToken"
            Logger.d("apiUrl:$apiUrl")
            val client = OkHttpClient()
            val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl).build()
            val call = client.newCall(request)
            val result = defaultAppResult
            if (pageageName.isEmpty() || apiToken.isEmpty()) {
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
                    if (jsonData.isEmpty()) {
                        result.code = 1
                        result.message = "Error: jsonData is null"

                    } else {
                        result.message = "Error:Gson to firimV1"
                        val firimApiLatestUpdateError = g.fromJson(jsonData, FirimApiLatestUpdateError::class.java)
                        result.message = ""
                        if (firimApiLatestUpdateError.code != null && firimApiLatestUpdateError.errors != null) {
                            result.code = 2
                            result.message = "Error! Code:${firimApiLatestUpdateError.code}"
                            val errors = firimApiLatestUpdateError.errors.exception
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
                            result.message = "Error:Gson to firimV1"
                            val latestUpdate = g.fromJson(jsonData, FirimApiLatestUpdate::class.java)
                            result.message = ""
                            Logger.d(latestUpdate.updated_at)
                            val updateTime = convertUtil.unixTime2date("${latestUpdate.updated_at}000".toLong())
                            result.result = AppInfo(pageageName, apiToken, latestUpdate.name, "FIRIM_V1", "",
                                    latestUpdate.versionShort + "(${latestUpdate.version})",
                                    updateTime, pageageName, latestUpdate.update_url,
                                    mutableListOf(FileList(latestUpdate.versionShort + "(${latestUpdate.version})", latestUpdate.install_url,
                                            null, updateTime, convertUtil.fileSizeInt2string(latestUpdate.binary.fsize)))
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

        fun WandoujiaV1(packageName: String): AppInfoResult {
            val webUrl = "https://www.wandoujia.com/apps/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val result = defaultAppResult
            Logger.d(doc.html())
            val title = jsoupUtil.title()
            val body = jsoupUtil.body()
            if (body == null) {
                result.message = "错误，找不到应用，服务器返回信息"
            } else {
                val webCode = body.attr("param-f") ?: "404"
                if (webCode.isEmpty() || webCode != "detail" || title == "豌豆荚") {
                    result.message = "错误，找不到应用，服务器返回信息"
                } else {
                    val appName = body.attr("data-title") ?: packageName
                    val appVersion = jsoupUtil.html("dl.infos-list > dd", 3).replace("&nbsp;", "")
                    val appSize = jsoupUtil.attr("dl.infos-list > dd > meta", "content")
                    val downloadUrl = jsoupUtil.link("a.install-btn")
                    val author = jsoupUtil.html("span.dev-sites")
                    val updateTime = jsoupUtil.attr("time#baidu_time", "datetime")
                    val downCount = jsoupUtil.html("span.item.install > i")
                    result.result = AppInfo(packageName, null, appName, "WANDOUJIA_V1", author, appVersion, updateTime, packageName, webUrl,
                            mutableListOf(FileList(appVersion, downloadUrl, downCount, updateTime, appSize))
                    )
                    result.success = true
                    result.code = 0
                }
            }
            return result
        }

        fun MyApp(packageName: String): AppInfoResult {
            val webUrl = "http://android.myapp.com/myapp/detail.htm?apkName=$packageName"
            val result = defaultAppResult
            if (packageName.isEmpty()) return result
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            Logger.d(doc.html())
            val body = jsoupUtil.body()
            if (body == null) {
                result.message = "错误，找不到应用，服务器返回空白信息"
            } else {
                val emptyElement = jsoupUtil.textorNull("search-none-text")
                if (emptyElement.isNullOrEmpty()) {
                    val appName = jsoupUtil.htmlorNull("div.det-name-int") ?: packageName
                    val appIcon = jsoupUtil.img("div.det-icon >img")
                    Logger.d("appIcon:$appIcon")
                    val appSize = jsoupUtil.html("div.det-size")
                    val downloadUrl = jsoupUtil.attr("a.det-down-btn", "data-apkurl")
                    Logger.d("downloadUrl:$downloadUrl")
                    val appVersion = jsoupUtil.html("div.det-othinfo-data", 0)
                    val updateUnixTime = jsoupUtil.attr("#J_ApkPublishTime", "data-apkpublishtime").toLong()
                    val updateTime = convertUtil.unixTime2date(updateUnixTime * 1000)
                    Logger.d("updateUnixTime:$updateUnixTime")
                    Logger.d("updateTime:$updateTime")
                    val author = jsoupUtil.html("div.det-othinfo-data", 2)
                    val downCount = jsoupUtil.html("div.det-ins-num")
                    result.result = AppInfo(packageName, null, appName, "MYAPP", author, appVersion, updateTime, packageName, webUrl,
                            mutableListOf(FileList(appVersion, downloadUrl, downCount, updateTime, appSize))
                    )
                    result.success = true
                    result.code = 0
                } else {
                    result.message = "错误，找不到应用，服务器返回信息($emptyElement)"
                }
            }
            return result
        }

        fun ApkPure(packageName: String): AppInfoResult {
            val webUrl = "https://apkpure.com/cn/$packageName"
            val result = defaultAppResult
            if (packageName.isEmpty()) return result
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            Logger.d(doc.html())
            val body = jsoupUtil.body()
            if (body == null) {
                result.message = "错误，找不到应用，服务器返回空白信息"
            } else {
                val webTitle = jsoupUtil.titleOrNull()
                if (webTitle.isNullOrEmpty() || webTitle == "404") {
                    result.message = "错误，找不到应用"
                } else {
                    val appInfoElement = jsoupUtil.findInElementsText("script[type=\"text/javascript\"]", "var comment_config = {")
                    if (appInfoElement.size != 1) {
                        result.message = "错误，获取应用信息失败 (appInfoElement.size != 1)"
                    } else {
                        val appInfoStr = appInfoElement[0].text()
                        if (appInfoStr.isEmpty()) {
                            result.message = "错误，获取应用信息失败 (appInfoStr.isEmpty)"
                        } else {
                            var appInfoStartFrom = appInfoStr.find("var comment_config = {")
                            if (appInfoStartFrom == -1) {
                                result.message = "错误，获取应用信息失败 (appInfoStartFrom.size == 1)"
                            } else {
                                appInfoStartFrom += 22
                                val appName = jsoupUtil.textorNull("div.title-like > h1")
                                        ?: packageName
                                val appIcon = jsoupUtil.img("div.icon > img")
                                val appSize = jsoupUtil.text(".ny-down > a > span.fsize > span")
                                val author = jsoupUtil.html("ul.version-ul > li > p", 1)
                                val appVersion = jsoupUtil.html("ul.version-ul > li > p", 3)
                                val updateTime = jsoupUtil.html("ul.version-ul > li > p", 5)
                                val mobileUrl = "https://m.apkpure.com/store/apps/details?id=$packageName"
                                result.result = AppInfo(packageName, null, appName, "APKPURE",
                                        author, appVersion, updateTime, packageName, mobileUrl, mutableListOf()
                                )
                                result.success = true
                                result.code = 0
                            }

                        }
                    }
                }
            }
            return result
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
            return AppDownFeed(name, appInfo.id_one, appInfo.id_two, appInfo.site, "", appInfo.version, appInfo.updateTime, appInfo.packageName, appInfo.webUrl, appInfo.fileList)
        }
    }
}