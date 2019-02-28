package com.zhihaofans.androidbox.data

import android.content.pm.ResolveInfo

data class Browser2BrowserBlackListData(
        val className: List<String>,
        val whiteListDomain: List<String>?
)

data class AppIntentData(
        val packageName: String,
        val className: String,
        val resolveInfo: ResolveInfo
)
