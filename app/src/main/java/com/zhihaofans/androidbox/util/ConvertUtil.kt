package com.zhihaofans.androidbox.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


/**
 * Created by zhihaofans on 2018/7/23.
 */
class ConvertUtil {
    companion object {
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


        fun json2List(jsonData: String): List<Any> {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<Any>>() {}.type
            return gson.fromJson(jsonData, type)
        }
    }

}