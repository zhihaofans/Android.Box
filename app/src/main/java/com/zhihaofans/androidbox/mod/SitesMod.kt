package com.zhihaofans.androidbox.mod

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhihaofans.androidbox.data.*
import com.zhihaofans.androidbox.gson.FirimApiLatestUpdate
import com.zhihaofans.androidbox.gson.FirimApiLatestUpdateError
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.gson.RsshubFirimGson
import com.zhihaofans.androidbox.util.DatetimeOldUtil
import com.zhihaofans.androidbox.util.HttpUtil
import com.zhihaofans.androidbox.util.JsoupUtil
import com.zhihaofans.androidbox.util.LogUtil
import io.zhihao.library.android.kotlinEx.remove
import io.zhihao.library.android.util.DatetimeUtil
import io.zhihao.library.android.util.FileUtil
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL
import java.util.*

/**

 * @author: zhihaofans

 * @date: 2018-12-10 19:09

 */
class NewsSitesMod {
    companion object {
        private val g = Gson()
        private val defaultAppResult = AppInfoResult(false, "", -1, null)
        fun githubReleaseMod(author: String, project: String): AppInfoResult {
            var result = defaultAppResult
            if (author.isEmpty() && project.isEmpty()) {
                result.message = "错误，author或project为空"
            } else {
                val apiUrl = UrlMod.APP_GITHUB_RELEASE.replace("@author@", author).replace("@project@", project)
                val client = OkHttpClient()
                val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl)
                val request = requestBuilder.build()
                val call = client.newCall(request)
                try {
                    val response = call.execute()
                    if (response.body == null) {
                        result.message = ""
                    } else {
                        val jsonData = response.body!!.string()
                        result = githubApiReleaseJson2Class(author, project, jsonData)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
            return result
        }

        fun githubApiReleaseJson2Class(author: String, project: String, jsonData: String): AppInfoResult {
            val result = defaultAppResult
            val type = object : TypeToken<List<GithubReleaseItem>>() {}.type
            val github: List<GithubReleaseItem> = g.fromJson(jsonData, type)
            if (github.isEmpty()) {
                result.message = "错误：github.isEmpty"
            } else {
                val lastRelease = github[0]
                LogUtil.d(lastRelease)
                result.result = AppInfo(author, project, project, "GITHUB_RELEASES", author,
                        if (lastRelease.name.isNullOrEmpty()) lastRelease.tag_name else lastRelease.name,
                        DatetimeOldUtil.githubUtc2Local(lastRelease.published_at), null,
                        lastRelease.html_url, lastRelease.assets.map {
                    FileList(
                            it.name,
                            it.browser_download_url,
                            it.download_count.toString(),
                            it.updated_at,
                            FileUtil.fileSizeInt2string(it.size)
                    )
                }.toMutableList()
                )
                result.success = true
                result.code = 0
            }
            return result
        }

        fun CoolapkWeb(packageName: String): AppInfoResult {
            val webUrl = "https://www.coolapk.com/apk/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val result = defaultAppResult
            LogUtil.d(doc.html())
            val title = jsoupUtil.title()
            if (title != "出错了") {
                val body = jsoupUtil.html("body")
                val a = jsoupUtil.html("p.detail_app_title")
                val b = body.indexOf("window.location.href = \"") + 24
                val c = jsoupUtil.html("p.apk_topba_message").split(" / ")
                val appInfos = jsoupUtil.html("div.apk_left_title > p.apk_left_title_info", 1).split("<br>")
                //LogUtil.d("appInfos:size:${appInfos.size}\n$appInfos")
                val appName = a.substring(0, a.indexOf("<span class=\"list_app_info\">"))
                val appVersion = jsoupUtil.text("p.detail_app_title > span.list_app_info")
                val appSize = c[0]
                val downloadUrl = body.substring(b, body.indexOf("\"", b))
                //val author = if (appInfos.size != 4) "" else appInfos[3].split("：")[1]
                val rawTime = if (appInfos.size != 4) "" else appInfos[appInfos.size - 3].split("：")[1]
                var newTime = if (appInfos.size != 4) "" else rawTime.replace("-", "/")
                if (newTime.endsWith("天前")) newTime = DatetimeOldUtil.datePlus(DatetimeUtil.nowDatetime(), -(newTime.substring(0, newTime.length - 2).toIntOrNull()
                        ?: 0))
                LogUtil.d("rawTime:$rawTime\nnewTime:$newTime")
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

        fun FirimApi(pageageName: String, apiToken: String): AppInfoResult {
            val apiUrl = "https://api.fir.im/apps/latest/$pageageName?type=android&api_token=$apiToken"
            LogUtil.d("apiUrl:$apiUrl")
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
                if (response.body == null) {
                    result.code = 1
                    result.message = "Error: response.body is null"
                } else {
                    val jsonData = response.body!!.string()
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
                            LogUtil.d(latestUpdate.updated_at)
                            val updateTime = DatetimeUtil.unixTime2date("${latestUpdate.updated_at}000".toLong(), Locale.CHINA)
                            result.result = AppInfo(pageageName, apiToken, latestUpdate.name, "FIRIM_V1", "",
                                    latestUpdate.versionShort + "(${latestUpdate.version})",
                                    updateTime, pageageName, latestUpdate.update_url,
                                    mutableListOf(FileList(latestUpdate.versionShort + "(${latestUpdate.version})", latestUpdate.install_url,
                                            null, updateTime, FileUtil.fileSizeInt2string(latestUpdate.binary.fsize)))
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

        fun FirimRsshub(projectId: String): AppInfoResult {
            val apiUrl = "https://rsshub.app/fir/update/$projectId.json"
            LogUtil.d("apiUrl:$apiUrl")
            val client = OkHttpClient()
            val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(apiUrl).build()
            val call = client.newCall(request)
            val result = defaultAppResult
            if (projectId.isEmpty()) {
                result.code = 1
                result.message = "Error: Project id or Api token is empty"
                return result
            }
            try {
                val response = call.execute()
                if (response.body == null) {
                    result.code = 1
                    result.message = "Error: response.body is null"
                } else {
                    val jsonData = response.body!!.string()
                    if (jsonData.isEmpty()) {
                        result.code = 1
                        result.message = "Error: jsonData is null"
                    } else {
                        result.message = "Error:Gson to firimV2"
                        val rsshubFirimGsons = g.fromJson(jsonData, RsshubFirimGson::class.java)
                        if (rsshubFirimGsons.title == "RSSHub" || rsshubFirimGsons.home_page_url == "https://github.com/DIYgod/RSSHub") {
                            result.message = "Error:Title is RSSHub"
                        } else if (rsshubFirimGsons.items.size == 0) {
                            result.message = "Error:Not item"
                        } else {
                            result.message = ""
                            val webUrl = rsshubFirimGsons.home_page_url
                            val updateItem = rsshubFirimGsons.items[0]
                            val updateTime = updateItem.date_published.remove("&#34;")
                                    .replace("-", "/")
                                    .replace("T", " ")
                                    .replace(".000Z", "")
                            val updateVersion = updateItem.title
                            result.result = AppInfo(projectId, null, rsshubFirimGsons.title, "FIRIM_V2", "",
                                    updateVersion, updateTime, null, webUrl, mutableListOf())
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

        fun WandoujiaWeb(packageName: String): AppInfoResult {
            val webUrl = "https://www.wandoujia.com/apps/$packageName"
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            val result = defaultAppResult
            LogUtil.d(doc.html())
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
                    val author = jsoupUtil.html("span.dev-Sites")
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

        fun MyAppWeb(packageName: String): AppInfoResult {
            val webUrl = "http://android.myapp.com/myapp/detail.htm?apkName=$packageName"
            val result = defaultAppResult
            if (packageName.isEmpty()) return result
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            LogUtil.d(doc.html())
            val body = jsoupUtil.body()
            if (body == null) {
                result.message = "错误，找不到应用，服务器返回空白信息"
            } else {
                val emptyElement = jsoupUtil.textorNull("search-none-text")
                if (emptyElement.isNullOrEmpty()) {
                    val appName = jsoupUtil.htmlOrNull("div.det-name-int") ?: packageName
                    val appIcon = jsoupUtil.img("div.det-icon >img")
                    LogUtil.d("appIcon:$appIcon")
                    val appSize = jsoupUtil.html("div.det-size")
                    val downloadUrl = jsoupUtil.attr("a.det-down-btn", "data-apkurl")
                    LogUtil.d("downloadUrl:$downloadUrl")
                    val appVersion = jsoupUtil.html("div.det-othinfo-data", 0)
                    val updateUnixTime = jsoupUtil.attr("#J_ApkPublishTime", "data-apkpublishtime").toLong()
                    val updateTime = DatetimeUtil.unixTime2date(updateUnixTime * 1000, Locale.CHINA)
                    LogUtil.d("updateUnixTime:$updateUnixTime")
                    LogUtil.d("updateTime:$updateTime")
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

        /*fun ApkPure(packageName: String): AppInfoResult {
            val webUrl = "https://apkpure.com/cn/$packageName"
            val result = defaultAppResult
            if (packageName.isEmpty()) return result
            val doc = Jsoup.connect(webUrl).get()
            val jsoupUtil = JsoupUtil(doc)
            LogUtil.d(doc.html())
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
        }*/

    }
}

class XXDownSitesMod {
    companion object {
        fun AcfunVideoThumbnail(url: URL): XXDownResultData? {
            val call = HttpUtil.httpClientGetCall(url, XXDownSite.headerbuild)
            try {
                val response = call.execute()
                val body = response.body
                        ?: return XXDownResultData(false, "Http body is null", listOf())
                val bodyStr = body.string()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message})", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                LogUtil.d(bodyStr)
                val doc = Jsoup.parse(bodyStr)
                val jsoupUtil = JsoupUtil(doc)
                val image = jsoupUtil.attrOrNull("div#pageInfo", "data-pic")
                return when {
                    image == null -> XXDownResultData(false, "image is null", listOf())
                    image.isEmpty() -> XXDownResultData(false, "image is empty", listOf())
                    else -> XXDownResultData(true, "OK", listOf(XXDownResultUrlData(image, XXDownUrlType.image)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return XXDownResultData(false, "Http get error(Exception)", listOf())
            }
        }

        fun BilibiliVideoThumbnail(url: URL): XXDownResultData? {
            val newUrl = if (url.host == "acg.tv") {
                var videoId = if (url.path.indexOf("av") >= 0) url.path else "av" + url.path
                if (videoId.startsWith("/")) videoId = videoId.remove("/")
                URL("https://www.bilibili.com/video/$videoId")
            } else {
                url
            }
            val call = HttpUtil.httpClientGetCall(newUrl, XXDownSite.headerbuild)
            try {
                val response = call.execute()
                val body = response.body
                        ?: return XXDownResultData(false, "Http body is null", listOf())
                val bodyStr = body.string()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message})", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                LogUtil.d(bodyStr)
                val doc = Jsoup.parse(bodyStr)
                val jsoupUtil = JsoupUtil(doc)
                LogUtil.d(jsoupUtil.html("head > meta[itemprop='image']"))
                val image = jsoupUtil.attr("head > meta[itemprop=\"image\"]", "content")
                return when {
                    image.isEmpty() -> XXDownResultData(false, "image is empty", listOf())
                    else -> XXDownResultData(true, "OK", listOf(XXDownResultUrlData(image, XXDownUrlType.image)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return XXDownResultData(false, "Http get error(Exception)", listOf())
            }
        }

        fun githubReleaseXX(url: URL): XXDownResultData? {
            if (url.host.startsWith(UrlMod.XXDOWN_SITE_GITHUB_RELEASE)) {
                var mUrl = url.path
                if (mUrl.endsWith("/")) mUrl = mUrl.substring(0, mUrl.length - 2)
                val mList = mUrl.split("/")
                if (mList.size != 2) return null
                val author = mList[0]
                val project = mList[1]
                val githubReleaseMod = NewsSitesMod.githubReleaseMod(author, project)
                try {
                    if (!githubReleaseMod.success) return XXDownResultData(false, "Get error(${githubReleaseMod.message})", listOf())
                    if (githubReleaseMod.result == null) return XXDownResultData(false, "Result is null", listOf())
                    val fileList = githubReleaseMod.result!!.fileList
                    return when {
                        fileList.isNullOrEmpty() -> XXDownResultData(false, "fileList is null or empty", listOf())
                        else -> XXDownResultData(true, "OK", fileList.map {
                            XXDownResultUrlData(it.url, XXDownUrlType.other)
                        }.toList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return XXDownResultData(false, "Need Github release url", listOf())
                }
            } else {
                return null
            }
        }

        fun instagram(url: URL): XXDownResultData? {
            LogUtil.d("instagram($url")
            return try {
                if (url.host == UrlMod.XXDOWN_SITE_HOST_INSTAGRAM) {
                    val htmlText = HttpUtil.httpPostString(UrlMod.XXDOWN_SITE_10INSTA, mutableMapOf("url" to url.toString()), XXDownSite.headers_map)
                            ?: return null
                    val imageVideoData = HtmlParserMod.tenInsta(htmlText) ?: return null
                    if (imageVideoData.item.isEmpty()) return null
                    val itemList = imageVideoData.item.map {
                        val itemType = if (it.isVideo) XXDownUrlType.video else XXDownUrlType.image
                        var mUrl = it.url
                        if (!mUrl.startsWith("https://www.10insta.net/")) mUrl = "https://www.10insta.net/$mUrl"
                        XXDownResultUrlData(mUrl, itemType)
                    }
                    XXDownResultData(true, "", itemList)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun twitter(url: URL): XXDownResultData? {
            LogUtil.d("twitter($url")
            return try {
                if (url.host == UrlMod.XXDOWN_SITE_HOST_TWITTER) {
                    val htmlText = HttpUtil.httpGetString(UrlMod.XXDOWN_SITE_TUBEOFFLINE.format("Twitter", url), XXDownSite.headers_map)
                            ?: return null
                    val imageVideoData = HtmlParserMod.tubeoffline(htmlText) ?: return null
                    if (imageVideoData.item.isEmpty()) return null
                    val itemList = imageVideoData.item.map {
                        val itemType = if (it.isVideo) XXDownUrlType.video else XXDownUrlType.image
                        val mUrl = it.url
                        XXDownResultUrlData(mUrl, itemType)
                    }
                    XXDownResultData(true, "", itemList)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}