package com.zhihaofans.androidbox.gson

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-12-05 02:50

 */
data class FavoritesGson(
        val items: MutableList<FavoritesItemGson>
)
data class FavoritesItemGson(
        val id: String,
        val title: String,
        val type: String,
        val content: String
)