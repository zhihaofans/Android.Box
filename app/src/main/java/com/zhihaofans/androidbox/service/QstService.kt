package com.zhihaofans.androidbox.service

import android.service.quicksettings.TileService
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.view.QrcodeActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * @author: zhihaofans

 * @date: 2018-11-11 19:03

 */
class QrcodeScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        startActivity<QrcodeActivity>("method" to "QRCODE_SCAN")
    }
}

class WechatScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        SystemUtil.collapseNotificationBar(this)
        toast("启动" + if (OtherAppMod.wechatQRCodeScan(this)) "成功" else "失败")
    }
}

class AlipayQrcodeScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        SystemUtil.collapseNotificationBar(this)
        toast("启动" + if (OtherAppMod.alipayQRCodeScan(this)) "成功" else "失败")
    }
}