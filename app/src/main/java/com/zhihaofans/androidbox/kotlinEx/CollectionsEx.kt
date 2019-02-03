package com.zhihaofans.androidbox.kotlinEx

/**
 * Created by zhihaofans on 2018/11/4.
 */
fun MutableMap<String, String>.get(key: String, defaultValve: String): String {
    return this[key] ?: defaultValve
}

fun MutableList<*>?.isNullorEmpty(): Boolean {
    return this?.isEmpty() ?: true
}

fun List<*>.hasChild(childItem: Any) = this.indexOf(childItem) >= 0
fun List<*>.hasNotChild(childItem: Any) = !this.hasChild(childItem)
fun List<String>.string(splitWord: String = ","): String {
    var resultStr = ""
    this.map {
        if (resultStr.isNotEmpty()) resultStr += splitWord
        resultStr += it
    }
    return resultStr
}