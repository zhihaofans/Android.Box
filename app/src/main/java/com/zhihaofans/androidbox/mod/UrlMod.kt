package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.kotlinEx.toURLEncode
import com.zhihaofans.androidbox.util.SystemUtil

/**
 * Created by zhihaofans on 2018/11/23.
 */
class UrlMod {
    companion object {
        // Update
        const val UPDATE_FIR_IM = "https://fir.im/fkw1"
        const val RSS2JSON = "https://api.rss2json.com/v1/api.json?rss_url="
        const val FEED2JSON = "https://feed2json.org/convert?url="
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
        var RSSHUB_V2EX = RSS2JSON + (RSSHUB + "v2ex/topics/latest").toURLEncode()
        var RSSHUB_DOUBANMOVIEPLAYING = RSS2JSON + (RSSHUB + "douban/movie/playing").toURLEncode()
        var RSSHUB_JIKE = RSS2JSON + (RSSHUB + "jike/topic/54dffb40e4b0f57466e675f0").toURLEncode()
        var RSSHUB_JUEJINTRENDINGANDROID = RSS2JSON + (RSSHUB + "juejin/trending/android/monthly").toURLEncode()
        var RSSHUB_BANGUMITODAY = RSS2JSON + (RSSHUB + "bangumi/calendar/today").toURLEncode()
        var RSSHUB_NEW_RSS = RSS2JSON + (RSSHUB + "RSSHUB/rss").toURLEncode()
        var RSSHUB_GUOKR_SCIENTIFIC = RSS2JSON + (RSSHUB + "guokr/scientific").toURLEncode()
        const val WANANDROID_INDEX = "http://www.wanandroid.com/article/list/"
        const val ZHIHU_DAILY = "https://news-at.zhihu.com/api/4/news/latest"
        const val ZHIHU_DAILY_WEB = "https://daily.zhihu.com/story/"
        const val WEIXIN_JINGXUAN = "http://v.juhe.cn/weixin/query?dtype=json&ps=20&key=" + ApiKeyMod.APIKEY_JUHE_WEIXIN + "&pno="
        const val NEWS_TOUTIAO = "http://v.juhe.cn/toutiao/index?key=" + ApiKeyMod.APIKEY_NEWS_TOUTIAO + "&type="
        // Local
        val APP_DOWNLOAD_PATH = SystemUtil.getDownloadPathString() + "Android.Box/"
        val APP_PICTURE_DOWNLOAD_PATH = SystemUtil.getPicturePathString() + "Android.Box/"
        // XXDown
        const val XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL = "http://www.acfun.cn/v/"
        const val XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL = "https://www.bilibili.com/video/"
        const val XXDOWN_SITE_GITHUB_RELEASE = "https://github.com/"
    }
}