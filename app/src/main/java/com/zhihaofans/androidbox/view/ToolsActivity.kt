package com.zhihaofans.androidbox.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.saveFile
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.kotlinEx.string
import com.zhihaofans.androidbox.mod.UrlMod
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
                    val wallpaper = SystemUtil.getWallpaper(this)
                    val saveTo = UrlMod.APP_PICTURE_DOWNLOAD_PATH
                    coordinatorLayout_tools.snackbar(
                            "保存" + wallpaper.saveFile(saveTo, Bitmap.CompressFormat.PNG).string(
                                    "成功", "失败"
                            )
                    )
                }
            }
        }
    }

}
