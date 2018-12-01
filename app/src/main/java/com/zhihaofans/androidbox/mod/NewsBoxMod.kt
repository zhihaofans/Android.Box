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

        fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<News>? {
            Logger.d("getNewsList($siteId,$channelId,$page)")
            return when (siteId) {
                ItemNameMod.FEED_SSPAI -> siteInfo_sspai(context).getNewsList(channelId, page)
                ItemNameMod.FEED_DGTLE -> siteInfo_dgtle(context).getNewsList(channelId, page)
                ItemNameMod.FEED_GANK_IO -> siteInfo_gankio(context).getNewsList(channelId, page)
                ItemNameMod.FEED_RSSHUB -> siteInfo_rsshub(context).getNewsList(channelId)
                ItemNameMod.FEED_WANANDROID -> siteInfo_wanandroid(context).getNewsList(channelId, page)
                ItemNameMod.FEED_ZHIHU_DAILY -> siteInfoZhihuDaily(context).getNewsList(channelId, page)
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

        fun getSiteList(): List<SiteInfo> {
            return mutableListOf(
                    SiteInfo(
                            ItemNameMod.FEED_SSPAI,
                            context.getString(R.string.text_site_sspai),
                            listOf(ChannelInfo(ItemNameMod.FEED_SSPAI_ARTICLE, context.getString(R.string.text_site_sspai_article)))
                    ),
                    SiteInfo(
                            ItemNameMod.FEED_DGTLE,
                            context.getString(R.string.text_site_dgtle),
                            listOf(ChannelInfo(ItemNameMod.FEED_DGTLE_NEWS, context.getString(R.string.text_site_dgtle_news)))
                    ),
                    SiteInfo(
                            ItemNameMod.FEED_GANK_IO,
                            context.getString(R.string.text_site_gank_io),
                            listOf(
                                    ChannelInfo(ItemNameMod.FEED_GANK_IO_ALL, context.getString(R.string.text_all)),
                                    ChannelInfo(ItemNameMod.FEED_GANK_IO_ANDROID, context.getString(R.string.text_android)),
                                    ChannelInfo(ItemNameMod.FEED_GANK_IO_GIRL, context.getString(R.string.text_gankio_girl))
                            )
                    ),
                    SiteInfo(
                            ItemNameMod.FEED_RSSHUB,
                            context.getString(R.string.text_site_rsshub),
                            listOf(
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_V2EX_TOPICS, context.getString(R.string.text_site_rsshub_v2ex_topics)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_DOUBAN_MOVIE_PLAYING, context.getString(R.string.text_site_rsshub_douban_movie_playing)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_JIKE_EDITOR_CHOICE, context.getString(R.string.text_site_rsshub_jike_editors_choice)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_JUEJIN_TRENDING_ANDROID, context.getString(R.string.text_site_rsshub_juejin_trending_android)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_BANGUMI_CALENDAR_TODAY, context.getString(R.string.text_site_rsshub_bangumi_calendar_today)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_NEW_RSS, context.getString(R.string.text_site_rsshub_new_rss)),
                                    ChannelInfo(ItemNameMod.FEED_RSSHUB_GUOKR_SCIENTIFIC, context.getString(R.string.text_site_rsshub_guokr_scientific))
                            )
                    ),
                    SiteInfo(
                            ItemNameMod.FEED_WANANDROID,
                            context.getString(R.string.text_site_wanandroid),
                            listOf(ChannelInfo(ItemNameMod.FEED_WANANDROID_INDEX, context.getString(R.string.text_site_wanandroid)))
                    ),
                    SiteInfo(
                            ItemNameMod.FEED_ZHIHU_DAILY,
                            context.getString(R.string.text_site_zhihu_daily),
                            listOf(ChannelInfo(ItemNameMod.FEED_ZHIHU_DAILY, context.getString(R.string.text_site_zhihu_daily)))
                    )
            )
        }

        fun getSiteChannelList(siteId: String): List<MutableMap<String, String>>? {
            this.getSiteList().map { mSite ->
                if (mSite.id == siteId) {
                    return mSite.channels.map { mChannel ->
                        mutableMapOf(
                                "channelId" to mChannel.id,
                                "channelName" to mChannel.name
                        )
                    }.toList()
                }
            }
            /*
            when (siteId) {
                ItemNameMod.FEED_SSPAI -> siteInfo_sspai(context).getchannelList()
                ItemNameMod.FEED_DGTLE -> siteInfo_dgtle(context).getchannelList()
                ItemNameMod.FEED_GANK_IO -> siteInfo_gankio(context).getchannelList()
                ItemNameMod.FEED_RSSHUB -> siteInfo_rsshub(context).getchannelList()
                ItemNameMod.FEED_WANANDROID -> siteInfo_wanandroid(context).getchannelList()
                ItemNameMod.FEED_ZHIHU_DAILY -> siteInfoZhihuDaily(context).getchannelList()
                else -> null
            }*/
            return null

        }
    }


    fun setContext(context: Context) {
        nowContext = context
    }


}