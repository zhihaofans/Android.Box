package com.zhihaofans.androidbox.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView
import com.zhihaofans.androidbox.R

/**
 * Created by zhihaofans on 2018/3/15.
 */

class ImageBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text: TextView // 标签
    val image: SimpleDraweeView // 日期

    init {
        text = itemView.findViewById<View>(R.id.titleView) as TextView
        image = itemView.findViewById<View>(R.id.imageView) as SimpleDraweeView
    }
}