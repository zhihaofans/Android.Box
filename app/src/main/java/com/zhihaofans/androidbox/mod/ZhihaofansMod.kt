package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:42

 */
class ZhihaofansMod {
    private var mContext: Context? = null
    private val g = Gson()

    fun init(context: Context) {
        this.mContext = context
    }


}