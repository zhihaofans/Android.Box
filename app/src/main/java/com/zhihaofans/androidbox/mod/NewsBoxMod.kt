package com.zhihaofans.androidbox.mod

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import okhttp3.CacheControl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null

    class newsBoxCommon {
        fun httpGet4String(url: String, headers: MutableMap<String, String>? = null): String {
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(url)
            if (headers != null) {
                headers.map {
                    requestBuilder.addHeader(it.key, it.value)
                }
            }
            val request = requestBuilder.build()
            val call = client.newCall(request)
            try {
                val response = call.execute()
                if (response.body() == null) {
                    return ""
                } else {
                    return response.body()!!.string()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }

        }

        fun httpPost4String(url: String, body: MutableMap<String, String> = mutableMapOf(), headers: MutableMap<String, String>? = null): String {
            Logger.d("httpPost4String\nurl:$url\nbody:$body\nheader:$headers")
            //val params = FormBody.Builder()
            var str = ""
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
            body.map {
                requestBody.addFormDataPart(it.key, it.value)
            }
            val client = OkHttpClient.Builder().retryOnConnectionFailure(true).build()

            val requestBuilder = Request.Builder().url(url).post(requestBody.build())
            headers?.map {
                requestBuilder.addHeader(it.key, it.value)
            }
            val call = client.newCall(requestBuilder.build())
            Logger.d("httpPost4String")
            return try {
                val response = call.execute()
                val responseBody = response.body()
                Logger.d("response.code():${response.code()}")
                Logger.d("httpPost4String")
                if (responseBody == null) {
                    Logger.e("response.body() = null")
                    ""

                } else {
                    str = responseBody.string()
                    response.close()
                    Logger.d(str)
                    str
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

        }

        fun httpGet4Jsoup(url: String, headers: MutableMap<String, String>? = null, timeout: Int = 10000): Document {
            Logger.d("httpGet4Jsoup:$url,$headers,$timeout")
            val doc = Jsoup.connect(url)
                    .headers(headers)
                    .timeout(timeout)
                    .get()
            return doc
        }
    }

    class sites(_context: Context) {
        private val context = _context
        fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            Logger.d("getNewsList($siteId,$channelId,$page)")
            return when (siteId) {
                "sspai" -> {
                    siteInfo_sspai(context).getNewsList(channelId, page)
                }
                "dgtle" -> {
                    siteInfo_dgtle(context).getNewsList(channelId, page)
                }
                "gank.io" -> {
                    siteInfo_gankio(context).getNewsList(channelId, page)
                }
                "pingwest" -> {
                    siteInfo_pingwest(context).getNewsList(channelId, page)
                }
                "all.gl" -> {
                    siteInfo_allgl(context).getNewsList(channelId, page)
                }
                "guandn" -> {
                    siteInfo_guandn(context).getNewsList(channelId, page)
                }
                "rsshub" -> {
                    siteInfo_rsshub(context).getNewsList(channelId, page)
                }
                else -> null
            }
        }

        fun getSiteList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "id" to "sspai",
                            "name" to context.getString(R.string.text_site_sspai)
                    ),
                    mutableMapOf(
                            "id" to "dgtle",
                            "name" to context.getString(R.string.text_site_dgtle)
                    ),
                    mutableMapOf(
                            "id" to "gank.io",
                            "name" to context.getString(R.string.text_site_gank_io)
                    ),
                    mutableMapOf(
                            "id" to "pingwest",
                            "name" to context.getString(R.string.text_site_pingwest)
                    ),
                    mutableMapOf(
                            "id" to "all.gl",
                            "name" to context.getString(R.string.text_site_allgl)
                    ),
                    mutableMapOf(
                            "id" to "guandn",
                            "name" to context.getString(R.string.text_site_guandn)
                    ),
                    mutableMapOf(
                            "id" to "rsshub",
                            "name" to context.getString(R.string.text_site_rsshub)
                    )
            )
        }

        fun getSiteChannelList(siteId: String): MutableList<MutableMap<String, String>>? {
            return when (siteId) {
                "sspai" -> siteInfo_sspai(context).getchannelList()
                "dgtle" -> siteInfo_dgtle(context).getchannelList()
                "gank.io" -> siteInfo_gankio(context).getchannelList()
                "pingwest" -> siteInfo_pingwest(context).getchannelList()
                "all.gl" -> siteInfo_allgl(context).getchannelList()
                "guandn" -> siteInfo_guandn(context).getchannelList()
                "rsshub" -> siteInfo_rsshub(context).getchannelList()
                else -> null
            }
        }
    }

    fun setContext(context: Context) {
        nowContext = context
    }


}