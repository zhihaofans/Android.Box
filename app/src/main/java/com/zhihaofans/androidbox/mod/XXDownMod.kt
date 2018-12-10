package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.XXDownResultData
import okhttp3.Headers

/**
 * Created by zhihaofans on 2018/10/2.
 */
class XXDownMod {
    companion object {
        fun get(site: String, url: String): XXDownResultData? {
            return when (site) {
                ItemNameMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL -> XXDownSitesMod.AcfunVideoThumbnail(url)
                ItemNameMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL -> XXDownSitesMod.BilibiliVideoThumbnail(url)
                ItemNameMod.XXDOWN_SITE_GITHUB_RELEASE -> XXDownSitesMod.GithubReleaseXX(url)
                else -> null
            }

        }
    }
}

class XXDownSite {
    companion object {
        private val headers_map = mutableMapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"
        )
        var headerbuild = Headers.of(headers_map)!!

    }
}