package com.zhihaofans.androidbox.mod

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.ChannelInfo
import com.zhihaofans.androidbox.data.News
import com.zhihaofans.androidbox.data.SiteInfo


/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null

    class sites(_context: Context) {
        private val context = _context

        fun getOldVerNewsList(siteId: String, channelId: String, page: Int): List<MutableMap<String, String>>? {
            val new = this.getNewsList(siteId, channelId, page) ?: return null
            return new.map {
                mutableMapOf(
                        "title" to it.title,
                        "web_url" to it.url
                )
            }.toList()
        }

        private fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<News>? {
            Logger.d("getNewsList($siteId,$channelId,$page)")
            return when (siteId) {
                ItemIdMod.FEED_SSPAI -> SiteInfoSspai.getNewsList(channelId, page)
                ItemIdMod.FEED_DGTLE -> SiteInfoDgtle.getNewsList(channelId, page)
                ItemIdMod.FEED_GANK_IO -> SiteInfoGankio.getNewsList(channelId, page)
                ItemIdMod.FEED_RSSHUB -> SiteInfoRsshub.getNewsList(channelId)
                ItemIdMod.FEED_WANANDROID -> SiteInfoWanandroid.getNewsList(channelId, page)
                ItemIdMod.FEED_ZHIHU_DAILY -> SiteInfoZhihudaily.getNewsList(channelId)
                ItemIdMod.FEED_JUHE_WEIXIN_JINGXUAN -> SiteInfoWeixinjingxuan.getNewsList(channelId, page)
                ItemIdMod.FEED_TOPHUB_TODAY -> SiteInfoTophubToday.getNewsList(channelId)
                ItemIdMod.FEED_FISH_POND_HOT_LIST -> SiteInfoFishPondHotList.start(channelId)
                else -> null
            }
        }

        fun getOldVerSiteList(): List<MutableMap<String, String>> {
            return this.getSiteList().map {
                mutableMapOf(
                        "id" to it.id,
                        "name" to it.name
                )
            }.toList()
        }

        private fun getSiteList(): List<SiteInfo> {
            return mutableListOf(
                    SiteInfo(
                            ItemIdMod.FEED_SSPAI,
                            context.getString(R.string.text_site_sspai),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_SSPAI_ARTICLE, context.getString(R.string.text_site_sspai_article))
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_DGTLE,
                            context.getString(R.string.text_site_dgtle),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_DGTLE_NEWS, context.getString(R.string.text_site_dgtle_news))
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_GANK_IO,
                            context.getString(R.string.text_site_gank_io),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_GANK_IO_ALL, context.getString(R.string.text_all)),
                                    ChannelInfo(ItemIdMod.FEED_GANK_IO_ANDROID, context.getString(R.string.text_android)),
                                    ChannelInfo(ItemIdMod.FEED_GANK_IO_GIRL, context.getString(R.string.text_gankio_girl))
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_RSSHUB,
                            context.getString(R.string.text_site_rsshub),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_DOUBAN_MOVIE_PLAYING, context.getString(R.string.text_site_rsshub_douban_movie_playing), true),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_JUEJIN_TRENDING_ANDROID, context.getString(R.string.text_site_rsshub_juejin_trending_android), true),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_BANGUMI_CALENDAR_TODAY, context.getString(R.string.text_site_rsshub_bangumi_calendar_today), true),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_NEW_RSS, context.getString(R.string.text_site_rsshub_new_rss), true),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_GUOKR_SCIENTIFIC, context.getString(R.string.text_site_rsshub_guokr_scientific), true)
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_WANANDROID,
                            context.getString(R.string.text_site_wanandroid),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_WANANDROID_INDEX, context.getString(R.string.text_site_wanandroid))
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_ZHIHU_DAILY,
                            context.getString(R.string.text_site_zhihu_daily),
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_ZHIHU_DAILY, context.getString(R.string.text_site_zhihu_daily), true)
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_JUHE_WEIXIN_JINGXUAN,
                            ItemNameMod.NAME_JUHE_WEIXIN_JINGXUAN,
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_JUHE_WEIXIN_JINGXUAN, ItemNameMod.NAME_JUHE_WEIXIN_JINGXUAN)
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_TOPHUB_TODAY,
                            ItemNameMod.NAME_TOPHUB_TODAY,
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_WEIBO, ItemNameMod.NAME_TOPHUB_TODAY_WEIBO, true),
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_BAIDU, ItemNameMod.NAME_TOPHUB_TODAY_BAIDU, true),
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_HUPUBUXINGJIE, ItemNameMod.NAME_TOPHUB_TODAY_HUPUBUXINGJIE, true),
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_ZHIHU_HOT, ItemNameMod.NAME_TOPHUB_TODAY_ZHIHU_HOT, true),
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_V2EX_HOT, ItemNameMod.NAME_TOPHUB_TODAY_V2EX_HOT, true),
                                    ChannelInfo(ItemIdMod.FEED_TOPHUB_TODAY_QDAILY, ItemNameMod.NAME_TOPHUB_TODAY_QDAILY, true)
                            )
                    ),
                    SiteInfo(
                            ItemIdMod.FEED_FISH_POND_HOT_LIST,
                            ItemNameMod.NAME_FEED_FISH_POND_HOT_LIST,
                            SiteInfoFishPondHotList.getChannel()
                    )
            )
        }

        fun getSiteChannelList(siteId: String): List<Map<String, Any>>? {
            this.getSiteList().map { mSite ->
                if (mSite.id == siteId) {
                    return mSite.channels.map { mChannel ->
                        mapOf(
                                "channelId" to mChannel.id,
                                "channelName" to mChannel.name,
                                "isChannelOnlyOnePage" to mChannel.onlyOnePage
                        )
                    }.toList()
                }
            }
            /*
            when (siteId) {
                ItemIdMod.FEED_SSPAI -> SiteInfoSspai(context).getchannelList()
                ItemIdMod.FEED_DGTLE -> SiteInfoDgtle(context).getchannelList()
                ItemIdMod.FEED_GANK_IO -> SiteInfoGankio(context).getchannelList()
                ItemIdMod.FEED_RSSHUB -> SiteInfoRsshub(context).getchannelList()
                ItemIdMod.FEED_WANANDROID -> SiteInfoWanandroid(context).getchannelList()
                ItemIdMod.FEED_ZHIHU_DAILY -> SiteInfoZhihudaily(context).getchannelList()
                else -> null
            }*/
            return null
        }
    }
}