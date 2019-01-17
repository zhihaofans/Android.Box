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
                ItemIdMod.FEED_SSPAI -> SiteInfoSspai().getNewsList(channelId, page)
                ItemIdMod.FEED_DGTLE -> SiteInfoDgtle().getNewsList(channelId, page)
                ItemIdMod.FEED_GANK_IO -> SiteInfoGankio().getNewsList(channelId, page)
                ItemIdMod.FEED_RSSHUB -> SiteInfoRsshub().getNewsList(channelId)
                ItemIdMod.FEED_WANANDROID -> SiteInfoWanandroid().getNewsList(channelId, page)
                ItemIdMod.FEED_ZHIHU_DAILY -> SiteInfoZhihudaily().getNewsList(channelId)
                ItemIdMod.FEED_JUHE_WEIXIN_JINGXUAN -> SiteInfoWeixinjingxuan().getNewsList(channelId, page)
                ItemIdMod.FEED_JUHE_TOUTIAO_NEWS -> SiteInfoToutiaoxinwen().getNewsList(channelId)
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
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_V2EX_TOPICS, context.getString(R.string.text_site_rsshub_v2ex_topics)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_DOUBAN_MOVIE_PLAYING, context.getString(R.string.text_site_rsshub_douban_movie_playing)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_JIKE_EDITOR_CHOICE, context.getString(R.string.text_site_rsshub_jike_editors_choice)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_JUEJIN_TRENDING_ANDROID, context.getString(R.string.text_site_rsshub_juejin_trending_android)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_BANGUMI_CALENDAR_TODAY, context.getString(R.string.text_site_rsshub_bangumi_calendar_today)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_NEW_RSS, context.getString(R.string.text_site_rsshub_new_rss)),
                                    ChannelInfo(ItemIdMod.FEED_RSSHUB_GUOKR_SCIENTIFIC, context.getString(R.string.text_site_rsshub_guokr_scientific))
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
                                    ChannelInfo(ItemIdMod.FEED_ZHIHU_DAILY, context.getString(R.string.text_site_zhihu_daily))
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
                            ItemIdMod.FEED_JUHE_TOUTIAO_NEWS,
                            ItemNameMod.NAME_JUHE_TOUTIAO_NEWS,
                            listOf(
                                    ChannelInfo(ItemIdMod.FEED_JUHE_TOUTIAO_NEWS, ItemNameMod.NAME_JUHE_TOUTIAO_NEWS)
                            )
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


    fun setContext(context: Context) {
        nowContext = context
    }


}