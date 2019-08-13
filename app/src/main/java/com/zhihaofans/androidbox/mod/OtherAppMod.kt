package com.zhihaofans.androidbox.mod

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.util.LogUtil
import io.zhihao.library.android.util.AppUtil
import io.zhihao.library.android.util.IntentUtil
import java.net.URL


/**

 * @author: zhihaofans

 * @date: 2019-01-20 14:36

 */
class OtherAppMod {

    companion object {
        @SuppressLint("StaticFieldLeak")

        fun alipayQRCodeScan(): Boolean {
            //打开支付宝扫一扫
            if (!AppUtil.isAppInstalled("com.eg.android.AlipayGphone")) return false
            return try {
                val uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                AppUtil.startActivity(intent)
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
                val intent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    putExtra("LauncherUI.From.Scaner.Shortcut", true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                AppUtil.startActivity(intent)
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
                LogUtil.d("未安装$packageName")
                return false
            }
            return try {
                val intent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, "tv.danmaku.bili.ui.game.web.GameCenterWebActivity")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                AppUtil.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun admAutoDownload(url: URL): Boolean = this.admAutoDownload(url.toString())
        fun admAutoDownload(url: String?): Boolean {
            if (url.isNullOrEmpty()) return false
            val a = admProDownload1(url)
            if (a) {
                return a
            }
            val b = admProDownload2(url)
            if (b) {
                return b
            }
            val c = admDownload1(url)
            if (c) {
                return c
            }
            val d = admDownload2(url)
            if (d) {
                return d
            }
            return false
        }

        private fun admProDownload1(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtil.isAppInstalled(packageName)) {
                LogUtil.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.adm.pay.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtil.isIntentAvailable(mIntent)) return false
                AppUtil.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        private fun admProDownload2(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm.pay"
            if (!AppUtil.isAppInstalled(packageName)) {
                LogUtil.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.get.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtil.isIntentAvailable(mIntent)) return false
                AppUtil.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        private fun admDownload1(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtil.isAppInstalled(packageName)) {
                LogUtil.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.adm.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtil.isIntentAvailable(mIntent)) return false
                AppUtil.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        private fun admDownload2(url: String): Boolean {
            if (url.isEmpty()) return false
            val packageName = "com.dv.adm"
            if (!AppUtil.isAppInstalled(packageName)) {
                LogUtil.d("未安装 $packageName")
                return false
            }
            return try {
                val mIntent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, "com.dv.get.AEditor")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtil.isIntentAvailable(mIntent)) return false
                AppUtil.startActivity(mIntent)
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
                val mIntent = (IntentUtil.getLaunchAppIntent(packageName) ?: return false).apply {
                    data = url.toUri()
                    setClassName(packageName, className)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (!IntentUtil.isIntentAvailable(mIntent)) return false
                AppUtil.startActivity(mIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}