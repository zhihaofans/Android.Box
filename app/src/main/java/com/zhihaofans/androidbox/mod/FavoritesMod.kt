package com.zhihaofans.androidbox.mod

import com.google.gson.Gson
import com.zhihaofans.androidbox.gson.FavoritesGson
import com.zhihaofans.androidbox.gson.FavoritesItemGson
import dev.utils.common.FileUtils
import io.paperdb.Paper

/**
 * @author: zhihaofans

 * @date: 2018-12-05 02:42

 */
class FavoritesMod {
    private val g = Gson()
    private val dbName = "com.zhihaofans.androidbox.favorites"
    private val favoritesListKey = ItemIdMod.DATEBASE_KEY_FAVORITES_LIST
    private val dbPath = Paper.book(dbName).getPath(favoritesListKey)
    fun load(): FavoritesGson {
        val json = Paper.book(dbName).read(favoritesListKey, g.toJson(FavoritesGson(mutableListOf()), FavoritesGson::class.java))
        return g.fromJson(json, FavoritesGson::class.java)
    }

    fun add(id: String, title: String, type: String, context: String): Boolean {
        val favoritesGson = this.load()
        val favoritesList = favoritesGson.items
        favoritesList.add(FavoritesItemGson(id, title, context, type))
        return this.write(FavoritesGson(favoritesList))
    }

    fun delete(index: Int): Boolean {
        val favoritesGson = this.load()
        val favoritesList = favoritesGson.items
        favoritesList.removeAt(index)
        return this.write(FavoritesGson(favoritesList))
    }

    fun deleteAll(): Boolean {
        Paper.book(dbName).delete(favoritesListKey)
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

    private fun write(favoritesList: FavoritesGson): Boolean {
        val json = g.toJson(favoritesList, FavoritesGson::class.java)
        return Paper.book(dbName).write(favoritesListKey, json).read(favoritesListKey, "") == json
    }

}
