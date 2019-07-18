package com.zhihaofans.androidbox.gson

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-07-14 17:58

 */
data class GithubBranchGson(
        val name: String,
        val commit: GithubBranchCommitGson,
        val protected: Boolean
)

data class GithubBranchCommitGson(
        val sha: String,
        val url: String
)
