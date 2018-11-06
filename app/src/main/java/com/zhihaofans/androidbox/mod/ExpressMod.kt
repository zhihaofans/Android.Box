package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.zhihaofans.androidbox.data.*
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.common.encrypt.MD5Utils


/**
 * Created by zhihaofans on 2018/9/19.
 */
class ExpressMod {
    private val siteList = listOf(
            listOf("jd", "京东")
    )
    private var apiKey = ""
    private var apiCustomer = ""
    private var mContext: Context? = null
    private val g = Gson()
    fun init(context: Context, apiInfo: ApiInfo) {
        mContext = context
        apiKey = apiInfo.apiKey
        apiCustomer = apiInfo.apiCustomer
    }

    fun getSites(): List<ExpressSiteInfo> {
        return siteList.map {
            ExpressSiteInfo(it[0], it[1])
        }
    }

    fun getExpress(siteId: String, expressId: String): ExpressResult {
        val param = "{\"com\":\"$siteId\",\"num\":\"$expressId\"}"
        val customer = apiCustomer
        val key = apiKey
        val sign = MD5Utils.md5(param + key + customer)
        val params = hashMapOf<String, String>("param" to param, "sign" to sign, "customer" to customer)
        val apiUrl = "https://poll.kuaidi100.com/poll/query.do"
        val expressResult = ExpressResult(false, "", null)
        val serverResult = SystemUtil.httpPost4String(apiUrl, params)
        return if (serverResult.isNullOrEmpty()) {
            expressResult
        } else {
            try {
                val expressErrorResult = g.fromJson(serverResult, ExpressErrorResult::class.java)
                if (expressErrorResult.result == null) {
                    val kuaidi100Result = g.fromJson(serverResult, Kuaidi100Result::class.java)
                    kuaidi100Result.state = this@ExpressMod.kuaidi100StatusStr(kuaidi100Result.state.toIntOrNull())
                }
                expressResult
            } catch (e: Exception) {
                e.printStackTrace()
                expressResult
            }
        }
    }

    private fun kuaidi100StatusStr(index: Int?): String {
        val statusStrList = listOf("在途中", "已揽收", "疑难", "已签收", "退签", "同城派送中", "退回", "转单")
        return if (index == null) "错误状态序号(null)" else if (index >= statusStrList.size) "错误状态序号(超出数组上限)" else statusStrList[index]
    }
}
