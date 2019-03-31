package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.kotlinEx.isActionSend
import io.zhihao.library.android.kotlinEx.isNotNullAndEmpty


/**
 * Created by zhihaofans on 2018/5/12.
 */

class Share2ClipboardActivity : Activity() {
    private var clipboardUtil: ClipboardUtil? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mIntent = intent
        if (mIntent.isActionSend && mIntent.type == "text/plain") {
            val st = mIntent.getStringExtra(Intent.EXTRA_TEXT)
            if (st.isNotNullAndEmpty()) {
                clipboardUtil = ClipboardUtil(this@Share2ClipboardActivity)
                if (clipboardUtil == null) {
                    ToastUtil.error("复制失败")
                } else {
                    clipboardUtil!!.copy(st)
                    ToastUtil.success("已复制")
                }
            } else {
                ToastUtil.error("复制失败")
            }
        } else {
            ToastUtil.error("复制失败")
        }
        finish()
    }

}