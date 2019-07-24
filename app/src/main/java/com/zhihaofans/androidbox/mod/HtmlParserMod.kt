package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.TenInstaNetData
import com.zhihaofans.androidbox.data.TenInstaNetItemData
import org.jsoup.Jsoup

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-07-24 18:32

 */
class HtmlParserMod {
    companion object {
        fun tenInsta(htmlText: String): TenInstaNetData? {
            // Instagram
            val doc = Jsoup.parse(htmlText)
            val items = doc.select("div#grid-gallery > section.grid-wrap > div.row> div.portfolio-item")
            if (items.size == 0) return null
            val itemList = items.map {
                val itemTypeText = it.select("div.card > div.card-body > h4.card-title > a").text().toLowerCase()
                val itemUrl = it.select("div.card > div.card-body > p.card-text > a").attr("href")
                val isVideo = itemTypeText !== "image"
                TenInstaNetItemData(isVideo, itemUrl)
            }
            return TenInstaNetData(itemList)
        }
    }
}