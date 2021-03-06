package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.*
import com.zhihaofans.androidbox.kotlinEx.mmkv
import com.zhihaofans.androidbox.util.HttpUtil
import com.zhihaofans.androidbox.util.LogUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**

 * @author: zhihaofans

 * @date: 2019-04-15 15:45

 */
class TophubMod {
    val libs = TophubLibs()
    val NOW_TYPE_HOMEPAGE = "now_type_homepage"
    val NOW_TYPE_CATEGORY = "now_type_category"
    val NOW_TYPE_SITE = "now_type_site"
    private val MMKV_MOD_TOPHUB = "mod_tophub"
    private val categoryList = mutableListOf<TophubModCategoryData>().apply {
        add(TophubModCategoryData("动态", "https://tophub.today/dashboard"))
        add(TophubModCategoryData("综合", "https://tophub.today/c/news"))
        add(TophubModCategoryData("科技", "https://tophub.today/c/tech"))
        add(TophubModCategoryData("娱乐", "https://tophub.today/c/ent"))
        add(TophubModCategoryData("购物", "https://tophub.today/c/shopping"))
        add(TophubModCategoryData("社区", "https://tophub.today/c/community"))
    }

    fun getHomePage(): TophubHomepage? {
        val url = UrlMod.TOPHUB_MOD_HOMEPAGE
        try {
            val result = HttpUtil.httpGetString(url, libs.httpHeader)
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
            val result = HttpUtil.httpGetString("$url?p=$page", libs.httpHeader)
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
            val result = HttpUtil.httpGetString(webSiteUrl, libs.httpHeader)
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

    fun getDashboard(): List<TophubModSiteList>? {
        return try {
            if (!libs.isLogin()) return null
            val mUrl = "https://tophub.today/dashboard"
            val result = HttpUtil.httpGetString(mUrl, libs.httpHeader)
            if (result.isNullOrEmpty()) {
                LogUtil.e("result.isNullOrEmpty()")
                null
            } else {
                val doc = Jsoup.parse(result, mUrl)
                val itemList = doc.select("a.weui-media-box.weui-media-box_appmsg")
                LogUtil.d(itemList)
                if (itemList.isEmpty()) {
                    LogUtil.e("itemList.isEmpty()")
                    return null
                }
                itemList.map {
                    val siteName = it.select("p.weui-media-box__desc").text() ?: return null
                    var siteIcon = it.select("p.weui-media-box__desc > img.radius").attr("src")
                            ?: return null
                    val newsTitle = it.select("h4.weui-media-box__title").text() ?: return null
                    if (siteIcon.startsWith("/")) siteIcon = UrlMod.TOPHUB_MOD_HOMEPAGE + siteIcon
                    TophubModSiteList(newsTitle, siteName, siteIcon)
                }.toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}

class TophubLibs {
    val userAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36"
    var httpHeader = mapOf("User-Agent" to userAgent, "cookies" to "itc_center_user=")

    fun isLogin(): Boolean {
        return TophubCookies.get() !== null
    }


    fun loginByCookies(value: String): Boolean {
        val cookies = "itc_center_user=$value"
        updateHeaders(cookies)
        return TophubCookies.set(cookies)
    }

    fun logout(): Boolean {
        updateHeaders("")
        return TophubCookies.remove()
    }

    fun updateHeaders(cookies: String) {
        httpHeader = mapOf("User-Agent" to userAgent, "cookies" to cookies)
    }
}

class TophubCookies {

    companion object {
        private val MMKV_ID = "mod_tophub"

        fun get(): String? {
            return try {
                val cookies = mmkv(MMKV_ID).getString("cookies", "")
                if (cookies.isNullOrEmpty()) null else cookies
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun set(value: String): Boolean {
            return try {
                mmkv(MMKV_ID).putString("cookies", value)
                mmkv(MMKV_ID).getString("cookies", null) !== null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        fun remove(): Boolean {
            return try {
                mmkv(MMKV_ID).remove("cookies")
                mmkv(MMKV_ID).getString("cookies", null) == null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}