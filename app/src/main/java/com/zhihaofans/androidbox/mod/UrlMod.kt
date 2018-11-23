package com.zhihaofans.androidbox.mod

/**
 * Created by zhihaofans on 2018/11/23.
 */
class UrlMod {
    companion object {
        const val RSS2JSON = "https://rss2json.com/#rss_url="
        const val FEED2JSON = "https://feed2json.org/convert?url="
        // News
        //  Feed
        const val RSSHUB = RSS2JSON + "https://rsshub.app/"
        const val RSSHUB_V2EX = RSSHUB + "v2ex/topics/latest.json"
        const val RSSHUB_DOUBANMOVIEPLAYING = RSSHUB + "douban/movie/playing.json"
        const val RSSHUB_JIKE = RSSHUB + "jike/topic/54dffb40e4b0f57466e675f0.json"
        const val RSSHUB_JUEJINTRENDINGANDROID = RSSHUB + "juejin/trending/android/monthly.json"
        const val RSSHUB_BANGUMITODAY = RSSHUB + "bangumi/calendar/today.json"
        const val RSSHUB_NEW_RSS = RSSHUB + "RSSHUB/rss.json"
        const val RSSHUB_GUOKR_SCIENTIFIC = RSSHUB + "guokr/scientific.json"
    }
}