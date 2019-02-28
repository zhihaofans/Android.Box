package com.zhihaofans.androidbox.mod

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.orhanobut.logger.Logger
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils


/**

 * @author: zhihaofans

 * @date: 2019-01-20 14:36

 */
class OtherAppMod {

    companion object {
        fun alipayQRCodeScan(mContext: Context): Boolean {
            //打开支付宝扫一扫
            if (!AppUtils.isInstalledApp("com.eg.android.AlipayGphone")) return false
            return try {
                val uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                mContext.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun wechatQRCodeScan(mContext: Context): Boolean {
            //打开微信扫一扫
            val packageName = "com.tencent.mm"
            if (!AppUtils.isInstalledApp(packageName)) return false
            return try {
                val intent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    putExtra("LauncherUI.From.Scaner.Shortcut", true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                mContext.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun bilibiliBlackroom(mContext: Context): Boolean {
            //在哔哩哔哩动画客户端打开小黑屋网页
            return this.bilibiliWebView(mContext, "https://www.bilibili.com/blackroom")
        }

        fun bilibiliWebView(mContext: Context, url: String): Boolean {
            //在哔哩哔哩动画客户端打开网页（全屏显示，但无法隐藏系统状态栏）
            val packageName = "tv.danmaku.bili"
            if (!AppUtils.isInstalledApp(packageName)) {
                Logger.d("未安装$packageName")
                return false
            }
            return try {
                val intent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, "tv.danmaku.bili.ui.game.web.GameCenterWebActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                mContext.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun admDownload(mContext: Context, url: String?): Boolean {
            if (url.isNullOrEmpty()) return false
            val _a = admProDownload1(mContext, url)
            if (_a) {
                return _a
            }
            val _b = admProDownload2(mContext, url)
            if (_b) {
                return _b
            }
            val _c = admDownload1(mContext, url)
            if (_c) {
                return _c
            }
            val _d = admDownload2(mContext, url)
            if (_d) {
                return _d
            }
            return false
        }

        fun admProDownload1(mContext: Context, url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtils.isInstalledApp(packageName)) {
                Logger.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.adm.pay.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtils.isIntentAvailable(mIntent)) return false
                mContext.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun admProDownload2(mContext: Context, url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtils.isInstalledApp(packageName)) {
                Logger.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.get.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtils.isIntentAvailable(mIntent)) return false
                mContext.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun admDownload1(mContext: Context, url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtils.isInstalledApp(packageName)) {
                Logger.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.adm.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtils.isIntentAvailable(mIntent)) return false
                mContext.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun admDownload2(mContext: Context, url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtils.isInstalledApp(packageName)) {
                Logger.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.get.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtils.isIntentAvailable(mIntent)) return false
                mContext.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}