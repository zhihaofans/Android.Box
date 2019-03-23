package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.*
import com.zhihaofans.androidbox.mod.OpenerMod
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.util.ToastUtil
import kotlinx.android.synthetic.main.activity_opener.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class OpenerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opener)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        try {
            initIntent()
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("Exception")
            finish()
        }
    }

    private fun initIntent() {
        val mIntent = intent
        val openerMod = OpenerMod(mIntent)
        when {
            openerMod.isUrl() -> urlMenu(openerMod)
            mIntent.isActionSend -> {
                when {
                    mIntent.isTypeTextPlain -> {
                        val st = mIntent.getTextPlain()
                        if (st.isNullOrEmpty()) {
                            ToastUtil.error("分享文本为空")
                            finish()
                        } else {
                            if (st.isUrl()) {

                            } else {
                                ToastUtil.error("分享内容不是链接")
                                finish()
                            }
                        }
                    }
                    else -> {
                        ToastUtil.error("未知分享内容")
                        finish()
                    }
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

    private fun urlMenu(openerMod: OpenerMod) {
        if (openerMod.getUrl().isNull()) {
            ToastUtil.error("分享文本为空")
            finish()
        } else {
            val mUrl = openerMod.getUrl()!!
            val menuList = listOf("打开", "分享", "下载")
            MaterialDialog.Builder(this)
                    .title("要干嘛")
                    .items(menuList)
                    .cancelListener {
                        ToastUtil.warning(R.string.text_canceled_by_user)
                        finish()
                    }
                    .itemsCallback { _, _, i, _ ->
                        when (i) {
                            0 -> {
                                //TODO:打开
                            }
                            2 -> OtherAppMod.admAutoDownload(this, mUrl)
                            else -> {
                                ToastUtil.error("未知错误")
                                finish()
                            }
                        }
                        alert {
                            title = "确定使用吗?"
                            yesButton {
                                finish()
                            }
                            noButton {
                                ToastUtil.warning(R.string.text_canceled_by_user)
                                finish()
                            }
                            onCancelled {
                                ToastUtil.warning(R.string.text_canceled_by_user)
                                finish()
                            }
                        }.show()
                    }
                    .show()

        }
    }
}
