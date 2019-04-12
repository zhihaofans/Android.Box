package com.zhihaofans.androidbox.mod

import io.zhihao.library.android.util.FileUtil

/**
 * Created by zhihaofans on 2018/11/23.
 */
class UrlMod {
    companion object {
        // Update
        const val UPDATE_FIR_IM = "https://fir.im/fkw1"
        // News
        //  App
        const val APP_GITHUB_RELEASE = "https://api.github.com/repos/@author@/@project@/releases"
        //  Feed
        const val GANK_IO = "https://gank.io/api/data/"
        const val GANK_IO_ALL = GANK_IO + "all/20/"
        const val GANK_IO_ANDROID = GANK_IO + "Android/20/"
        const val GANK_IO_GIRL = GANK_IO + "福利/20/"
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
        // Local
        val APP_DOWNLOAD_PATH = FileUtil.getDownloadPathString() + "Android.Box/"
        val APP_PICTURE_DOWNLOAD_PATH = FileUtil.getPicturePathString() + "Android.Box/"
        // XXDown
        const val XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL = "http://www.acfun.cn/v/"
        const val XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL = "https://www.bilibili.com/video/"
        const val XXDOWN_SITE_GITHUB_RELEASE = "https://github.com/"
    }
}