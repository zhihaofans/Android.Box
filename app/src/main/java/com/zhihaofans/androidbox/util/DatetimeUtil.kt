package com.zhihaofans.androidbox.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**

 * @author: zhihaofans

 * @date: 2018-12-10 19:12

 */
class DatetimeUtil {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun githubUtc2Local(utcTime: String): String {
            var githubUtc = utcTime
            githubUtc = githubUtc.replace("T", " ")
            githubUtc = githubUtc.replace("Z", " ")
            val utcFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//UTC时间格式
            utcFormater.timeZone = TimeZone.getTimeZone("UTC")
            var gpsUTCDate: Date? = null
            try {
                gpsUTCDate = utcFormater.parse(githubUtc)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val localFormater = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")//当地时间格式
            localFormater.timeZone = TimeZone.getDefault()
            return localFormater.format(gpsUTCDate!!.time)
        }

        fun unixTime2date(time: Int): String {
            return unixTime2date(time.toLong())
        }

        fun unixTime2date(time: Long): String {
            return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(Date(time)) as String
        }

    }
}