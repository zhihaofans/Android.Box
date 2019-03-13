package com.zhihaofans.androidbox.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.browser.customtabs.CustomTabsIntent
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.isUrl
import com.zhihaofans.androidbox.kotlinEx.toUrl
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.view.Browser2BrowserActivity
import com.zhihaofans.androidbox.view.ImageViewActivity
import dev.utils.common.FileUtils
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
        fun debug(context: Context): Boolean {
            return this.isApkDebugable(context)
        }


        fun closeKeyborad(activity: Activity) {
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }

        fun chromeCustomTabs(context: Context, url: String) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabsIntent: CustomTabsIntent = builder.build()
            builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark))
            builder.setShowTitle(true)
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }

        fun browse(context: Context, url: String, title: String = url) {
            val appSettingMod = AppSettingMod()
            appSettingMod.init(context)
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

        fun checkIfImageUrl(imageUrl: String): Boolean {
            return when (FileUtils.getFileSuffix(imageUrl).toLowerCase()) {
                "jpg", "jpeg", "bmp", "webp", "gif" -> true
                else -> false
            }
        }

        fun viewGetFocusable(editText: EditText) {
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
        }

        fun isApkDebugable(context: Context): Boolean {
            try {
                val info = context.applicationInfo
                return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: Exception) {

            }

            return false
        }

        fun getUrlFromBiliShare(shareString: String): URL? {
            val biliScheme = listOf("http", "https", "bilibili")
            var checked = shareString.toUrl()
            var result: String? = null
            Logger.d("getUrlFromBiliShare:1")
            if (checked !== null) {
                return checked
            } else {
                Logger.d("getUrlFromBiliShare:2")
                biliScheme.map {
                    Logger.d("Scheme:$it")
                    val _a = shareString.indexOf("$it://")
                    if (_a >= 0) {
                        val _b = shareString.indexOf(" ", _a)
                        Logger.d("_a:$_a\n_b:$_b")
                        if (_b > _a) {
                            result = shareString.substring(_a, _b)
                        } else {
                            result = shareString.substring(_a, shareString.length - 1)
                        }
                        Logger.d("result:$result")
                        checked = result.toUrl()
                        Logger.d("getUrlFromBiliShare:3")
                        return if (checked !== null) {
                            checked
                        } else {
                            null
                        }
                    }
                }
                Logger.d("getUrlFromBiliShare:4")
                return if (result.isNullOrEmpty()) {
                    null
                } else {
                    result.toUrl()
                }
            }
        }


        fun listViewAdapter(context: Context, listData: List<String>): ArrayAdapter<String> {
            return ArrayAdapter(context, android.R.layout.simple_list_item_1, listData)
        }

        fun collapseNotificationBar(mContext: Context) {
            //折叠通知栏
            //感谢：https://stackoverflow.com/questions/15568754
            mContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
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