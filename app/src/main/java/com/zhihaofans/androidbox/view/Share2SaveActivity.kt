package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.IntentUtil
import dev.utils.common.FileUtils
import org.jetbrains.anko.toast
import java.util.*


/**

 * @author: zhihaofans

 * @date: 2019-02-18 10:26

 */

class Share2SaveActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mIntent = intent
        if (Objects.equals(Intent.ACTION_SEND, mIntent.action) && mIntent.type != null && Objects.equals("text/plain", mIntent.type)) {
            when (mIntent.type) {
                null -> {
                }
                "text/plain" -> {
                    //FileUtils.saveFile()
                    val st: String? = mIntent.getStringExtra(Intent.EXTRA_TEXT)
                    if (st.isNullOrEmpty()) {
                        toast("空白文本，保存失败")
                        finish()
                    } else {
                        val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share.txt", "text/plain")
                        startActivityForResult(saveIntent, 0)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        // Save file
                        if (resultData != null) {
                            val uri = resultData.data
                            if (uri == null) {
                                toast("未知错误，即将退出")
                                finish()
                            } else {
                                Logger.i("Uri: $uri")
                                val saveTo = uri
                                FileUtils.getfile
                                FileUtils.saveFile()
                            }
                        } else {
                            toast("未知代码，即将退出")
                            finish()
                        }
                    }
                    else -> {
                        toast("未知代码，即将退出")
                        finish()
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