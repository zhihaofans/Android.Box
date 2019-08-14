package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.ImageVideoData
import com.zhihaofans.androidbox.data.ImageVideoItemData
import org.jsoup.Jsoup

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-07-24 18:32

 */
class HtmlParserMod {
    companion object {
        fun tenInsta(htmlText: String): ImageVideoData? {
            // Instagram
            val doc = Jsoup.parse(htmlText)
            val items = doc.select("div.portfolio-item")
            if (items.size == 0) return null
            val itemList = items.map {
                val itemTypeText = it.select("div.card > div.card-body > h4.card-title > a").text().toLowerCase()
                val itemUrl = it.select("div.card > div.card-body > p.card-text > a").attr("href")
                val isVideo = itemTypeText !== "image"
                ImageVideoItemData(isVideo, itemUrl)
            }
            return ImageVideoData(itemList)
        }

        fun tubeoffline(htmlText: String): ImageVideoData? {
            // Instagram
            val doc = Jsoup.parse(htmlText)
            val items = doc.select("div#videoDownload > table > tbody > tr > td > a")
            if (items.size == 0) return null
            val itemList = items.map {
                //val itemTypeText = it.select("div.card > div.card-body > h4.card-title > a").text().toLowerCase()
                val itemUrl = it.attr("href")
                val isVideo = false
                ImageVideoItemData(isVideo, itemUrl)
            }
            return ImageVideoData(itemList)
        }
    }
}