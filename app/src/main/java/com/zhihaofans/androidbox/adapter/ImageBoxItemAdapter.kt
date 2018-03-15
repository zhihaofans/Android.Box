package com.zhihaofans.androidbox.adapter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.ImageBoxDataModel
import com.zhihaofans.androidbox.holder.ImageBoxViewHolder


/**
 * Created by zhihaofans on 2018/3/15.
 */

class ImageBoxItemAdapter(dataModels: MutableList<ImageBoxDataModel>) : RecyclerView.Adapter<ImageBoxViewHolder>() {
    private var mDataModels = dataModels
    private var mHeights: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ImageBoxViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_box, parent, false)
        return ImageBoxViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ImageBoxViewHolder, position: Int) {
        val (text, imageUri) = mDataModels[position]
        // 随机高度, 模拟瀑布效果.
        if (mHeights.size <= position) {
            mHeights.add((100 + Math.random() * 300).toInt())
        }
        val lp = viewHolder.text.layoutParams
        lp.height = mHeights[position]
        viewHolder.text.text = text
        viewHolder.image.setImageURI(imageUri)
    }

    override fun getItemCount(): Int {
        return mDataModels.size
    }

    fun addData(position: Int, text: String, imageUri: String) {
        addData(position, text, Uri.parse(imageUri))
    }

    fun addData(position: Int, text: String, imageUri: Uri) {
        val model = ImageBoxDataModel(text, imageUri)
        mDataModels.add(position, model)
        notifyItemInserted(position)
    }


    fun removeData(position: Int) {
        mDataModels.removeAt(position)
        notifyItemRemoved(position)
    }
}