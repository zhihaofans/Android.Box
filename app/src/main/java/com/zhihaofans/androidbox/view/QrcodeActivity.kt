package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.GlobalSettingMod
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_qrcode.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.share


class QrcodeActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val sysUtil = SystemUtil()
    private val globalSetting = GlobalSettingMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        setSupportActionBar(toolbar)
        qrcode.setActivity(this@QrcodeActivity)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        val qrResult = qrcode.handleScanResult(data)
                        when (qrResult["code"]) {
                            "0" -> {
                                val qrData = qrResult["qrcodeData"].toString()
                                Logger.d(qrData)
                                Snackbar.make(coordinatorLayout_qrcode, qrData, Snackbar.LENGTH_LONG).setAction(R.string.text_more, {
                                    val acts = mutableListOf<String>(getString(R.string.text_open), getString(R.string.text_copy), getString(R.string.text_share))
                                    selector("", acts, { _, index ->
                                        when (index) {
                                            0 -> sysUtil.browseWeb(this@QrcodeActivity, qrData)
                                            1 -> ClipboardUtils.copy(this@QrcodeActivity, qrData)
                                            2 -> share(qrData)
                                        }
                                    })
                                }).show()

                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> Snackbar.make(coordinatorLayout_qrcode, R.string.text_canceled_by_user, Snackbar.LENGTH_SHORT).show()
        }
    }


}
