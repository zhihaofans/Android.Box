package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.database.AppInfo
import com.zhihaofans.androidbox.database.FileList
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.gson.GithubReleaseItemAsset
import com.zhihaofans.androidbox.util.SystemUtil
import io.paperdb.Paper
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Created by zhihaofans on 2018/7/25.
 */
class AppDownMod {

    class SiteParser {
        private val g = Gson()
        private var mcontext: Context? = null
        private val sites: List<Map<String, String>> = mutableListOf(
                mutableMapOf("id" to "GITHUB_RELEASES", "name" to "Github releases")// Github releases
        )

        fun init(context: Context): Context? {
            this.mcontext = context
            return this.mcontext
        }

        fun getSites(): List<Map<String, String>> {
            return sites
        }

        fun getAppUpdates(site: String, idOne: String, idTwo: String? = null): MutableList<AppInfo>? {
            val resultList = mutableListOf<AppInfo>()
            when (site) {
                "GITHUB_RELEASES" -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            val githubReleaseResult = githubRelease(idOne, idTwo!!)
                            githubReleaseResult.map {
                                resultList.add(this.app(if (it.name.isNullOrEmpty()) it.tag_name else it.name.toString(),
                                        idOne, idTwo, site, it.body, it.published_at, it.html_url, this.fileList("GITHUB_RELEASES", it.assets)
                                ))

                            }
                            return resultList
                        }
                    }
                }
                else -> return null
            }
        }

        private fun app(appDownFeed: AppDownFeed) {

        }

        private fun app(name: String, idOne: String, idTwo: String?, site: String, description: String?, updateTime: String, webUrl: String = "", fList: MutableList<FileList>?): AppInfo {
            return AppInfo(name, idOne, idTwo, site, description ?: "", updateTime, webUrl, fList
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

        fun githubRelease(author: String, project: String): List<GithubReleaseItem> {
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
    }

    class DataBase {
        private val sysUtil = SystemUtil()
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

        fun addFeed(name: String, appInfo: AppInfo): Boolean {
            val dataBase = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBase")
            dataBase.add(appDownFeed(name, appInfo))
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

        fun appDownFeed(name: String, appInfo: AppInfo): AppDownFeed {
            return AppDownFeed(getAppFeeds().size, name, appInfo.idOne, appInfo.idTwo, appInfo.site, appInfo.name, appInfo.updateTime, appInfo.fileList)
        }
    }
}