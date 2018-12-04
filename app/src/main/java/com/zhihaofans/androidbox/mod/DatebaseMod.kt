package com.zhihaofans.androidbox.mod

import io.paperdb.Paper

/**
 * 数据库快捷调用mod

 * @author: zhiuhaofans

 * @date: 2018-12-03 00:52

 */
class DatebaseMod(dbName: String) {
    private val mDbName = dbName
    fun readString(key: String): MutableList<String> = Paper.book(mDbName).read(key)

    fun readInt(key: String): MutableList<Int> = Paper.book(mDbName).read(key)
    fun read(key: String): MutableList<Any> = Paper.book(mDbName).read(key)
    fun write(key: String, data: List<Any>) = Paper.book(mDbName).write(key, data)
}