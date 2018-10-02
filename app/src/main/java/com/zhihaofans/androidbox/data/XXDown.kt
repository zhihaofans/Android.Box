package com.zhihaofans.androidbox.data

/**
 * Created by zhihaofans on 2018/10/2.
 */
data class InstagramData(
        val success: Boolean,
        val message: String,
        val url: String?,
        val type: Int?
)

data class XXDownResultData(
        val success: Boolean,
        val message: String,
        val url: List<XXDownResultUrlData>
)

data class XXDownResultUrlData(
        val url: String,
        val type: Int
)