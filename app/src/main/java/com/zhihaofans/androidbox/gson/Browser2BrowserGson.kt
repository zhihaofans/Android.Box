package com.zhihaofans.androidbox.gson

import android.content.pm.ResolveInfo

data class AppIntentGson(
        val packageName: String,
        val className: String,
        val resolveInfo: ResolveInfo
)

data class Browser2BrowserBlockGson(
        val packageName: String,
        val className: String,
        val whiteListUri: List<String>?
)