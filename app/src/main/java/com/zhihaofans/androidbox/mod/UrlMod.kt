package com.zhihaofans.androidbox.mod

/**
 * Created by zhihaofans on 2018/11/23.
 */
class UrlMod {
    companion object {
        const val RSS2JSON = "https://api.rss2json.com/v1/api.json?rss_url="
        const val FEED2JSON = "https://feed2json.org/convert?url="
        // News
        //  Feed
        const val GANK_IO = "https://gank.io/api/data/"
        const val GANK_IO_ALL = GANK_IO + "all/20/"
        const val GANK_IO_ANDROID = GANK_IO + "Android/20/"
        const val GANK_IO_GIRL = GANK_IO + "福利/20/"
        const val DGTLE_NEWS = "https://api.yii.dgtle.com/v2/news?perpage=24&page="
        const val SSPAI_ARTICLE = "https://sspai.com/api/v1/articles?limit=20&type=recommend_to_home&sort=recommend_to_home_at&include_total=false&offset="
        const val RSSHUB = RSS2JSON + "https://rsshub.app/"
        const val RSSHUB_V2EX = RSSHUB + "v2ex/topics/latest"
        const val RSSHUB_DOUBANMOVIEPLAYING = RSSHUB + "douban/movie/playing"
        const val RSSHUB_JIKE = RSSHUB + "jike/topic/54dffb40e4b0f57466e675f0"
        const val RSSHUB_JUEJINTRENDINGANDROID = RSSHUB + "juejin/trending/android/monthly"
        const val RSSHUB_BANGUMITODAY = RSSHUB + "bangumi/calendar/today"
        const val RSSHUB_NEW_RSS = RSSHUB + "RSSHUB/rss"
        const val RSSHUB_GUOKR_SCIENTIFIC = RSSHUB + "guokr/scientific"
        const val WANANDROID_INDEX = "http://www.wanandroid.com/article/list/"
        const val DIYCODE_NEWS = "https://diycode.cc/api/v3/news?node_id=1&limit=20&offset="
        const val ZHIHU_DAILY = "https://news-at.zhihu.com/api/4/news/latest"
        const val ZHIHU_DAILY_WEB = "https://daily.zhihu.com/story/"
    }
}