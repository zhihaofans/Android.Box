package com.zhihaofans.androidbox.mod

import io.zhihao.library.android.util.FileUtil

/**
 * Created by zhihaofans on 2018/11/23.
 */
class UrlMod {
    companion object {
        // Update
        const val UPDATE_FIR_IM = "https://fir.im/fkw1"
        const val UPDATE_PGYER = "https://www.pgyer.com/Tka7"
        // News
        //  App
        const val APP_GITHUB_RELEASE = "https://api.github.com/repos/@author@/@project@/releases"
        //  Feed
        const val DGTLE_NEWS = "https://api.yii.dgtle.com/v2/news?perpage=24&page="
        const val SSPAI_ARTICLE = "https://sspai.com/api/v1/articles?limit=20&type=recommend_to_home&sort=recommend_to_home_at&include_total=false&offset="
        const val RSSHUB = "https://rsshub.app/"
        const val RSSHUB_DOUBANMOVIEPLAYING = RSSHUB + "douban/movie/playing"
        const val RSSHUB_JUEJINTRENDINGANDROID = RSSHUB + "juejin/trending/android/monthly"
        const val RSSHUB_BANGUMITODAY = RSSHUB + "bangumi/calendar/today"
        const val RSSHUB_NEW_RSS = RSSHUB + "RSSHUB/rss"
        const val RSSHUB_GUOKR_SCIENTIFIC = RSSHUB + "guokr/scientific"
        const val WANANDROID_INDEX = "http://www.wanandroid.com/article/list/"
        const val ZHIHU_DAILY = "https://news-at.zhihu.com/api/4/news/latest"
        const val ZHIHU_DAILY_WEB = "https://daily.zhihu.com/story/"
        const val WEIXIN_JINGXUAN = "http://v.juhe.cn/weixin/query?dtype=json&ps=20&key=" + ApiKeyMod.APIKEY_JUHE_WEIXIN + "&pno="
        const val TOPHUB_TODAY_WEIBO = "https://tophub.today/n/KqndgxeLl9"
        const val TOPHUB_TODAY_JINRITOUTIAO = "https://tophub.today/n/20MdKa2ow1"
        const val TOPHUB_TODAY_HUPUBUXINGJIE = "https://tophub.today/n/Jb0vmloB1G"
        const val TOPHUB_TODAY_ZHIHU_HOT = "https://tophub.today/n/mproPpoq6O"
        const val TOPHUB_TODAY_V2EX_HOT = "https://tophub.today/n/wWmoORe4EO"
        const val TOPHUB_TODAY_QDAILY = "https://tophub.today/n/Y3QeLGAd7k"
        const val TOPHUB_MOD_HOMEPAGE = "https://tophub.today"
        const val TOPHUBFUN_TYPE = "https://www.tophub.fun:8080/GetType"
        const val TOPHUBFUN_GET_ALL_INFO = "https://www.tophub.fun:8080/GetAllInfoGzip?id="
        // Local
        val APP_DOWNLOAD_PATH = FileUtil.getDownloadPathString() + "Android.Box/"
        val APP_PICTURE_DOWNLOAD_PATH = FileUtil.getPicturePathString() + "Android.Box/"
        // XXDown
        const val XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL = "http://www.acfun.cn/v/"
        const val XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL_HTTP = "http://www.bilibili.com/video/"
        const val XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL = "https://www.bilibili.com/video/"
        const val XXDOWN_SITE_BILIBILI_ACG_VIDEO_THUMBNAIL = "https://acg.tv/"
        const val XXDOWN_SITE_GITHUB_RELEASE = "https://github.com/"
        const val XXDOWN_SITE_HOST_GITHUB_RELEASE = "github.com"
        const val XXDOWN_SITE_INSTAGRAM = "https://www.instagram.com/p/"
        const val XXDOWN_SITE_HOST_INSTAGRAM = "www.instagram.com"
        const val XXDOWN_SITE_10INSTA = "https://www.10insta.net/"
        const val XXDOWN_SITE_HOST_TWITTER = "twitter.com"
        const val XXDOWN_SITE_TUBEOFFLINE = "https://www.tubeoffline.com/downloadFrom.php?host=%s&video=%s"
        //const val XXDOWN_SITE_GITHUBUSERCONTENT_RAW = "https://raw.githubusercontent.com/zhihaofans/zhuang.zhihao.io/master/.gitignore"
        const val XXDOWN_SITE_GITHUBUSERCONTENT_RAW_HOST = "raw.githubusercontent.com"
        const val XXDOWN_SITE_GITHUBUSERCONTENT_RAW = "https://raw.githubusercontent.com/"
    }
}