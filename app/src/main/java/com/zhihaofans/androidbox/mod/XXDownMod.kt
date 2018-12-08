package com.zhihaofans.androidbox.mod

import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.data.XXDownResultData
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.util.HttpUtil
import com.zhihaofans.androidbox.util.JsoupUtil
import okhttp3.CacheControl
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

/**
 * Created by zhihaofans on 2018/10/2.
 */
class XXDownMod {
    companion object {
        fun get(site: String, url: String): XXDownResultData? {
            return when (site) {
                ItemNameMod.XXDOWN_SITE_ACFUN_VIDEO -> XXDownSite.acfun(url)
                ItemNameMod.XXDOWN_SITE_BILIBILI_VIDEO -> XXDownSite.bilibili(url)
                else -> null
            }

        }
    }
}

class XXDownSite {
    companion object {
        private val headers_map = mutableMapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"
        )
        var headerbuild = Headers.of(headers_map)
        private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
        fun acfun(url: String): XXDownResultData? {
            val client = OkHttpClient()
            val call = client.newCall(request.url(url).headers(headerbuild).build())
            try {
                val response = call.execute()
                val body = response.body()
                        ?: return XXDownResultData(false, "Http body is null", listOf())
                val bodyStr = body.string()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message()})", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                Logger.d(bodyStr)
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

        fun bilibili(url: String): XXDownResultData? {
            val client = OkHttpClient()
            val call = client.newCall(request.url(url).headers(headerbuild).build())
            try {
                val response = call.execute()
                val body = response.body()
                        ?: return XXDownResultData(false, "Http body is null", listOf())
                val bodyStr = body.string()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message()})", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                Logger.d(bodyStr)
                val doc = Jsoup.parse(bodyStr)
                val jsoupUtil = JsoupUtil(doc)
                Logger.d(jsoupUtil.html("head > meta[itemprop='image']"))
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

        fun adnmb(postId: String, page: Int): XXDownResultData? {
            val appid = "nimingban"
            val host = "https://adnmb1.com/"
            val url = "$host?appid=&id=$postId&page=$page"
            HttpUtil.httpGetJsoup(url)
            val client = OkHttpClient()
            val call = client.newCall(request.url(url).headers(headerbuild).build())
            try {
                val response = call.execute()
                val body = response.body()
                        ?: return XXDownResultData(false, "Http body is null", listOf())
                val bodyStr = body.string()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message()})", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                Logger.d(bodyStr)
                val doc = Jsoup.parse(bodyStr)
                val jsoupUtil = JsoupUtil(doc)
                Logger.d(jsoupUtil.html("head > meta[itemprop='image']"))
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
    }
}