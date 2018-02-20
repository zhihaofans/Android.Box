package com.zhihaofans.androidbox.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zhihaofans.androidbox.R
import java.util.Objects
import android.content.Intent
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import org.jetbrains.anko.*


class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null) {
            when (intent.type) {
                "text/plain" -> {
                    val st = intent.getStringExtra(Intent.EXTRA_TEXT) as String
                    if (st.isNotEmpty()) {
                        Logger.d(st)

                        alert {
                            verticalLayout {
                                val et: String = editText(st).text.toString()
                                noButton {}
                                yesButton {
                                    val acts = listOf<String>(getString(R.string.text_copy), getString(R.string.text_share))
                                    selector("要干嘛", acts, { _, i ->
                                        when (i) {
                                            0 -> ClipboardUtils.copy(this@ShareActivity, et)
                                            1 -> share(et)
                                        }
                                    })
                                }
                            }
                        }.show()
                    } else {
                        alert("空白数据", getString(R.string.text_error)) {
                            okButton {
                                finish()
                            }
                        }.show()
                    }
                }
                else -> alert("空白数据", getString(R.string.text_error)) {
                    okButton {
                        finish()
                    }
                }.show()
            }
        } else {
            alert("空白数据", getString(R.string.text_error)) {
                okButton {
                    finish()
                }
            }.show()
        }
    }
}

private fun showFun(mode: Int) {
    when (mode) {
        0 -> {
            // String
        }
    }
}