package com.zhihaofans.androidbox.view

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.Menu
import com.jyuesong.android.kotlin.extract._browse
import com.jyuesong.android.kotlin.extract._sendSMS
import com.jyuesong.android.kotlin.extract._toast
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.mod.QrcodeMod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import com.zhihaofans.androidbox.R


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v0.0.1"
        setSupportActionBar(toolbar_main)
        qrcode.setActivity(this@MainActivity)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> startActivity<SettingActivity>()
            }
            true
        }
        _sendSMS()
        button_qrcode.onClick {
            val qrcodePlugin = qrcode.getInstalledPlugin(this@MainActivity)
            Logger.d("Qrcode Plugin:$qrcodePlugin")
            if (qrcodePlugin < 1) {
                snackbar(R.string.text_no_install_need_plugin)
            } else {
                try {
                    qrcode.scan(qrcodePlugin)
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbar("调用二维码插件失败")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) { // 0:Qrcode
                    0 -> {
                        if (qrcode.isInstallQrPlugin && data != null) {
                            Logger.d(data.extras)
                            if (data.hasExtra("data")) {
                                val result: String = data.getStringExtra("data")
                                Logger.d(result)
                                Snackbar.make(coordinatorLayout_main, result, Snackbar.LENGTH_LONG).setAction(R.string.text_share, {
                                    share(result)
                                }).show()
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> Snackbar.make(coordinatorLayout_main, R.string.text_canceled_by_user, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 自定义扩展函数
    private fun snackbar(message: CharSequence, showLongTime: Boolean = false) {
        var showTime = Snackbar.LENGTH_SHORT
        if (showLongTime) showTime = Snackbar.LENGTH_LONG
        Snackbar.make(coordinatorLayout_main, message, showTime).show()
    }

    private fun snackbar(message: Int, showLongTime: Boolean = false) {
        snackbar(getString(message), showLongTime)
    }
}
