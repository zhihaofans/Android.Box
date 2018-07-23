package com.zhihaofans.androidbox.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


/**
 * Created by zhihaofans on 2018/7/23.
 */
class ConvertUtil {
    class Image {
        fun drawable2bitmap(context: Context, src: Int): Bitmap? {
            return BitmapFactory.decodeResource(context.resources, src)
        }
    }
}