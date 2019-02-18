package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.IntentUtil
import org.jetbrains.anko.toast
import java.util.*

/**

 * @author: zhihaofans

 * @date: 2019-02-18 10:26

 */

class Share2SaveActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            when (intent.type) {
                null -> {
                }
                "text/plain" -> {
                    //FileUtils.saveFile()
                    val st: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (st.isNullOrEmpty()) {
                        toast("空白文本，保存失败")
                        finish()
                    } else {
                        val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share.txt")
                    }
                }
                else -> {

                }
            }
        } else {
            toast("只能发送内容给我")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        // Save file
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                toast(R.string.text_canceled_by_user)
                finish()
            }
        }
    }
}