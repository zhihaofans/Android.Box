package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.ToastUtil
import java.util.*


/**
 * Created by zhihaofans on 2018/5/12.
 */

class Share2ClipboardActivity : Activity() {
    private var clipboardUtil: ClipboardUtil? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st != null) {
                clipboardUtil = ClipboardUtil(this@Share2ClipboardActivity)
                clipboardUtil?.copy(st)
                ToastUtil.success("已复制")
            } else {
                ToastUtil.error("复制失败")
            }
        } else {
            ToastUtil.error("复制失败")
        }
        finish()
    }

}