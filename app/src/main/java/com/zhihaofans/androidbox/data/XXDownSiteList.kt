package com.zhihaofans.androidbox.data


/**
 * Created by zhihaofans on 2018/10/2.
 */

class XXDownSiteList {
    companion object {
        const val acfun = 0
        const val bilibili = 1

        private val nameList = listOf("Acfun", "Bilibili")
        fun getName(index: Int): String {
            return nameList[index]
        }

        fun getNameList(): List<String> {
            return nameList
        }
    }
}
