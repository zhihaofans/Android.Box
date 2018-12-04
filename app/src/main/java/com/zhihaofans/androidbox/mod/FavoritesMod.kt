package com.zhihaofans.androidbox.mod

import android.content.Context
import com.jyuesong.android.kotlin.extract.removeIt
import com.zhihaofans.androidbox.gson.FavoritesItemGson
import dev.utils.common.FileUtils
import io.paperdb.Paper

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-12-05 02:42

 */
class FavoritesMod {
    private val dbName = "com.zhihaofans.androidbox.favorites"
    private val favoritesListKey = ItemNameMod.DATEBASE_KEY_FAVORITES_LIST
    private val dbPath = Paper.book(dbName).getPath(favoritesListKey)
    fun load(): MutableList<FavoritesItemGson> = Paper.book(dbName).read(favoritesListKey, mutableListOf())
    fun add(id: String, title: String, type: String, context: String): Boolean {
        val favoritesList = this.load()
        favoritesList.add(FavoritesItemGson(id, title, type, context))
        return this.write(favoritesList)
    }

    fun delete(index: Int): Boolean {
        val favoritesList = this.load()
        favoritesList.removeAt(index)
        return this.write(favoritesList)
    }

    fun deleteAll(context: Context): Boolean {
        Paper.book(dbName).removeIt(context, favoritesListKey)
        return Paper.book(dbName).read(favoritesListKey, mutableListOf<FavoritesItemGson>()).size == 0
    }

    fun export(saveTo: String): Boolean {
        return try {
            FileUtils.copyFile(dbPath, saveTo + FileUtils.getFileName(dbPath), true)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun import(backupFilePath: String): Boolean {
        return try {
            FileUtils.copyFile(backupFilePath, dbPath, true)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteDataBase(): Boolean {
        return try {
            FileUtils.deleteFile(dbPath)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun write(favoritesList: MutableList<FavoritesItemGson>): Boolean {
        val book = Paper.book(dbName).write(favoritesListKey, favoritesList)
        return book.read(favoritesListKey, null) == favoritesList
    }

}
