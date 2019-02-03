package com.zhihaofans.androidbox.kotlinEx

import android.widget.EditText

/**
 * @author: zhihaofans

 * @date: 2018-12-05 04:25

 */

val EditText.string: String
    get() = this.text.toString()
val EditText.int: Int
    get() = this.text.toString().toInt()
val EditText.intOrNull: Int?
    get() = this.text.toString().toIntOrNull()