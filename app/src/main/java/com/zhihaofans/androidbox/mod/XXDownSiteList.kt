package com.zhihaofans.androidbox.mod


/**
 * Created by zhihaofans on 2018/10/2.
 */

class XXDownSiteList {
    companion object {
        private val nameList = listOf(ItemNameMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL_NAME, ItemNameMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL_NAME)
        fun getName(index: Int): String {
            return nameList[index]
        }
        fun getNameList(): List<String> {
            return nameList
        }
    }
}
