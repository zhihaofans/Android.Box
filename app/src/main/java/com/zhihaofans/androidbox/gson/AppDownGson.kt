package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/7/25.
 */
data class GithubReleaseFailed(
        val message: String,
        val documentation_url: String
)

data class GithubReleaseItem(
        val url: String,
        val html_url: String,
        val id: Int,
        val tag_name: String,
        val name: String?,
        val published_at: String,
        val assets: MutableList<GithubReleaseItemAsset>,
        val body: String
)

data class GithubReleaseItemAsset(
        val url: String,
        val id: Int,
        val name: String,
        val content_type: String,
        val size: Int,
        val download_count: Int,
        val updated_at: String,
        val browser_download_url: String

)