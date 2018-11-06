package com.zhihaofans.androidbox.mod

import android.app.Activity
import android.content.Intent
import com.xuexiang.xqrcode.XQRCode
import com.xuexiang.xqrcode.ui.CaptureActivity


/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
class QrcodeMod {
    private var activity: Activity = Activity()
    var isInstallQrPlugin = false
    fun setActivity(activity: Activity) {
        this@QrcodeMod.activity = activity
        XQRCode.debug(false)
    }

    fun setActivity(activity: Activity, debug: Boolean) {
        this@QrcodeMod.activity = activity
        XQRCode.debug(debug)
    }
    fun file() {
    }

    fun scan(qrcodeFrame: Int) {
        //0:调用自带 1:调用mark.qrcode 2:调用org.noear.scan
        when (qrcodeFrame) {
            0 -> {
                val intent = Intent(activity, CaptureActivity::class.java)
                activity.startActivityForResult(intent, 0)
            }
            1 -> {
                val intent = Intent("mark.qrcode.SCAN")
                intent.setClassName("mark.qrcode", "mark.qrcode.CaptureActivity")
                try {
                    activity.startActivityForResult(intent, 1)
                    isInstallQrPlugin = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    isInstallQrPlugin = false
                    throw RuntimeException(e)
                }
            }
            2 -> {
                val intent = Intent("org.noear.scan.H5_SCAN")
                intent.setClassName("org.noear.scan", "org.noear.scan.CaptureActivity")
                try {
                    activity.startActivityForResult(intent, 2)
                    isInstallQrPlugin = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    isInstallQrPlugin = false
                    throw RuntimeException(e)
                }
            }
        }
    }

    /**
     * 处理二维码扫描结果
     * @param data
     */
    fun handleScanResult(data: Intent?): MutableMap<String, String> {
        val returnMessage = mutableMapOf(
                "code" to "0",
                "message" to "ok",
                "qrcodeData" to ""
        )
        if (data != null) {
            val bundle = data.extras
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    returnMessage["qrcodeData"] = bundle.getString(XQRCode.RESULT_DATA)
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    returnMessage["code"] = "1"
                    returnMessage["message"] = "RESULT_TYPE = RESULT_FAILED"
                }
            } else {
                returnMessage["code"] = "2"
                returnMessage["message"] = "bundle = null"
            }
        } else {
            returnMessage["code"] = "3"
            returnMessage["message"] = "data = null"
        }
        return returnMessage
    }
}