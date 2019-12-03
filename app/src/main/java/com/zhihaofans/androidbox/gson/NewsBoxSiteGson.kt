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

// RSSHub
data class Rss2jsonNewGson(
        val rss: Rss2jsonNewRssGson?
)

data class Rss2jsonNewRssGson(
        val channel: Rss2jsonNewChannelGson
)

data class Rss2jsonNewChannelGson(
        val item: List<RsshubItemGson>?
)


data class RsshubItemGson(
        val link: String,
        val title: String
)

// WanAndroid
data class WanandroidGson(
        val data: WanandroidDataGson,
        val errorCode: Int,
        val errorMsg: String
)

data class WanandroidDataGson(
        val curPage: Int,
        val datas: List<WanandroidDataItemGson>,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
)

data class WanandroidDataItemGson(
        val apkLink: String,
        val author: String,
        val chapterId: Int,
        val chapterName: String,
        val collect: Boolean,
        val courseId: Int,
        val desc: String,
        val envelopePic: String,
        val fresh: Boolean,
        val id: Int,
        val link: String,
        val niceDate: String,
        val origin: String,
        val projectLink: String,
        val publishTime: Long,
        val superChapterId: Int,
        val superChapterName: String,
        val tags: List<WanandroidDataItemTagGson>,
        val title: String,
        val type: Int,
        val userId: Int,
        val visible: Int,
        val zan: Int
)

data class WanandroidDataItemTagGson(
        val name: String,
        val url: String
)

// zhihu daily
data class ZhihuDailyGson(
        val date: String,
        val stories: List<ZhihuDailyStoriesGson>,
        val top_stories: List<ZhihuDailyStoriesGson>
)

data class ZhihuDailyStoriesGson(
        val images: List<String>,
        val type: Int,
        val id: Int,
        val ga_prefix: String,
        val title: String
)
// Juhe Weixin

data class JuheWeixinGson(
        val reason: String,
        val result: JuheWeixinResultGson?,
        val error_code: Int
)

data class JuheWeixinResultGson(
        val list: List<JuheWeixinResultListGson>,
        val totalPage: Int,
        val ps: Int,
        val pno: Int
)

data class JuheWeixinResultListGson(
        val id: String,
        val title: String,
        val source: String,
        val firstImg: String,
        val mark: String,
        val url: String
)


// Tophub.fun
data class TophubfunTypeGson(
        val Code: Int,
        val Message: String,
        val Data: List<TophubfunTypeDataGson>?
)

data class TophubfunTypeDataGson(
        val id: String,
        val sort: String,
        val title: String
)

data class TophubfunInfoGson(
        val Code: Int,
        val Message: String,
        val Data: List<TophubfunInfoDataGson>?
)

data class TophubfunInfoDataGson(
        val id: String,
        val CreateTime: String,
        val Desc: String,
        val Url: String,
        val approvalNum: String,
        val commentNum: String,
        val hotDesc: String,
        val imgUrl: String,
        val title: String
)