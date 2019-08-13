package com.zhihaofans.androidbox.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.view.Browser2BrowserActivity
import com.zhihaofans.androidbox.view.ImageViewActivity
import io.zhihao.library.android.kotlinEx.isUrl
import io.zhihao.library.android.kotlinEx.toUrl
import io.zhihao.library.android.util.DeviceUtil
import io.zhihao.library.android.util.FileUtil
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import java.net.URL


/**
 *
 * @author zhihaofans
 * @date 2018/1/5
 */
@SuppressLint("SimpleDateFormat")
class SystemUtil {
    companion object {
        fun chromeCustomTabs(context: Context, url: String) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabsIntent: CustomTabsIntent = builder.build()
            builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark))
            builder.setShowTitle(true)
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }

        fun browse(context: Context, url: String, title: String = url) {
            if (DeviceUtil.isXiaomi()) {
                this.browser2browser(context, url)
            } else {
                val appSettingMod = AppSettingMod()
                if (!url.isUrl()) {
                    throw Exception("Need Url.")
                }
                if (appSettingMod.imageUrlOpenWithBuiltinViewer && this.checkIfImageUrl(url)) {
                    context.startActivity<ImageViewActivity>("image" to url, "title" to title)
                } else {
                    try {
                        this.chromeCustomTabs(context, url)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        context.browse(url)
                    }
                }
            }
        }

        fun checkIfImageUrl(imageUrl: String): Boolean {
            return when (FileUtil.getFileSuffix(imageUrl).toLowerCase()) {
                "jpg", "jpeg", "bmp", "webp", "gif" -> true
                else -> false
            }
        }

        fun getUrlFromBiliShare(shareString: String): URL? {
            val biliScheme = listOf("http", "https", "bilibili")
            return if (shareString.isUrl()) {
                shareString.toUrl()
            } else {
                var result = ""
                LogUtil.d("getUrlFromBiliShare:1")
                biliScheme.map {
                    LogUtil.d("Scheme:$it")
                    val _a = shareString.indexOf("$it://")
                    if (_a >= 0) {
                        val _b = shareString.indexOf(" ", _a)
                        LogUtil.d("_a:$_a\n_b:$_b")
                        result = if (_b > _a) {
                            shareString.substring(_a, _b)
                        } else {
                            shareString.substring(_a, shareString.length - 1)
                        }
                        LogUtil.d("result:$result")
                        val checked = result.toUrl()
                        LogUtil.d("getUrlFromBiliShare:3")
                        return checked
                    }
                }
                LogUtil.d("getUrlFromBiliShare:4")
                return if (result.isEmpty()) {
                    null
                } else {
                    result.toUrl()
                }

            }
        }

        fun browser2browser(mContext: Context, uri: String): Boolean {
            return try {
                mContext.startActivity<Browser2BrowserActivity>("uri" to uri)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}