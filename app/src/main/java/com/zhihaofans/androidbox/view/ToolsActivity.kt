package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_tools.*
import kotlinx.android.synthetic.main.content_tools.*

class ToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        setSupportActionBar(toolbar_tools)

        fab_tools.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val tools = listOf(
                "获取当前壁纸"
        )
        listView_tools.init(this, tools)
        listView_tools.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    SystemUtil.getWallpeper(this)
                }
            }
        }
    }

}
