package com.zhihaofans.androidbox.data

/**
 * Created by zhihaofans on 2018/9/20.
 */
data class ExpressResult(
        var success: Boolean,
        var message: String,
        var serverResult: Kuaidi100Result?
)

data class ExpressSiteInfo(
        val siteId: String,
        val siteName: String
)

data class Kuaidi100Result(
        val message: String?,
        var state: String,
        val status: String?,
        val condition: String?,
        val ischeck: String?,
        val com: String,
        val nu: String,
        val data: List<ExpressResultData>
)

data class ExpressErrorResult(
        val result: Boolean?,
        val returnCode: String?,
        val message: String
)

data class ExpressResultData(
        val context: String,
        val time: String,
        val ftime: String
)

data class ApiInfo(
        val apiKey: String,
        val apiCustomer: String
)

data class ExpressDatabase(
        val siteId: String,
        val expressId: String
)