package com.zhihaofans.androidbox.data

/**
 * Created by zhihaofans on 2018/11/24.
 */
data class SiteInfo(
        val id: String,
        val name: String,
        val channels: List<ChannelInfo>
)

data class ChannelInfo(
        val id: String,
        val name: String,
        val onlyOnePage: Boolean = false
)

data class FeedParseRule(
        val id: String,
        val name: String,
        val onePage: Boolean,
        val type: String
)

data class FeedParseItemRule(
        val itemselector: String,
        val titleSelector: String,
        val urlSelector: String
)

data class News(
        val title: String,
        val url: String
)
