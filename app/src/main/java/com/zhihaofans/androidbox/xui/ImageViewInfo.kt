package com.zhihaofans.androidbox.xui

/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable

import com.xuexiang.xui.widget.imageview.preview.enitity.IPreviewInfo

/**
 * 图片预览实体类
 *
 * @author xuexiang
 * @since 2018/12/7 下午5:34
 */
class ImageViewInfo : IPreviewInfo {
    private var mUrl: String? = null  //图片地址
    private var mBounds: Rect? = null // 记录坐标
    private var mVideoUrl: String? = null
    var description: String? = "描述信息"

    constructor(url: String) {
        mUrl = url
    }

    constructor(videoUrl: String, url: String) {
        mUrl = url
        mVideoUrl = videoUrl
    }

    protected constructor(`in`: Parcel) {
        mUrl = `in`.readString()
        mBounds = `in`.readParcelable(Rect::class.java.classLoader)
        description = `in`.readString()
        mVideoUrl = `in`.readString()
    }

    override fun getUrl(): String? {//将你的图片地址字段返回
        return mUrl
    }

    fun setUrl(url: String) {
        mUrl = url
    }

    override fun getBounds(): Rect? {//将你的图片显示坐标字段返回
        return mBounds
    }

    fun setBounds(bounds: Rect) {
        mBounds = bounds
    }

    override fun getVideoUrl(): String? {
        return mVideoUrl
    }

    fun setVideoUrl(videoUrl: String) {
        mVideoUrl = videoUrl
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mUrl)
        dest.writeParcelable(mBounds, flags)
        dest.writeString(description)
        dest.writeString(mVideoUrl)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImageViewInfo> = object : Parcelable.Creator<ImageViewInfo> {
            override fun createFromParcel(source: Parcel): ImageViewInfo {
                return ImageViewInfo(source)
            }

            override fun newArray(size: Int): Array<ImageViewInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
