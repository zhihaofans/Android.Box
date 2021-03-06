package com.zhihaofans.androidbox.data

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-07-24 18:35

 */

data class ImageVideoData(
        val item: List<ImageVideoItemData>
)

data class ImageVideoItemData(
        val isVideo: Boolean,
        val url: String
)