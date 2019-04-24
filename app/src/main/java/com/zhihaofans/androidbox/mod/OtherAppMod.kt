package com.zhihaofans.androidbox.mod

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.orhanobut.logger.Logger
import dev.utils.app.IntentUtils
import io.zhihao.library.android.ZLibrary
import io.zhihao.library.android.util.AppUtil
import java.net.URL


/**

 * @author: zhihaofans

 * @date: 2019-01-20 14:36

 */
class OtherAppMod {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private val mContext = ZLibrary.getContext()

        fun alipayQRCodeScan(): Boolean {
            //打开支付宝扫一扫
            if (!AppUtil.isAppInstalled("com.eg.android.AlipayGphone")) return false
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

        fun wechatQRCodeScan(): Boolean {
            //打开微信扫一扫
            val packageName = "com.tencent.mm"
            if (!AppUtil.isAppInstalled(packageName)) return false
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

        fun bilibiliBlackroom(): Boolean {
            //在哔哩哔哩动画客户端打开小黑屋网页
            return this.bilibiliWebView("https://www.bilibili.com/blackroom")
        }

        fun bilibiliWebView(url: String): Boolean {
            //在哔哩哔哩动画客户端打开网页（全屏显示，但无法隐藏系统状态栏）
            val packageName = "tv.danmaku.bili"
            if (!AppUtil.isAppInstalled(packageName)) {
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

        fun admAutoDownload(url: URL): Boolean = this.admAutoDownload(url.toString())
        fun admAutoDownload(url: String?): Boolean {
            if (url.isNullOrEmpty()) return false
            val _a = admProDownload1(url)
            if (_a) {
                return _a
            }
            val _b = admProDownload2(url)
            if (_b) {
                return _b
            }
            val _c = admDownload1(url)
            if (_c) {
                return _c
            }
            val _d = admDownload2(url)
            if (_d) {
                return _d
            }
            return false
        }

        fun admProDownload1(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtil.isAppInstalled(packageName)) {
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

        fun admProDownload2(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtil.isAppInstalled(packageName)) {
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

        fun admDownload1(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtil.isAppInstalled(packageName)) {
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

        fun admDownload2(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtil.isAppInstalled(packageName)) {
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

        fun browserByLynket(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "arun.com.chromer"
            val className = "arun.com.chromer.browsing.browserintercept.BrowserInterceptActivity"
            if (!AppUtil.isAppInstalled(packageName)) {
                Logger.e("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = IntentUtils.getLaunchAppIntent(packageName).apply {
                    data = url.toUri()
                    setClassName(packageName, className)
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