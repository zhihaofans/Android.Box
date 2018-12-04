package com.zhihaofans.androidbox.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esotericsoftware.kryo.util.Util.string
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
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
                    XXPermissions.with(this)
                            .permission(Permission.Group.STORAGE)
                            .request(object : OnPermission {
                                override fun hasPermission(granted: List<String>, isAll: Boolean) {
                                    if (isAll) {
                                        val time = SystemUtil.unixTimeStampMill()
                                        val saveTo = UrlMod.APP_PICTURE_DOWNLOAD_PATH + "Wallpaper-$time.png"
                                        val wallpaper = SystemUtil.saveWallpaper(this@ToolsActivity, saveTo)
                                        coordinatorLayout_tools.snackbar("保存" + wallpaper.string("成功($saveTo)", "失败"))
                                    } else {
                                        coordinatorLayout_tools.snackbar("未授权储存权限，无法保存")
                                    }
                                }

                                override fun noPermission(denied: List<String>, quick: Boolean) {
                                    coordinatorLayout_tools.snackbar("未授权储存权限，无法保存")
                                }
                            })

                }
            }
        }
    }

}
