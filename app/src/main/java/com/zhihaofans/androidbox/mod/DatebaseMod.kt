package com.zhihaofans.androidbox.mod

import io.paperdb.Book
import io.paperdb.Paper

/**
 * 数据库快捷调用mod

 * @author: zhihaofans

 * @date: 2018-12-03 00:52

 */
class DatebaseMod(dbName: String) {
    private val mDbName = dbName
    fun readString(key: String): MutableList<String>? = Paper.book(mDbName).read(key, null)
    fun readBoolean(key: String): MutableList<Boolean>? = Paper.book(mDbName).read(key, null)
    fun readInt(key: String): MutableList<Int>? = Paper.book(mDbName).read(key, null)
    fun read(key: String): MutableList<Any>? = Paper.book(mDbName).read(key, null)
    fun write(key: String, data: List<Any>): Book = Paper.book(mDbName).write(key, data)
    fun writeInt(key: String, data: List<Int>): Book = Paper.book(mDbName).write(key, data)
    fun writeString(key: String, data: List<String>): Book = Paper.book(mDbName).write(key, data)
    fun writeBoolean(key: String, data: List<Boolean>): Book = Paper.book(mDbName).write(key, data)

}