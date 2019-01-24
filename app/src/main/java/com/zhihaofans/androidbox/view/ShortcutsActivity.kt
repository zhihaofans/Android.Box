package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.mod.FeedShortcuts
import com.zhihaofans.androidbox.mod.QrcodeScanShortcuts
import com.zhihaofans.androidbox.util.ShortcutsUtil
import kotlinx.android.synthetic.main.activity_shortcuts.*
import kotlinx.android.synthetic.main.content_shortcuts.*
import org.jetbrains.anko.toast

class ShortcutsActivity : AppCompatActivity() {
    private val shortcutsUtil = ShortcutsUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcuts)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            init()
        } else {
            toast("你的手机不支持创建快捷方式")
        }
    }

    private fun init() {
        val shortcutList = listOf(
                getString(R.string.text_feed),
                getString(R.string.text_qrcode_scan)
        )
        listViewShortcuts.init(this, shortcutList)
        listViewShortcuts.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val launchIntent = Intent(this, FeedShortcuts::class.java)
                    if (shortcutsUtil.addPinShortcut("$packageName.FeedActivity", launchIntent, shortcutList[position])) {
                        toast("创建快捷方式成功")
                    } else {
                        toast("创建快捷方式失败")
                    }
                }
                1 -> {
                    val launchIntent = Intent(this, QrcodeScanShortcuts::class.java)
                    if (shortcutsUtil.addPinShortcut("$packageName.QrcodeScan", launchIntent, shortcutList[position])) {
                        toast("创建快捷方式成功")
                    } else {
                        toast("创建快捷方式失败")
                    }
                }
                else -> toast("未知错误")
            }
        }
    }
}
