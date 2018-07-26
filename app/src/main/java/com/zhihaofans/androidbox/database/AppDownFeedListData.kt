package com.zhihaofans.androidbox.database

/**
 * Created by zhihaofans on 2018/7/25.
 */
data class AppDownFeed(
        val No: Int,
        val name: String,
        val id_one: String,
        val id_two: String?,
        val site: String,
        val version: String,
        val updateTime: String
)