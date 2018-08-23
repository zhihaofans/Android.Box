package com.zhihaofans.androidbox.mod

import android.app.Activity
import android.os.Bundle
import com.zhihaofans.androidbox.view.NewsBoxActivity
import com.zhihaofans.androidbox.view.QrcodeActivity
import com.zhihaofans.androidbox.view.ServerChanActivity
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

class ServerChanShortcuts : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<ServerChanActivity>()
        finish()
    }
}

class NewsboxShortcuts : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //SharedPreferencesUtils.init(this)
        startActivity<NewsBoxActivity>()
        finish()
    }

}