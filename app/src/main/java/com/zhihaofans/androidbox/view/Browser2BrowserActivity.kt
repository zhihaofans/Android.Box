package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.gson.Gson
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.AppIntentGson
import com.zhihaofans.androidbox.kotlinEx.label
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.mod.Browser2BrowserMod
import com.zhihaofans.androidbox.util.IntentUtil
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils
import org.jetbrains.anko.*


class Browser2BrowserActivity : AppCompatActivity() {
    private val g = Gson()
    private val appSettingMod = AppSettingMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("初始化失败")
            finish()
        }
    }

    private fun init() {
        appSettingMod.init(this)
        val mIntent = intent
        if (mIntent.action == Intent.ACTION_VIEW) {
            val uri = mIntent.data
            if (uri == null) {
                toast("uri = null")
                finish()
            } else {
                val mUri = uri.toString()
                if (mUri.isEmpty()) {
                    toast("uri isEmpty")
                    finish()
                } else {
                    open(mUri)
                }
            }
        }
    }

    private fun open(uri: String) {
        //TODO:Browser2BrowserActivity()
        val defaultBrowser = appSettingMod.browser2BrowserDefault
        if (defaultBrowser.isNullOrEmpty()) {
            // 未设置默认浏览器
            val mIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri.toUri()
                newTask()
            }
            try {
                val appList = Browser2BrowserMod.getLauncherListWithBlackList(uri)
                if (appList.isNullOrEmpty()) {
                    toast("启动失败,未安装可启动的应用")
                    finish()
                } else {
                    val appNameList = appList.map {
                        val activityName = it.resolveInfo.activityInfo.label
                        if (activityName.isNullOrEmpty()) AppUtils.getAppName(it.packageName) else activityName
                    }.toList()
                    selector("选择浏览器", appNameList) { _: DialogInterface, i: Int ->
                        val mBrowser = appList[i]
                        val browserIntent = IntentUtil.getLaunchAppIntentWithClassName(mBrowser.packageName, mBrowser.className)
                        alert {
                            title = "确定使用${appNameList[i]}打开网页吗?"
                            yesButton {
                                browserIntent.data = uri.toUri()
                                startActivity(browserIntent)
                                toast("已经尝试启动应用(${appNameList[i]})")
                                finish()
                            }
                            noButton {
                                toast(R.string.text_canceled_by_user)
                                finish()
                            }
                            onCancelled {
                                toast(R.string.text_canceled_by_user)
                                finish()
                            }
                        }.show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast("启动失败,出现Exception")
                finish()
            }
        } else {
            // 已设置默认浏览器
            try {
                val appIntentGson = g.fromJson(defaultBrowser, AppIntentGson::class.java)
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
            }
        }
    }
}