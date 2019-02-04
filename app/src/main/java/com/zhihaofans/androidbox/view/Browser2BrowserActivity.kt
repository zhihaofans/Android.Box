package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.gson.Gson
import com.zhihaofans.androidbox.gson.AppIntentGson
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.util.IntentUtil
import dev.utils.app.AppUtils
import dev.utils.app.IntentUtils
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast


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
            val mIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri.toUri()
                newTask()
            }
            val pm = AppUtils.getPackageManager()
            try {
                val appList = pm.queryIntentActivities(mIntent, 0)
                if (appList.isNullOrEmpty()) {
                    toast("启动失败,未安装可启动的应用")
                    finish()
                } else {
                    startActivity(mIntent)
                    toast("已经尝试启动应用")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast("启动失败,出现Exception")
                finish()
            }
        } else {
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