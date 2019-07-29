package com.zhihaofans.androidbox.util

import com.orhanobut.logger.Logger
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URL

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:50

 */
class HttpUtil {
    companion object {
        fun httpGetString(url: String, headers: Map<String, String>? = null): String? {
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(url)
            headers?.map {
                requestBuilder.addHeader(it.key, it.value)
            }
            val request = requestBuilder.build()
            val call = client.newCall(request)
            return try {
                val response = call.execute()
                if (response.body == null) {
                    null
                } else {
                    response.body!!.string()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }

        fun httpPostString(url: URL, body: Map<String, String> = mutableMapOf(), headers: Map<String, String>? = null): String? {
            return this.httpPostString(url.path, body, headers)
        }

        fun httpPostString(url: String, body: Map<String, String> = mutableMapOf(), headers: Map<String, String>? = null): String? {
            Logger.d("httpPostString\nurl:$url\nbody:$body\nheader:$headers")
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
            return try {
                val response = call.execute()
                val responseBody = response.body
                if (responseBody == null) {
                    null
                } else {
                    val str = responseBody.string()
                    response.close()
                    str
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }

        fun httpGetJsoup(url: String, headers: MutableMap<String, String>? = null, timeout: Int = 10000): Document {
            Logger.d("httpGetJsoup:$url,$headers,$timeout")
            return Jsoup.connect(url)
                    .headers(headers)
                    .timeout(timeout)
                    .get()
        }

        fun httpClientGetCall(url: String, headers: Headers): Call {
            val client = OkHttpClient()
            val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
            return client.newCall(request.url(url).headers(headers).build())
        }

        fun httpClientGetCall(url: URL, headers: Headers): Call {
            val client = OkHttpClient()
            val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
            return client.newCall(request.url(url).headers(headers).build())
        }
    }
}