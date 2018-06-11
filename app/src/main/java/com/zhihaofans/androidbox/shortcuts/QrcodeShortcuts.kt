package com.zhihaofans.androidbox.shortcuts

import android.app.Activity
import android.os.Bundle
import com.zhihaofans.androidbox.view.QrcodeActivity
import org.jetbrains.anko.startActivity

/**
 * Created by zhihaofans on 2018/6/11.
 */
class QrcodeScanShortcuts : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<QrcodeActivity>("method" to "QRCODE_SCAN")
        finish()
    }
}

class QrcodeGenerateShortcuts : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<QrcodeActivity>("method" to "QRCODE_GENERATE")
        finish()
    }
}