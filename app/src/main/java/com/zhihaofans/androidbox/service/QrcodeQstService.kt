package com.zhihaofans.androidbox.service

import android.service.quicksettings.TileService
import com.zhihaofans.androidbox.view.QrcodeActivity
import org.jetbrains.anko.startActivity

/**
 * @author: zhihaofans

 * @date: 2018-11-11 19:03

 */
class QrcodeQstService : TileService() {

    override fun onClick() {
        super.onClick()
        startActivity<QrcodeActivity>("method" to "QRCODE_SCAN")
    }
}