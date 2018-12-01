package com.zhihaofans.androidbox.kotlinEx

/**
 * Created by zhihaofans on 2018/9/15.
 */

// String
fun String.find(string: String, startIndex: Int = 0, ignoreCase: Boolean = false): Int = this.indexOf(string, startIndex, ignoreCase)

fun String.remove(removeString: String, ignoreCase: Boolean = false) = this.replace(removeString, "", ignoreCase)


// String?
fun String?.isNotNull() = this != null

fun String?.isNotNullAndEmpty() = !this.isNullOrEmpty()