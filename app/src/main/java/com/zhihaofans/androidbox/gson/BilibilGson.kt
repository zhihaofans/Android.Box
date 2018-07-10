package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/4/13.
 */
//这两个是弹幕用户hash转换用户uid
data class BilibiliDanmuGetHashGson(
        val error: Int,
        val data: MutableList<BilibiliDanmuGetHashItemGson>
)

data class BilibiliDanmuGetHashItemGson(
        val id: Int
)

//下面的是用户信息
data class BilibiliUserInfoResultGson(
        val code: Int,
        val message: String,
        val data: BilibiliUserInfoResultDataGson
)

data class BilibiliUserInfoResultDataGson(
        val card: BilibiliUserInfoResultCardGson
)

data class BilibiliUserInfoResultCardGson(
        val mid: String,//用户id
        val name: String,//用户昵称
        val sex: String,//用户性别
        val face: String,//用户头像地址
        val attentions: MutableList<Int>,//关注列表
        val fans: Int,//粉丝数量
        val attention: Int,//关注列表数量
        val level_info: BilibiliUserInfoResultCardLevelGson//等级
)

data class BilibiliUserInfoResultCardLevelGson(
        val current_level: Int//等级
)

data class BilibiliVideoGson(
        val error: Int,
        val cid: String?
)

// 视频封面解析(Galmoe)
data class BiliBiliGalmoeGson(
        val result: Int,
        val url: String
)
