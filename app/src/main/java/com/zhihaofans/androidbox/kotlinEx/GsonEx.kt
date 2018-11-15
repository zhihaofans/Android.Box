package com.zhihaofans.androidbox.kotlinEx

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.zhihaofans.androidbox.util.HttpUtil

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-12 19:55

 */

@Throws(JsonSyntaxException::class)
fun <T> Gson.fromWebGetJson(url: String, classOfT: Class<T>): T? {
    val json = HttpUtil.httpGet4String(url)
    if (json.isNullOrEmpty()) return null
    return this.fromJson(json, classOfT)
}

@Throws(JsonSyntaxException::class)
fun <T> Gson.fromWebPostJson(url: String, body: MutableMap<String, String>? = null, classOfT: Class<T>): T? {
    val json = HttpUtil.httpPost4String(url, body ?: mutableMapOf())
    if (json.isNullOrEmpty()) return null
    return this.fromJson(json, classOfT)
}