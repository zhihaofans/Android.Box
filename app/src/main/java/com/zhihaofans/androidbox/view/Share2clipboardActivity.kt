package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.wx.android.common.util.ClipboardUtils
import org.jetbrains.anko.toast
import java.util.*


/**
 * Created by zhihaofans on 2018/5/12.
 */

class Share2clipboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st != null) {
                ClipboardUtils.copy(this, st)
                toast("已复制")
            } else {
                toast("复制失败")
            }
        } else {
            toast("复制失败")
        }
        finish()
    }

}