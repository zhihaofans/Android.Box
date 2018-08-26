package com.zhihaofans.androidbox.util

import android.R
import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

/**
 * Created by zhihaofans on 2018/8/26.
 */
fun MutableMap<String, String>.get(key: String, defaultValve: String): String {
    return this[key] ?: defaultValve
}

fun MutableList<*>?.isNullorEmpty(): Boolean {
    return this?.isEmpty() ?: true
}

fun Activity.snackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

class ListView {

    fun ListView.init(context: Context, listData: List<String>) {
        this.adapter = ArrayAdapter<String>(context, R.layout.simple_list_item_1, listData)
    }

    fun ListView.removeAllItems() {
        this.adapter = null
    }

}