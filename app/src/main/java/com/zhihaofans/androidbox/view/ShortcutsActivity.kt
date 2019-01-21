package com.zhihaofans.androidbox.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import dev.utils.app.ShortCutUtils
import kotlinx.android.synthetic.main.activity_shortcuts.*
import kotlinx.android.synthetic.main.content_shortcuts.*
import org.jetbrains.anko.toast

class ShortcutsActivity : AppCompatActivity() {

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
        val sm = getSystemService(Context.SHORTCUT_SERVICE)
        val shortcutList = listOf(
                "订阅"
        )
        listViewShortcuts.init(this, shortcutList)
        listViewShortcuts.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val feedClassName = "$packageName.FeedActivity"
                    try {
                        ShortCutUtils.addShortcut(this, feedClassName, feedClassName, R.mipmap.ic_launcher)
                        toast("创建快捷方式成功")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast("创建快捷方式失败")
                    }
                }
                else -> toast("未知错误")
            }
        }
    }
}
