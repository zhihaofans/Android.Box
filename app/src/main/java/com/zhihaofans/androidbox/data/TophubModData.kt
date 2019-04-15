package com.zhihaofans.androidbox.data

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-04-15 15:49

 */

// Home page
data class TophubHomepage(
        val groupList: List<TophubHomepageGroup>
)

data class TophubHomepageGroup(
        val title: String,
        val items: MutableList<TophubHomepageGroupItem>
)

data class TophubHomepageGroupItem(
        val title: String,
        val url: String,
        val iconUrl: String
)

// Site
data class TophubModSite(
        val title: String,
        val subtitle: String,
        val url: String,
        val iconUrl: String,
        val updateText: String,
        val hotList: List<TophubModSiteList>,
        val historyList: List<TophubModSiteList>,
        val recommendSiteList: List<TophubModSiteRecommend>
)

data class TophubModSiteList(
        val title: String,
        val subtitle: String,
        val url: String
)

data class TophubModSiteRecommend(
        val title: String,
        val subtitle: String,
        val url: String,
        val iconUrl: String
)