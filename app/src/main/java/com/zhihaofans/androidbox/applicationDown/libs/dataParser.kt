package com.zhihaofans.androidbox.applicationDown.libs

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-07-06 14:58

 */
data class AppInfoGson(
        val bucket_rule_version: Int,
        val app_name: AppInfoI18nGson,
        val developer: String,
        val homepage: String,
        val icon: String,
        val description: AppInfoI18nGson,
        val version_name: String,
        val version_code: Int,
        val min_sdk_version: Int,
        val apk_size: String,
        val downloads: String?
)

data class AppInfoI18nGson(
        val en_us: String?,
        val zh_cn: String?
)