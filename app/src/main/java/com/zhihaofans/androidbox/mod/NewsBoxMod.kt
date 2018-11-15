package com.zhihaofans.androidbox.mod

import android.content.Context
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R


/**
 * Created by zhihaofans on 2018/3/9.
 */
class NewsBoxMod {
    private var nowContext: Context? = null

    data class News(
            val title: String,
            val url: String
    )

    data class ChannelInfo(
            val id: String,
            val name: String
    )



    class sites(_context: Context) {
        private val context = _context
        fun getNewsList(siteId: String, channelId: String, page: Int): MutableList<MutableMap<String, String>>? {
            Logger.d("getNewsList($siteId,$channelId,$page)")
            return when (siteId) {
                "sspai" -> {
                    siteInfo_sspai(context).getNewsList(channelId, page)
                }
                "dgtle" -> {
                    siteInfo_dgtle(context).getNewsList(channelId, page)
                }
                "gank.io" -> {
                    siteInfo_gankio(context).getNewsList(channelId, page)
                }
                "rsshub" -> {
                    siteInfo_rsshub(context).getNewsList(channelId, page)
                }
                "wanandroid" -> {
                    siteInfo_wanandroid(context).getNewsList(channelId, page)
                }
                "diycode" -> {
                    siteInfo_diycode(context).getNewsList(channelId, page)
                }
                "zhihu_daily" -> {
                    siteInfoZhihuDaily(context).getNewsList(channelId, page)
                }
                else -> null
            }
        }

        fun getSiteList(): MutableList<MutableMap<String, String>> {
            return mutableListOf(
                    mutableMapOf(
                            "id" to "sspai",
                            "name" to context.getString(R.string.text_site_sspai)
                    ),
                    mutableMapOf(
                            "id" to "dgtle",
                            "name" to context.getString(R.string.text_site_dgtle)
                    ),
                    mutableMapOf(
                            "id" to "gank.io",
                            "name" to context.getString(R.string.text_site_gank_io)
                    ),
                    mutableMapOf(
                            "id" to "rsshub",
                            "name" to context.getString(R.string.text_site_rsshub)
                    ),
                    mutableMapOf(
                            "id" to "wanandroid",
                            "name" to context.getString(R.string.text_site_wanandroid)
                    ),
                    mutableMapOf(
                            "id" to "diycode",
                            "name" to "diycode"
                    ),
                    mutableMapOf(
                            "id" to "zhihu_daily",
                            "name" to context.getString(R.string.text_site_zhihu_daily)
                    )
            )
        }

        fun getSiteChannelList(siteId: String): MutableList<MutableMap<String, String>>? {
            return when (siteId) {
                "sspai" -> siteInfo_sspai(context).getchannelList()
                "dgtle" -> siteInfo_dgtle(context).getchannelList()
                "gank.io" -> siteInfo_gankio(context).getchannelList()
                "rsshub" -> siteInfo_rsshub(context).getchannelList()
                "wanandroid" -> siteInfo_wanandroid(context).getchannelList()
                "diycode" -> siteInfo_diycode(context).getchannelList()
                "zhihu_daily" -> siteInfoZhihuDaily(context).getchannelList()
                else -> null
            }
        }
    }


    fun setContext(context: Context) {
        nowContext = context
    }


}