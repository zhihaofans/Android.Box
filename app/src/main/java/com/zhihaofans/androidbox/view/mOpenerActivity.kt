package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zhihaofans.androidbox.kotlinEx.isActionSend
import com.zhihaofans.androidbox.kotlinEx.isActionView
import com.zhihaofans.androidbox.kotlinEx.isUrl
import com.zhihaofans.androidbox.util.ToastUtil

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-22 21:29

 */
class mOpenerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mIntent = intent
        when {
            mIntent.isActionSend && mIntent.type == "text/plain" -> {
                val st = mIntent.getStringExtra(Intent.EXTRA_TEXT)
                if (st != null) {
                    if (st.isUrl()) {
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            }
            mIntent.isActionView -> {
                val uri = mIntent.data
                if (uri !== null) {
                } else {
                    finish()
                }
            }
            else -> {
                ToastUtil.error("?")
                finish()
            }
        }
    }

}