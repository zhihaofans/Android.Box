package com.zhihaofans.androidbox.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.maning.librarycrashmonitor.MCrashMonitor
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.kotlinEx.string
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.mod.ZhihaofansMod
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.NotificationUtil
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val appSettingMod = AppSettingMod()
    private var clipboardUtil: ClipboardUtil? = null
    private val updateWebUrl = "https://fir.im/fkw1"
    private val zhihaofansMod = ZhihaofansMod()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v" + AppUtils.getAppVersionName()
        setSupportActionBar(toolbar_main)
        init()
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> {
                    val settings = mutableListOf(
                            getString(R.string.text_buildin_web_browser) + ":" +
                                    appSettingMod.buildinX5Browser.string(getString(R.string.text_yes), getString(R.string.text_no)),
                            getString(R.string.text_setting_open_image_url_with_buildin_viewer) + ":" +
                                    appSettingMod.imageUrlOpenWithBuiltinViewer.string(getString(R.string.text_yes), getString(R.string.text_no)),
                            "Crash page"
                    )
                    selector(getString(R.string.text_setting), settings) { _, i ->
                        when (i) {
                            0 -> {
                                coordinatorLayout_main.snackbar(
                                        if (appSettingMod.buildinX5Browser(!appSettingMod.buildinX5Browser)) {
                                            "ok"
                                        } else {
                                            "no"
                                        }
                                                + ":" + getString(R.string.text_buildin_web_browser) + ":" +
                                                appSettingMod.buildinX5Browser.string(getString(R.string.text_yes),
                                                        getString(R.string.text_no))
                                )
                            }
                            1 -> {
                                coordinatorLayout_main.snackbar(
                                        if (appSettingMod.imageUrlOpenWithBuiltinViewer(!appSettingMod.imageUrlOpenWithBuiltinViewer)) {
                                            "ok"
                                        } else {
                                            "no"
                                        }
                                                + ":" + getString(R.string.text_setting_open_image_url_with_buildin_viewer) + ":" +
                                                appSettingMod.imageUrlOpenWithBuiltinViewer.string(getString(R.string.text_yes),
                                                        getString(R.string.text_no))
                                )
                            }
                            2 -> MCrashMonitor.startCrashListPage(this)
                        }
                    }
                }
                R.id.menu_manual_update -> {
                    SystemUtil.browse(this@MainActivity, updateWebUrl)
                }
                R.id.menu_checkPermission -> {
                    checkPermissions(true)
                }
            }
            true
        }
        val listData = listOf(
                getString(R.string.text_qrcode),
                getString(R.string.text_appmanagement),
                getString(R.string.text_serverchan),
                getString(R.string.title_activity_app_down),
                getString(R.string.text_feed),
                "更多工具"
        )
        listView_main.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, listData)
        listView_main.setOnItemClickListener { _, _, index, _ ->
            when (index) {
                0 -> startActivity<QrcodeActivity>()
                1 -> startActivity<AppManagementActivity>()
                2 -> startActivity<ServerChanActivity>()
                3 -> startActivity<AppDownActivity>()
                4 -> startActivity<FeedActivity>()
                5 -> startActivity<ToolsActivity>()
            }
        }
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) { // 0:Qrcode
                    0 -> {
                        if (qrcode.isInstallQrPlugin && data != null) {
                            Logger.d(data.extras)
                            if (data.hasExtra("data")) {
                                val result: String = data.getStringExtra("data")
                                Logger.d(result)
                                Snackbar.make(coordinatorLayout_main, result, Snackbar.LENGTH_LONG).setAction(R.string.text_more) {
                                    val acts = mutableListOf<String>(getString(R.string.text_open), getString(R.string.text_copy), getString(R.string.text_share))
                                    selector("", acts) { _, index ->
                                        when (index) {
                                            0 -> SystemUtil.browse(this@MainActivity, result)
                                            1 -> clipboardUtil?.copy(result)
                                            2 -> share(result)
                                        }
                                    }
                                }.show()
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> Snackbar.make(coordinatorLayout_main, R.string.text_canceled_by_user, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun checkPermissions(manual: Boolean = false) {
        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE, Permission.Group.CAMERA)) {
            if (manual) {
                Snackbar.make(coordinatorLayout_main, "已授权需要的权限，应该可以正常使用", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(coordinatorLayout_main, "发现某个权限未授权，可能影响正常使用", Snackbar.LENGTH_SHORT).setAction("授权") { initPermissions() }.show()
        }
    }

    private fun init() {
        clipboardUtil = ClipboardUtil(this)
        appSettingMod.init(this)
        zhihaofansMod.init(this)
        //checkUpdate()
    }

    private fun initPermissions() {
        XXPermissions.with(this)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.REQUEST_INSTALL_PACKAGES, Permission.SYSTEM_ALERT_WINDOW) //支持请求安装权限和悬浮窗权限
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA) //支持多个权限组进行请求，不指定则默以清单文件中的危险权限进行请求
                .request(object : OnPermission {
                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                        var t = "${granted.size}个权限通过授权"
                        if (!isAll) t += "，可能影响正常使用"
                        Snackbar.make(coordinatorLayout_main, t, Snackbar.LENGTH_SHORT).show()
                    }

                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        Snackbar.make(coordinatorLayout_main, "${denied.size}个权限未授权，可能影响正常使用", Snackbar.LENGTH_SHORT).setAction("授权") { initPermissions() }.show()
                    }
                })
    }

    private fun checkUpdate() {
        doAsync {
            val update = zhihaofansMod.Update()
            uiThread {
                when (update.checkUpdate()) {
                    true -> {
                        val updateData = update.getUpdateData()
                        if (updateData == null) {
                            coordinatorLayout_main.snackbar("检测更新失败")
                        } else {
                            coordinatorLayout_main.snackbar("发现更新")
                            val updateSiteList = mutableListOf<String>()
                            val webUrlList = mutableListOf<String>()
                            updateData.web_url.map { url ->
                                val u = SystemUtil.checkUrl(url)
                                if (u != null) {
                                    updateSiteList.add(u.host)
                                    webUrlList.add(url)
                                }
                            }
                            if (webUrlList.size == 0) {
                                coordinatorLayout_main.snackbar("错误：有0个有效更新站点")
                            } else {
                                selector("选择更新站点", updateSiteList) { _, i ->
                                    browse(webUrlList[i])
                                }
                            }
                        }
                    }
                    false -> coordinatorLayout_main.snackbar("没有更新")
                    else -> coordinatorLayout_main.snackbar("检测更新失败")
                }
            }
        }
    }
}
