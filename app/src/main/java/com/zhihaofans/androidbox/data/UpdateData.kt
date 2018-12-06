package com.zhihaofans.androidbox.data

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:47

 */
data class UpdateData(
        val source: List<String>,
        val version: UpdateVersionData,
        val web_url: List<String>
)

data class UpdateVersionData(
        val code: Int,
        val name: String,
        val time: String,
        val file_url: List<String>
)