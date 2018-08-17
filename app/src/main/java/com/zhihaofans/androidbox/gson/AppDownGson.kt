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
data class FirimApiLatestUpdate(
        val name: String,
        val version: String,
        val changelog: String,
        val updated_at: Int,
        val versionShort: String,
        val build: String,
        val installUrl: String,
        val install_url: String,
        val direct_install_url: String,
        val update_url: String,
        val binary: FirimApiLatestUpdateBinary
)

data class FirimApiLatestUpdateError(
        val errors: FirimApiErrorList?,
        val code: Int?
)

data class FirimApiLatestUpdateBinary(
        val fsize: Int
)

data class FirimApiErrorList(
        val exception: MutableList<String>
)