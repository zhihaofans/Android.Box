package com.zhihaofans.androidbox.mod

import com.wx.logger.Logger
import com.zhihaofans.androidbox.data.XXDownResultData
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.data.XXDownSiteList
import com.zhihaofans.androidbox.data.XXDownUrlType
import com.zhihaofans.androidbox.util.JsoupUtil
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

/**
 * Created by zhihaofans on 2018/10/2.
 */
class XXDownMod {
    companion object {
        fun get(site: Int, url: String): XXDownResultData? {
            return when (site) {
                XXDownSiteList.acfun -> XXDownSite.acfun(url)
                XXDownSiteList.bilibili -> XXDownSite.bilibili(url)
                else -> null
            }

        }
    }
}

class XXDownSite {
    companion object {
        private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
        fun acfun(url: String): XXDownResultData? {
            val client = OkHttpClient()
            val call = client.newCall(request.url(url).build())
            try {
                val response = call.execute()
                val body = response.body()
                val bodyStr = body.toString()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message()})", listOf())
                if (body == null) return XXDownResultData(false, "Http body is null", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
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
            val call = client.newCall(request.url(url).build())
            try {
                val response = call.execute()
                val body = response.body()
                val bodyStr = body.toString()
                if (!response.isSuccessful) return XXDownResultData(false, "Http get error(${response.message()})", listOf())
                if (body == null) return XXDownResultData(false, "Http body is null", listOf())
                if (bodyStr.isEmpty()) return XXDownResultData(false, "Http body is empty", listOf())
                val doc = Jsoup.parse(bodyStr)
                val jsoupUtil = JsoupUtil(doc)
                Logger.d(jsoupUtil.html("head > meta[\"itemprop\"=\"image\"]"))
                val image = jsoupUtil.attrOrNull("head > meta[\"itemprop\"=\"image\"]", "content")
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
    }
}