package com.zhihaofans.androidbox.view

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.copy
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.mod.FavoritesMod
import com.zhihaofans.androidbox.mod.UrlMod
import com.zhihaofans.androidbox.util.*
import dev.utils.app.PhoneUtils
import dev.utils.app.ScreenUtils
import kotlinx.android.synthetic.main.activity_tools.*
import kotlinx.android.synthetic.main.content_tools.*
import org.jetbrains.anko.*


class ToolsActivity : AppCompatActivity() {
    private var saveWallpaperStatus = true
    private val notificationUtil = NotificationUtil()
    private val xuiUtil = XUIUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        setSupportActionBar(toolbar_tools)
        notificationUtil.init(this)
        fab_tools.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val tools = listOf(
                getString(com.zhihaofans.androidbox.R.string.text_androidsdk),
                "获取当前壁纸",
                "收藏夹",
                "修复收藏夹",
                "测试通知",
                getString(com.zhihaofans.androidbox.R.string.text_weather),
                getString(com.zhihaofans.androidbox.R.string.title_activity_xxdown),
                "Waterfall test",
                getString(com.zhihaofans.androidbox.R.string.text_bilibili),
                "快捷方式",
                "随机",
                "号码自动添加前缀"
        )
        listView_tools.init(this, tools)
        listView_tools.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val sdks = listOf(
                            "Android 1.0 (API 1)",
                            "Android 1.1 (API 2, Petit Four 花式小蛋糕)",
                            "Android 1.5 (API 3, Cupcake 纸杯蛋糕)",
                            "Android 1.6 (API 4, Donut 甜甜圈)",
                            "Android 2.0 (API 5, Eclair 松饼)",
                            "Android 2.0.1 (API 6, Eclair 松饼)",
                            "Android 2.1 (API 7, Eclair 松饼)",
                            "Android 2.2.x (API 8, Froyo 冻酸奶)",
                            "Android 2.3-2.3.2 (API 9, Gingerbread 姜饼)",
                            "Android 2.3.3-2.3.7 (API 10, Gingerbread 姜饼)",
                            "Android 3.0 (API 11, Honeycomb 蜂巢)",
                            "Android 3.1 (API 12, Honeycomb 蜂巢)",
                            "Android 3.2.x (API 13, Honeycomb 蜂巢)",
                            "Android 4.0-4.0.2 (API 14, Ice Cream Sandwich 冰激凌三明治)",
                            "Android 4.0.3-4.0.4 (API 15, Ice Cream Sandwich 冰激凌三明治)",
                            "Android 4.1.x (API 16, Jelly Bean  果冻豆)",
                            "Android 4.2.x (API 17, Jelly Bean  果冻豆)",
                            "Android 4.3.x (API 18, Jelly Bean  果冻豆)",
                            "Android 4.4.x (API 19, KitKat 奇巧巧克力棒)",
                            "Android 4.4w.x (API 20, KitKat 奇巧巧克力棒)",
                            "Android 5.0.x (API 21, Lollipop 棒棒糖)",
                            "Android 5.1.x (API 22, Lollipop 棒棒糖)",
                            "Android 6.0.x (API 23, Marshmallow 棉花糖)",
                            "Android 7.0 (API 24, Nougat 牛轧糖)",
                            "Android 7.1.x (API 25, Nougat 牛轧糖)",
                            "Android 8.0 (API 26, Oreo 奥利奥)",
                            "Android 8.1 (API 27, Oreo 奥利奥)",
                            "Android 9.0（API 28, Pie 派)"
                    )
                    val nowSdk = Build.VERSION.SDK_INT
                    selector("你是" + if (nowSdk <= sdks.size) sdks[nowSdk - 1] else "UNKNOWN", sdks) { _, i ->
                        val acts = listOf(getString(com.zhihaofans.androidbox.R.string.text_copy), getString(com.zhihaofans.androidbox.R.string.text_share))
                        selector(sdks[i], acts) { _, ii ->
                            when (ii) {
                                0 -> {
                                    copy(sdks[i])
                                    xuiUtil.snackbar(coordinatorLayout_tools, R.string.text_finish)
                                }
                                1 -> share(sdks[i])
                            }
                        }
                    }
                }
                1 -> saveWallpaper()
                2 -> startActivity<FavoritesActivity>()
                3 -> {
                    try {

                        val favoritesMod = FavoritesMod()
                        if (favoritesMod.deleteDataBase()) {
                            xuiUtil.snackbar(coordinatorLayout_tools, R.string.text_yes)
                        } else {
                            xuiUtil.snackbarDanger(coordinatorLayout_tools, R.string.text_no)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        xuiUtil.snackbarDanger(coordinatorLayout_tools, "Exception")

                    }
                }
                4 -> {
                    try {
                        val noId = notificationUtil.create("test", "测试", true)
                        if (noId !== null) {
                            xuiUtil.snackbar(coordinatorLayout_tools, "成功")
                        } else {
                            xuiUtil.snackbarDanger(coordinatorLayout_tools, "失败!")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        xuiUtil.snackbarDanger(coordinatorLayout_tools, "失败")
                    }
                }
                5 -> startActivity<WeatherActivity>()
                6 -> startActivity<XXDownActivity>()
                7 -> startActivity<ImageWebActivity>()
                8 -> startActivity<BilibiliActivity>()
                9 -> startActivity<ShortcutsActivity>()
                10 -> startActivity<RandomActivity>()
                11 -> number()
            }
        }
    }

    private fun number() {
        val list = listOf(
                "125831"
        )
        BottomSheet.BottomListSheetBuilder(this)
                .apply {
                    list.map {
                        addItem(it)
                    }
                }
                .setOnSheetItemClickListener { dialog, _, position, _ ->
                    dialog.dismiss()
                    xuiUtil.materialDialogInput4Int(
                            "请输入号码", "开头自动加" + list[position], "请输入号码", "",
                            getString(R.string.text_yes), getString(R.string.text_no), true
                    ).onPositive { materialDialog: MaterialDialog, _: DialogAction ->
                        val text = materialDialog.inputEditText!!.text
                        if (text.isNullOrEmpty()) {
                            ToastUtil.error("number is null")
                        } else {
                            BottomSheet.BottomListSheetBuilder(this)
                                    .addItem("短信")
                                    .apply {
                                        if (PhoneUtils.isPhone() && !ScreenUtils.isTablet()) addItem("电话")
                                    }
                                    .setOnSheetItemClickListener { mDialog, _, mPosition, _ ->
                                        mDialog.dismiss()
                                        when (mPosition) {
                                            0 -> try {
                                                if (PhoneUtils.sendSms(list[position] + text, "")) {
                                                    ToastUtil.success("成功")
                                                } else {
                                                    ToastUtil.error("失败")
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                ToastUtil.error("Exception")
                                            }
                                            1 -> try {
                                                if (PhoneUtils.dial(list[position] + text)) {
                                                    ToastUtil.success("成功")
                                                } else {
                                                    ToastUtil.error("失败")
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                ToastUtil.error("Exception")
                                            }
                                        }
                                    }
                                    .build()
                                    .show()
                        }
                    }.onNegative { _: MaterialDialog, _: DialogAction ->
                        xuiUtil.snackbar(coordinatorLayout_tools, getString(R.string.text_canceled_by_user))
                    }.show()
                }
                .build()
                .show()
    }

    private fun saveWallpaper() {
        if (saveWallpaperStatus) {
            saveWallpaperStatus = false
            xuiUtil.snackbar(coordinatorLayout_tools, "正在导出壁纸")
            XXPermissions.with(this)
                    .permission(Permission.Group.STORAGE)
                    .request(object : OnPermission {
                        override fun hasPermission(granted: List<String>, isAll: Boolean) {
                            if (isAll) {
                                val time = DatetimeUtil.unixTimeStampMill()
                                val saveTo = UrlMod.APP_PICTURE_DOWNLOAD_PATH + "Wallpaper-$time.png"
                                doAsync {
                                    try {
                                        val wallpaper = FileUtil.saveWallpaperPng(this@ToolsActivity, saveTo)
                                        uiThread {
                                            if (wallpaper) {
                                                xuiUtil.snackbar(coordinatorLayout_tools, "保存成功($saveTo)")

                                            } else {
                                                xuiUtil.snackbarDanger(coordinatorLayout_tools, "保存失败")
                                            }
                                            saveWallpaperStatus = true
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        uiThread {
                                            saveWallpaperStatus = true
                                            xuiUtil.snackbarDanger(coordinatorLayout_tools, "失败:Exception")
                                        }
                                    }
                                }
                            } else {
                                saveWallpaperStatus = true
                                xuiUtil.snackbarDanger(coordinatorLayout_tools, "未授权储存权限，无法保存")
                            }
                        }

                        override fun noPermission(denied: List<String>, quick: Boolean) {
                            saveWallpaperStatus = true
                            xuiUtil.snackbarDanger(coordinatorLayout_tools, "未授权储存权限，无法保存")
                        }
                    })
        } else {
            xuiUtil.snackbarDanger(coordinatorLayout_tools, "处理中，请不要重复点击")
        }
    }
}
