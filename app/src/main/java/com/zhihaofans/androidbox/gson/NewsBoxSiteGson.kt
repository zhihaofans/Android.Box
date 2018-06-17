package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/5/7.
 */
// 数字尾巴(Dgtle)
data class DgtleIndexGson(
        val list: MutableList<DgtleIndexListGson>,
        val dateline: String,
        val count: Int,
        val total: Int,
        val cdn_img_ext: String
)

data class DgtleIndexListGson(
        val tid: String,
        val subject: String,
        val message: String,
        val date: String?
)

// Gank.io
data class GankIoAllGson(
        val error: Boolean,
        val results: MutableList<GankIoAllListGson>
)

data class GankIoAllListGson(
        val _id: String,
        val createdAt: String,
        val desc: String,
        val publishedAt: String,
        val source: String,
        val type: String,
        val url: String,
        val used: Boolean,
        val who: String
)

// 少数派(sspai)
data class SspaiArticleGson(
        val list: MutableList<SspaiArticleListGson>
)

data class SspaiArticleListGson(
        val id: Int,
        val title: String,
        val summary: String,
        val promote_intro: String
)

// 品玩(pingwest)
data class PingwestForwardRecommendGson(
        val id: String,
        val title: String,
        val content: String,
        val link: String,
        val img: String,
        val source: String,
        val zuozhe: String,
        val orglink: String
)

// 观点(guandn)
data class GuandnIndexGson(
        val source: MutableList<GuandnIndexListGson>
)

data class GuandnIndexListGson(
        val title: String,
        val identification: String

)

// RSSHub
data class RsshubGson(
        val items: MutableList<RsshubItemGson>
)

data class RsshubItemGson(
        val url: String,
        val title: String,
        val summary: String
)