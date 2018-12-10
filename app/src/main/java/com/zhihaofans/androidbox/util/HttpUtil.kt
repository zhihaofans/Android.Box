package com.zhihaofans.androidbox.util

import com.orhanobut.logger.Logger
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:50

 */
class HttpUtil {
    companion object {
        fun httpGetString(url: String, headers: MutableMap<String, String>? = null): String? {
            val client = OkHttpClient()
            val requestBuilder = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build()).url(url)
            headers?.map {
                requestBuilder.addHeader(it.key, it.value)
            }
            val request = requestBuilder.build()
            val call = client.newCall(request)
            return try {
                val response = call.execute()
                if (response.body() == null) {
                    null
                } else {
                    response.body()!!.string()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }

        fun httpPostString(url: String, body: MutableMap<String, String> = mutableMapOf(), headers: MutableMap<String, String>? = null): String? {
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
            Logger.d("httpPostString")
            return try {
                val response = call.execute()
                val responseBody = response.body()
                Logger.d("response.code():${response.code()}")
                Logger.d("httpPostString")
                if (responseBody == null) {
                    Logger.e("response.body() = null")
                    null
                } else {
                    val str = responseBody.string()
                    response.close()
                    Logger.d(str)
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
    }
}