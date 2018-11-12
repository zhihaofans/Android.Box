package com.zhihaofans.androidbox.service

import android.service.quicksettings.TileService
import com.zhihaofans.androidbox.view.QrcodeActivity
import org.jetbrains.anko.startActivity

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-11 19:03

 */
class QrcodeQstService : TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        // 当用户添加Tile到快速设置区域时调用，可以在这里进行一次性的初始化操作。
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        // 当用户从快速设置区域移除一个Tile时调用，这里不要做有关于此Tile的任何操作。
    }

    override fun onStartListening() {
        super.onStartListening()
        // 当Tile变为可见时调用，这里可以进行更新Tile，注册监听或回调等操作。

    }

    override fun onStopListening() {
        super.onStopListening()
        // 当Tile变为不可见时调用，这里可以进行注销监听或回调等操作。

    }

    override fun onClick() {
        super.onClick()
        startActivity<QrcodeActivity>("method" to "QRCODE_SCAN")
    }
}