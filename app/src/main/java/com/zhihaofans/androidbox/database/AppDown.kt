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

data class AppInfo(
        val name: String,
        val idOne: String,
        val idTwo: String?,
        val site: String,
        val description:String,
        val updateTime:String,
        val webUrl:String,
        val fileList:MutableList<FileList>
)
data class FileList(
        val id:String,
        val name: String,
        val url:String,
        val downCount:Int,
        val time:String,
        val size: Int
)