package com.zhihaofans.androidbox.gson

/**
 * Created by zhihaofans on 2018/7/25.
 */
// Github release
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
        val body: String?
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

// Fir.im v1
data class FirimApiDownloadToken(
        val download_token: String?,
        val code: Int?,
        val errors: FirimApiErrorList
)

data class FirimApiAppInfo(
        val id: String,
        val type: String,
        val name: String,
        val desc: String,
        val short: String,
        val bundle_id: String,
        val created_at: Int,
        val icon_url: String,
        val expired_at: Int
)

data class FirimApiErrorList(
        val exception: MutableList<String>
)