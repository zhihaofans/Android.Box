package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.snackbar
import io.zhihao.library.android.kotlinEx.string
import kotlinx.android.synthetic.main.activity_application_down.*

class ApplicationDownActivity : AppCompatActivity() {
    private val xuiUtil = XUIUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_down)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { mView ->
            xuiUtil.materialDialogInputString("结果", "",
                    getString(R.string.text_search), getString(R.string.text_cancel), true)
                    .apply {
                        onPositive { mDialog, _ ->
                            searchApp(mDialog.inputEditText!!.string)
                        }
                        onNegative { _, _ ->
                            snackbar(mView, getString(R.string.text_canceled_by_user))
                        }
                    }.show()
        }
    }

    fun searchApp(searchKey: String) {

    }
}
