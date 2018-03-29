package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/3/29.
 */
data class WeatherNowGson(
        val results: MutableList<WeatherNowResultGson>?,
        val status: String,
        val status_code: String
)

data class WeatherNowResultGson(
        val location: WeatherNowResultLocationGson,
        val now: WeatherNowResultNowGson,
        val last_update: String
)

data class WeatherNowResultLocationGson(
        val id: String,
        val name: String,
        val country: String,
        val path: String,
        val timezone: String,
        val timezone_offset: String
)

data class WeatherNowResultNowGson(
        val text: String,
        val code: String,
        val temperature: String
)