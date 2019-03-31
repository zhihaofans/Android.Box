package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.gson.Gson
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.label
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.mod.Browser2BrowserMod
import com.zhihaofans.androidbox.util.IntentUtil
import com.zhihaofans.androidbox.util.ToastUtil
import dev.utils.app.AppUtils
import io.zhihao.library.android.kotlinEx.isActionSend
import io.zhihao.library.android.kotlinEx.isActionView
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class Browser2BrowserActivity : AppCompatActivity() {
    private val g = Gson()
    private val appSettingMod = AppSettingMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("初始化失败")
            finish()
        }
    }

    private fun init() {
        appSettingMod.init(this)
        val mIntent = intent
        if (mIntent.isActionView) {
            val uri = mIntent.data
            if (uri == null) {
                ToastUtil.error("uri = null")
                finish()
            } else {
                val mUri = uri.toString()
                if (mUri.isEmpty()) {
                    ToastUtil.error("uri isEmpty")
                    finish()
                } else {
                    open(mUri)
                }
            }
        } else if (mIntent.isActionSend && mIntent.type == "text/plain") {
            val uri = mIntent.getStringExtra(Intent.EXTRA_TEXT)
            if (uri.isNullOrEmpty()) {
                ToastUtil.error("uri = null")
                finish()
            } else {
                val mUri = uri.toString()
                if (mUri.isEmpty()) {
                    ToastUtil.error("uri isEmpty")
                    finish()
                } else {
                    open(mUri)
                }
            }
        } else if (intent.extras !== null) {
            val uri = intent.extras!!.getString("uri", null)
            if (uri.isNullOrEmpty()) {
                ToastUtil.error("uri = null")
                finish()
            } else {
                open(uri)
            }
        } else {
            ToastUtil.error("null")
            finish()
        }
    }

    private fun open(uri: String) {
        val defaultBrowser = appSettingMod.browser2BrowserDefault
        if (defaultBrowser.isNullOrEmpty()) {
            // 未设置默认浏览器
            try {
                val appList = Browser2BrowserMod.getLauncherListWithBlackList(uri)
                if (appList.isNullOrEmpty()) {
                    ToastUtil.error("启动失败,未安装可启动的应用")
                    finish()
                } else {
                    val appNameList = appList.map {
                        val activityName = it.resolveInfo.activityInfo.label
                        val appName = AppUtils.getAppName(it.packageName)
                        "${if (activityName.isNullOrEmpty()) appName else activityName} ($appName)"
                    }.toList()
                    MaterialDialog.Builder(this)
                            .title("选择浏览器")
                            .items(appNameList)
                            .cancelListener {
                                ToastUtil.warning(R.string.text_canceled_by_user)
                                finish()
                            }
                            .itemsCallback { _, _, i, _ ->
                                val mBrowser = appList[i]
                                val browserIntent = IntentUtil.getLaunchAppIntentWithClassName(mBrowser.packageName, mBrowser.className)
                                alert {
                                    title = "确定使用${appNameList[i]}打开网页吗?"
                                    yesButton {
                                        browserIntent.data = uri.toUri()
                                        browserIntent.action = Intent.ACTION_VIEW
                                        startActivity(browserIntent)
                                        ToastUtil.success("已经尝试启动应用(${appNameList[i]})")
                                        finish()
                                    }
                                    noButton {
                                        ToastUtil.warning(R.string.text_canceled_by_user)
                                        finish()
                                    }
                                    onCancelled {
                                        ToastUtil.warning(R.string.text_canceled_by_user)
                                        finish()
                                    }
                                }.show()
                            }
                            .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.error("启动失败,出现Exception")
                finish()
            }
        } else {
            //TODO:已设置默认浏览器
            /*
            try {
                val appIntentGson = g.fromJson(defaultBrowser, AppIntentData::class.java)
                val packageName = appIntentGson.packageName
                val className = appIntentGson.className
                val intent = IntentUtil.getLaunchAppIntentWithClassName(packageName, className).apply {
                    data = uri.toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                if (IntentUtils.isIntentAvailable(intent) && IntentUtil.isIntentHasAppToLaunch(intent)) {
                    startActivity(intent)
                    toast("已经尝试启动应用")
                } else {
                    toast("启动失败,不允许启动")
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast("启动失败,出现Exception")
                finish()
            }*/
        }
    }
}