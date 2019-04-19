package com.zhihaofans.androidbox.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.entity.MultipleItem

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-04-19 13:24

 */
class MultipleItemQuickAdapter(data: List<MultipleItem>) : BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder>(data) {

    init {
        addItemType(MultipleItem.TEXT, R.layout.item_text_view)
        addItemType(MultipleItem.IMG, R.layout.item_image_view)
        addItemType(MultipleItem.IMG_TEXT, R.layout.item_img_text_view)
    }

    override fun convert(helper: BaseViewHolder, item: MultipleItem) {
        when (helper.itemViewType) {
            MultipleItem.TEXT -> helper.setText(R.id.tv, item.content)
            MultipleItem.IMG_TEXT -> helper.setImageResource(R.id.iv, R.mipmap.ic_launcher)
        }
    }
}