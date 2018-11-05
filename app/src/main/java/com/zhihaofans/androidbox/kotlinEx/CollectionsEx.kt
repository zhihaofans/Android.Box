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