package com.zhihaofans.androidbox.gson

data class AppIntentGson(
        val packageName: String,
        val className: String
)

data class Browser2BrowserBlockGson(
        val packageName: String,
        val className: String,
        val whiteListUri: List<String>?
)