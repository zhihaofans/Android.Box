package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/3/7.
 */
data class DgtleIndexGson(
        val list: MutableList<DgtleIndexListGson>,
        val dateline: String,
        val count: Int,
        val total: Int,
        val cdn_img_ext: String
)

data class DgtleIndexListGson(
        val tid: String,
        val subject: String,
        val message: String,
        val date: String?
)