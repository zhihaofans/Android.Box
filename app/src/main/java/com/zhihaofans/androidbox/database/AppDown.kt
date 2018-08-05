package com.zhihaofans.androidbox.database

/**
 * Created by zhihaofans on 2018/7/25.
 */
data class AppDownFeed(//数据库表
        val No: Int,
        val name: String,
        val id_one: String,
        val id_two: String?,
        val site: String,
        val version: String,
        val updateTime: String,
        val packageName: String?,
        val fileList: MutableList<FileList>
)

data class AppFeed(
        val name: String,
        val id_one: String,
        val id_two: String?,
        val site: String,
        val version: String,
        val updateTime: String,
        val packageName: String?,
        val appUpdate: MutableList<AppUpdate>
)

data class AppUpdate(
        val name: String,
        val idOne: String,
        val idTwo: String?,
        val site: String,
        val description: String,
        val updateTime: String,
        val webUrl: String,
        val packageName: String?,
        val fileList: MutableList<FileList>
)

data class FileList(
        val id: String,
        val name: String,
        val url: String,
        val downCount: Int,
        val time: String,
        val size: Int,
        val sizeStr: String?
)