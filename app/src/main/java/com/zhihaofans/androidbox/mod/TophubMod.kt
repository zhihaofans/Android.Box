package com.zhihaofans.androidbox.mod

import com.lxj.androidktx.core.mmkv
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
        const val NOW_TYPE_HOMEPAGE = "now_type_homepage"
        const val NOW_TYPE_CATEGORY = "now_type_category"
        const val NOW_TYPE_SITE = "now_type_site"
        private const val MMKV_MOD_TOPHUB = "mod_tophub"
        private const val userAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36"
        private val httpHeader = mapOf("User-Agent" to userAgent, "cookies" to "itc_center_user=")
        private val categoryList = mutableListOf<TophubModCategoryData>().apply {
            add(TophubModCategoryData("综合", "https://tophub.today/c/news"))
            add(TophubModCategoryData("科技", "https://tophub.today/c/tech"))
            add(TophubModCategoryData("娱乐", "https://tophub.today/c/ent"))
            add(TophubModCategoryData("购物", "https://tophub.today/c/shopping"))
            add(TophubModCategoryData("社区", "https://tophub.today/c/community"))
        }

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
                                if (element.hasClass("weui-cells__title")) {
                                    thisGroup = TophubHomepageGroup(element.text()
                                            ?: "", mutableListOf())
                                }
                                if (element.hasClass("weui-cells")) {
                                    val itemListHtml = element.select("a.weui-cell.weui-cell_access")
                                    itemListHtml.map { item ->
                                        val itemTitle = item.select("div.weui-cell__bd > p").html()
                                                ?: ""
                                        var itemUrl = item.attr("href") ?: ""
                                        if (itemUrl.startsWith("/")) {
                                            itemUrl = url + itemUrl
                                        }
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

        fun getCategoryList() = categoryList
        fun getCategoryContent(url: String, page: Int = 1): TophubModCategoryContentData? {
            try {
                val result = HttpUtil.httpGetString("$url?p=$page", httpHeader)
                return if (result.isNullOrEmpty()) {
                    null
                } else {
                    val doc = Jsoup.parse(result, url)
                    val categoryTitle = doc.select("a.weui_tab_nav_item.weui_tab_nav_item_red.weui_tab_nav_item_red_on").text()
                    val categorySubtitle = doc.select("div.weui-cells__title").text()
                    val itemList = doc.select("a.weui-cell.weui-cell_access")
                    if (itemList.isNullOrEmpty()) {
                        null
                    } else {
                        val list = mutableListOf<TophubModCategoryItemData>()
                        itemList.map { element ->
                            val itemTitle = element.select("div.weui-cell__bd > p").text()
                            var itemIcon = element.select("div.weui-cell__hd > img").attr("src")
                            var itemUrl = element.attr("href")
                            if (itemIcon.startsWith("/")) itemIcon = "https://tophub.today$itemIcon"
                            if (itemUrl.startsWith("/")) itemUrl = "https://tophub.today$itemUrl"
                            list.add(TophubModCategoryItemData(itemTitle, itemUrl, itemIcon))

                        }
                        TophubModCategoryContentData(categoryTitle, categorySubtitle, list)
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
                    var siteIcon = doc.select("img.custom-pic").attr("src") ?: ""
                    if (siteIcon.startsWith("/")) {
                        siteIcon = UrlMod.TOPHUB_MOD_HOMEPAGE + siteIcon
                    }
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
                            var itemUrl = it.attr("href") ?: ""
                            if (itemUrl.startsWith("/")) {
                                itemUrl = UrlMod.TOPHUB_MOD_HOMEPAGE + itemUrl
                            }
                            hotList.add(TophubModSiteList(itemTitle, subtitle, itemUrl))
                        }
                        // History list
                        historyListElement.select("div.weui-panel__bd > a.weui-media-box.weui-media-box_appmsg").apply {
                            if (isNullOrEmpty()) return null
                        }?.map {
                            val itemTitle = it.select("div.weui-media-box__bd > h4.weui-media-box__title").text()
                            val subtitle = it.select("div.weui-media-box__bd > p.weui-media-box__desc").text()
                            var itemUrl = it.attr("href") ?: ""
                            if (itemUrl.startsWith("/")) {
                                itemUrl = UrlMod.TOPHUB_MOD_HOMEPAGE + itemUrl
                            }
                            historyList.add(TophubModSiteList(itemTitle, subtitle, itemUrl))
                        }
                        // Recommend site list
                        recommendSiteElement.select("div.weui-panel__bd > a.weui-media-box.weui-media-box_appmsg.weui-cell").apply {
                            if (isNullOrEmpty()) return null
                        }?.map {
                            val itemTitle = it.select("div.weui-media-box__bd > h4.weui-media-box__title").text()
                            val subtitle = it.select("div.weui-media-box__bd > p.weui-media-box__desc").text()
                            var itemUrl = it.attr("href") ?: ""
                            if (itemUrl.startsWith("/")) {
                                itemUrl = UrlMod.TOPHUB_MOD_HOMEPAGE + itemUrl
                            }
                            var iconUrl = it.select("div.weui-media-box__hd > img.weui-media-box__thumb radius").attr("src")
                                    ?: ""
                            if (iconUrl.startsWith("/")) {
                                iconUrl = UrlMod.TOPHUB_MOD_HOMEPAGE + iconUrl
                            }
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

        fun isLogin(): Boolean {
            return getCookies() == null
        }

        fun loginByCookies(value: String): Boolean {
            return setCookies("itc_center_user=$value")
        }

        fun getCookies(): String? {
            return try {
                val cookies = mmkv(MMKV_MOD_TOPHUB).getString("cookies", "")
                if (cookies.isNullOrEmpty()) null else cookies
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun setCookies(value: String): Boolean {
            return try {
                mmkv(MMKV_MOD_TOPHUB).putString("cookies", value)
                mmkv(MMKV_MOD_TOPHUB).getString("cookies", null) == null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        fun removeCookies() {
            mmkv(MMKV_MOD_TOPHUB).remove("cookies")
        }

    }
}