package com.zhihaofans.androidbox.service

import android.service.quicksettings.TileService
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.view.QrcodeActivity
import io.zhihao.library.android.kotlinEx.collapseNotificationBar
import org.jetbrains.anko.startActivity

/**
 * @author: zhihaofans

 * @date: 2018-11-11 19:03

 */
class QrcodeScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        collapseNotificationBar()
        startActivity<QrcodeActivity>("method" to "QRCODE_SCAN")
    }
}

class WechatScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        collapseNotificationBar()
        if (OtherAppMod.wechatQRCodeScan()) {
            ToastUtil.success("启动成功")
        } else {
            ToastUtil.error("启动失败")
        }
    }
}

class AlipayQrcodeScanQstService : TileService() {

    override fun onClick() {
        super.onClick()
        collapseNotificationBar()
        if (OtherAppMod.alipayQRCodeScan()) {
            ToastUtil.success("启动成功")
        } else {
            ToastUtil.error("启动失败")
        }
    }
}