package com.zhihaofans.androidbox.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by zhihaofans on 2018/7/23.
 */
class ConvertUtil {

    fun time2date(time: Long): String {
        Logger.d(time)
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(Date(time)) as String
    }
    fun drawable2bitmap(context: Context, src: Int): Bitmap? {
        return BitmapFactory.decodeResource(context.resources, src)
    }

    fun boolean2string(boolean: Boolean): String {
        return boolean.toString()
    }

    fun boolean2string(boolean: Boolean, trueString: String, falseString: String): String {
        return if (boolean) {
            trueString
        } else {
            falseString
        }
    }
    fun fileSizeInt2string(fileSize: Int): String {
        var result = fileSize.toFloat()
        var times = 0
        while (result >= 1024) {
            result /= 1024
            times++
        }
        val units = mutableListOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB", "CB")
        val sizeUnit = if (times >= units.size) "???" else units[times]
        return "$result $sizeUnit"
    }

    fun json2List(jsonData: String): List<Any> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Any>>() {}.type
        return gson.fromJson(jsonData, type)
    }


}