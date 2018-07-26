package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.gson.GithubReleaseItem
import com.zhihaofans.androidbox.util.ConvertUtil
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
        private val convertUtil = ConvertUtil()
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

        fun getApp(site: String, idOne: String, idTwo: String? = null): MutableList<MutableMap<String, Any>>? {
            val resultList = mutableListOf<MutableMap<String, Any>>()
            when (site) {
                "GITHUB_RELEASES" -> {
                    when {
                        idOne.isEmpty() -> throw Exception("Author cannot empty")
                        idTwo.isNullOrEmpty() -> throw Exception("project cannot empty or null")
                        else -> {
                            val githubReleaseResult = githubRelease(idOne, idTwo!!)
                            githubReleaseResult.map {
                                resultList.add(mutableMapOf(
                                        "html_url" to it.html_url,
                                        "name" to it.name,
                                        "tag_name" to it.tag_name,
                                        "update_time" to it.published_at,
                                        "file_list" to it.assets,
                                        "description" to it.body
                                ))
                            }
                            return resultList
                        }
                    }
                }
                else -> return null
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
        private val dataBaseName = "app_down"
        val book = Paper.book(dataBaseName)

        private fun update(key: String, valve: Any): Boolean {
            book.write(key, valve)
            return book.read<Any>(key) == valve
        }

        fun getAppFeeds(): MutableList<AppDownFeed> {
            Logger.d(book.path)
            return book.read("feeds", mutableListOf())
        }

        fun updateFeed(feeds: List<AppDownFeed>): Boolean {
            return this.update("feeds", feeds)
        }

        fun addFeed(name: String, site: String, idOne: String, idTwo: String? = null): Boolean {
            val dataBase = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBase")
            val appInfo = SiteParser().getApp(site, idOne, idTwo)
            if (appInfo == null) {
                Logger.d("appInfo\n$site/$idOne/$idTwo($name) is null")
                return false
            } else if (appInfo.size == 0) {
                Logger.d("appInfo\n$site/$idOne/$idTwo($name) size==0")
                return false
            }
            val lastUpdate = appInfo[0]
            val thisApp = AppDownFeed(dataBase.size, name, idOne, idTwo, site, (lastUpdate["name"] as String?)
                    ?: (lastUpdate["tag_name"] as String?)
                    ?: "", lastUpdate["update_time"] as String)
            dataBase.add(thisApp)
            book.write("feeds", dataBase)
            val dataBaseNew = this.getAppFeeds()
            Logger.d("appDownFeed:$dataBaseNew")
            return dataBaseNew == dataBase
        }

        fun delFeed(feedMo: Int, site: String, idOne: String, idTwo: String): Boolean {
            val dataBase = this.getAppFeeds()
            if (dataBase.size <= feedMo) return false
            val delFeedItem = dataBase[feedMo]
            return if (delFeedItem.site == site && delFeedItem.id_one == idOne && delFeedItem.id_one == idTwo) {
                dataBase.removeAt(feedMo)
                Logger.e("Delete feed item")
                this.updateFeed(dataBase)
            } else {
                Logger.e("Not this feed item")
                false
            }
        }

    }
}