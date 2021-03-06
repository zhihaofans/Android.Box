package com.zhihaofans.androidbox.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**

 * @author: zhihaofans

 * @date: 2018-12-10 19:12

 */
@SuppressLint("SimpleDateFormat")
class DatetimeOldUtil {
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


        fun datePlus(day: String, Num: Int): String {
            LogUtil.d(day)
            val df = SimpleDateFormat(if (day.indexOf(":") >= 0) "yyyy/MM/dd HH:mm:ss" else "yyyy/MM/dd")
            var nowDate: Date? = null
            try {
                nowDate = df.parse(day)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val newDate2 = Date(nowDate!!.time + Num.toLong() * 24 * 60 * 60 * 1000)
            return df.format(newDate2)
        }

        fun unixTimeStamp(): Long = System.currentTimeMillis() / 1000L


        fun unixTimeStampMill(): Long = System.currentTimeMillis()
    }
}