package com.zhihaofans.androidbox.data

/**
 * Created by zhihaofans on 2018/7/25.
 */
data class AppDownFeed(//数据库表
        val name: String,
        val id_one: String,
        val id_two: String?,
        val site: String,
        val author: String,
        val version: String,
        val updateTime: String,
        val packageName: String?,
        val webUrl: String,
        val fileList: MutableList<FileList>
)

data class AppInfoResult(
        var success: Boolean,
        var message: String,
        var code: Int,
        var result: AppInfo?
)

data class AppInfo(
        val id_one: String,
        val id_two: String?,
        val appName: String,
        val site: String,
        val author: String,
        val version: String,
        val updateTime: String,
        val packageName: String?,
        val webUrl: String,
        val fileList: MutableList<FileList>
)


data class FileList(
        val name: String,
        val url: String,
        val downCount: String?,
        val time: String,
        val sizeStr: String?
)