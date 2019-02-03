package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.mod.AppSettingMod
import org.jetbrains.anko.toast


class Browser2BrowserActivity : AppCompatActivity() {
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

        }
    }
}