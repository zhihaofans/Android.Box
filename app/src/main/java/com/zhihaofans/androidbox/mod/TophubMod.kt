package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.*
import com.zhihaofans.androidbox.util.HttpUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-04-15 15:45

 */
class TophubMod {
    companion object {
        private val userAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36"
        private val httpHeader = mapOf("User-Agent" to userAgent)
        fun getHomePage(): TophubHomepage? {
            val url = UrlMod.TOPHUB_MOD_HOMEPAGE
            try {
                val result = HttpUtil.httpGetString(url, httpHeader)
                return if (result.isNullOrEmpty()) {
                    null
                } else {
                    val doc = Jsoup.parse(result, url)
                    val elements = doc.select("div.weui-tab#tab > div.weui-tab__panel > div.weui-tab__content > div")
                    if (elements.isNullOrEmpty()) {
                        null
                    } else {
                        val list = mutableListOf<Element>()
                        elements.map {
                            if (it.hasClass("weui-cells__title") || it.hasClass("weui-cells")) list.add(it)
                        }
                        if (list.isNullOrEmpty()) {
                            null
                        } else {
                            val groupList: MutableList<TophubHomepageGroup> = mutableListOf()
                            var thisGroup = TophubHomepageGroup("", mutableListOf())
                            list.map { element ->
                                if (element.hasClass("weui-cells__title")) thisGroup = TophubHomepageGroup("", mutableListOf())
                                if (element.hasClass("weui-cells")) {
                                    val itemListHtml = element.select("a.weui-cell.weui-cell_access")
                                    itemListHtml.map { item ->
                                        val itemTitle = item.select("div.weui-cell__bd > p").html()
                                                ?: ""
                                        val itemUrl = item.attr("href") ?: ""
                                        val itemIcon = item.select("div.weui-cell__hd > img").attr("src")
                                                ?: ""
                                        thisGroup.items.add(TophubHomepageGroupItem(itemTitle, itemUrl, itemIcon))
                                    }
                                    groupList.add(thisGroup)
                                }
                            }
                            TophubHomepage(groupList)
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun getWebSite(webSiteUrl: String): TophubModSite? {
            return try {
                val result = HttpUtil.httpGetString(webSiteUrl, httpHeader)
                if (result.isNullOrEmpty()) {
                    null
                } else {
                    val doc = Jsoup.parse(result, webSiteUrl)
                    val siteTitle = doc.select("h1.custom-title").text() ?: webSiteUrl
                    val subTitle = doc.select("div.official-topic-brief").text() ?: webSiteUrl
                    val siteIcon = doc.select("img.custom-pic").attr("src") ?: ""
                    val elementGroup = doc.select("div.weui-panel.weui-panel_access")
                    if (elementGroup.size == 3) {
                        val hotList = mutableListOf<TophubModSiteList>()
                        val historyList = mutableListOf<TophubModSiteList>()
                        val recommendSiteList = mutableListOf<TophubModSiteRecommend>()
                        val hotListElement = elementGroup[0]
                        val historyListElement = elementGroup[1]
                        val recommendSiteElement = elementGroup[2]
                        val updateText = hotListElement.select("div.weui-loadmore.weui-loadmore_line > span.weui-loadmore__tips").text()
                                ?: ""
                        // Hot list
                        hotListElement.select("div.weui-panel__bd > a.weui-media-box.weui-media-box_appmsg").apply {
                            if (isNullOrEmpty()) return null
                        }?.map {
                            val itemTitle = it.select("div.weui-media-box__bd > h4.weui-media-box__title").text()
                            val subtitle = it.select("div.weui-media-box__bd > p.weui-media-box__desc").text()
                            val itemUrl = it.attr("href") ?: ""
                            hotList.add(TophubModSiteList(itemTitle, subtitle, itemUrl))
                        }
                        // History list
                        historyListElement.select("div.weui-panel__bd > a.weui-media-box.weui-media-box_appmsg").apply {
                            if (isNullOrEmpty()) return null
                        }?.map {
                            val itemTitle = it.select("div.weui-media-box__bd > h4.weui-media-box__title").text()
                            val subtitle = it.select("div.weui-media-box__bd > p.weui-media-box__desc").text()
                            val itemUrl = it.attr("href") ?: ""
                            historyList.add(TophubModSiteList(itemTitle, subtitle, itemUrl))
                        }
                        // Recommend site list
                        recommendSiteElement.select("div.weui-panel__bd > a.weui-media-box.weui-media-box_appmsg.weui-cell").apply {
                            if (isNullOrEmpty()) return null
                        }?.map {
                            val itemTitle = it.select("div.weui-media-box__bd > h4.weui-media-box__title").text()
                            val subtitle = it.select("div.weui-media-box__bd > p.weui-media-box__desc").text()
                            val itemUrl = it.attr("href") ?: ""
                            val iconUrl = it.select("div.weui-media-box__hd > img.weui-media-box__thumb radius").attr("src")
                                    ?: ""
                            recommendSiteList.add(TophubModSiteRecommend(itemTitle, subtitle, itemUrl, iconUrl))
                        }
                        TophubModSite(siteTitle, subTitle, webSiteUrl, siteIcon, updateText, hotList, historyList, recommendSiteList)
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}