package com.zhihaofans.androidbox.data

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-05-21 21:51

 */
data class TextInputListData(
        val title: String,
        val items: List<TextInputItemData>
)

data class TextInputItemData(
        val title: String,
        val type: String
)