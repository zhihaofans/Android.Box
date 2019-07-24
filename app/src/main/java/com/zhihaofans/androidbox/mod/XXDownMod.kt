package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.XXDownResultData
import okhttp3.Headers.Companion.toHeaders
import java.net.URL

/**
 * Created by zhihaofans on 2018/10/2.
 */
class XXDownMod {
    companion object {
        fun get(site: String, url: URL): XXDownResultData? {
            return when (site) {
                ItemIdMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL -> XXDownSitesMod.AcfunVideoThumbnail(url)
                ItemIdMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL -> XXDownSitesMod.BilibiliVideoThumbnail(url)
                ItemIdMod.XXDOWN_SITE_GITHUB_RELEASE -> XXDownSitesMod.githubReleaseXX(url)
                ItemIdMod.XXDOWN_SITE_INSTAGRAM -> XXDownSitesMod.instagram(url)
                else -> null
            }

        }
    }
}

class XXDownSite {
    companion object {
        val headers_map = mutableMapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"
        )
        val headerbuild = headers_map.toHeaders()

    }
}