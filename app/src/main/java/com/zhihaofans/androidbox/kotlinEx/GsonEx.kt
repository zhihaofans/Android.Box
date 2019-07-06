package com.zhihaofans.androidbox.kotlinEx

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.zhihaofans.androidbox.util.HttpUtil

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:55

 */

@Throws(JsonSyntaxException::class)
fun <T> Gson.fromWebGetJson(url: String, classOfT: Class<T>): T? {
    return try {
        val json = HttpUtil.httpGetString(url)
        if (json.isNullOrEmpty()) null else this.fromJson(json, classOfT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Throws(JsonSyntaxException::class)
fun <T> Gson.fromWebPostJson(url: String, body: MutableMap<String, String>? = null, classOfT: Class<T>): T? {
    return try {
        val json = HttpUtil.httpPostString(url, body ?: mutableMapOf())
        if (json.isNullOrEmpty()) null else this.fromJson(json, classOfT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}