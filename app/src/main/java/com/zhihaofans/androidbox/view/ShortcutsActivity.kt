package com.zhihaofans.androidbox.view

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.FeedShortcuts
import com.zhihaofans.androidbox.mod.QrcodeScanShortcuts
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.ZLibrary
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.util.ShortcutsUtil
import kotlinx.android.synthetic.main.activity_shortcuts.*
import kotlinx.android.synthetic.main.content_shortcuts.*

class ShortcutsActivity : AppCompatActivity() {
    private val shortcutsUtil = ShortcutsUtil()
    private val defaultIcon = Icon.createWithResource(ZLibrary.getAppContext(), R.mipmap.ic_launcher)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcuts)
        setSupportActionBar(toolbar_shortcuts)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            init()
        } else {
            ToastUtil.error("你的手机不支持创建快捷方式")
        }
    }

    private fun init() {
        val shortcutList = listOf(
                getString(R.string.text_feed),
                getString(R.string.text_qrcode_scan),
                getString(R.string.title_activity_tophub),
                getString(R.string.text_setting)
        )
        listViewShortcuts.init(shortcutList)
        listViewShortcuts.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val launchIntent = Intent(this, FeedShortcuts::class.java)
                    addPinShortcut("$packageName.FeedActivity", launchIntent, shortcutList[position], defaultIcon)
                }
                1 -> {
                    val launchIntent = Intent(this, QrcodeScanShortcuts::class.java)
                    addPinShortcut("$packageName.QrcodeScan", launchIntent, shortcutList[position],
                            Icon.createWithResource(this, R.drawable.ic_camera))
                }
                2 -> {
                    val launchIntent = Intent(this, TophubActivity::class.java)
                    val mIcon = Icon.createWithResource(ZLibrary.getAppContext(), R.drawable.ic_tophub_today)
                    addPinShortcut("$packageName.TophubActivity", launchIntent, shortcutList[position], mIcon)
                }
                3 -> {
                    val launchIntent = Intent(this, SettingActivity::class.java)
                    addPinShortcut("$packageName.SettingActivity", launchIntent, shortcutList[position], defaultIcon)
                }
                else -> ToastUtil.error("未知错误")
            }
        }
    }

    private fun addPinShortcut(id: String, intent: Intent, shortcutName: String, icon: Icon) {
        if (shortcutsUtil.addPinShortcut(id, intent, shortcutName, icon)) {
            ToastUtil.success("创建快捷方式成功")
        } else {
            ToastUtil.error("创建快捷方式失败")
        }
    }
}
