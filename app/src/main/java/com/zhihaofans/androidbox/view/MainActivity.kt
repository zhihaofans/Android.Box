package com.zhihaofans.androidbox.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.maning.librarycrashmonitor.MCrashMonitor
import com.orhanobut.logger.Logger
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.GlobalSettingMod
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.ConvertUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val convertUtil = ConvertUtil()
    private val globalSetting = GlobalSettingMod()
    private val updateWebUrl = "https://fir.im/fkw1"
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v" + AppUtils.getVersionName(this@MainActivity)
        setSupportActionBar(toolbar_main)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> {
                    val settings = mutableListOf(
                            getString(R.string.text_setting_use_cct_for_web) + ":" +
                                    convertUtil.boolean2string(globalSetting.forceUseChromeCustomTabs(), getString(R.string.text_yes), getString(R.string.text_no)),
                            getString(R.string.text_setting_open_image_url_with_buildin_viewer) + ":" +
                                    convertUtil.boolean2string(globalSetting.imageUrlOpenWithBuiltinViewer(), getString(R.string.text_yes), getString(R.string.text_no)),
                            "Crash page"
                    )
                    selector(getString(R.string.text_setting), settings) { _, i ->
                        when (i) {
                            0 -> {
                                globalSetting.forceUseChromeCustomTabs(!(globalSetting.forceUseChromeCustomTabs()))
                                Snackbar.make(coordinatorLayout_main,
                                        getString(R.string.text_setting_use_cct_for_web) + ":" +
                                                convertUtil.boolean2string(globalSetting.forceUseChromeCustomTabs(), getString(R.string.text_yes), getString(R.string.text_no)),
                                        Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            1 -> {
                                globalSetting.imageUrlOpenWithBuiltinViewer(!(globalSetting.imageUrlOpenWithBuiltinViewer()))
                                Snackbar.make(coordinatorLayout_main,
                                        getString(R.string.text_setting_open_image_url_with_buildin_viewer) + ":" +
                                                convertUtil.boolean2string(globalSetting.imageUrlOpenWithBuiltinViewer(), getString(R.string.text_yes), getString(R.string.text_no)),
                                        Snackbar.LENGTH_SHORT
                                ).show()
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
                getString(R.string.text_androidsdk),
                getString(R.string.text_appmanagement),
                getString(R.string.text_weather),
                getString(R.string.text_bilibili),
                getString(R.string.text_serverchan),
                getString(R.string.title_activity_app_down),
                getString(R.string.text_feed),
                getString(R.string.title_activity_xxdown)
        )
        listView_main.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, listData)
        listView_main.onItemClick { _, _, index, _ ->
            when (index) {
                0 -> startActivity<QrcodeActivity>()
                1 -> {
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
                            "Android 9.0（API 27, Pie 派)"
                    )
                    val nowSdk = Build.VERSION.SDK_INT
                    selector("你是" + if (nowSdk <= sdks.size) sdks[nowSdk - 1] else "UNKNOWN", sdks) { _, i ->
                        val acts = listOf(getString(R.string.text_copy), getString(R.string.text_share))
                        selector(sdks[i], acts) { _, ii ->
                            when (ii) {
                                0 -> {
                                    ClipboardUtils.copy(this@MainActivity, sdks[i])
                                    Snackbar.make(coordinatorLayout_main, R.string.text_finish, Snackbar.LENGTH_SHORT).show()
                                }
                                1 -> share(sdks[i])
                            }
                        }
                    }
                }
                2 -> startActivity<AppManagementActivity>()
                3 -> startActivity<WeatherActivity>()
                4 -> startActivity<BilibiliActivity>()
                5 -> startActivity<ServerChanActivity>()
                6 -> startActivity<AppDownActivity>()
                7 -> startActivity<FeedActivity>()
                8 -> startActivity<XXDownActivity>()
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
                                            1 -> ClipboardUtils.copy(this@MainActivity, result)
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

    fun checkPermissions(manual: Boolean = false) {
        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE, Permission.Group.CAMERA)) {
            if (manual) {
                Snackbar.make(coordinatorLayout_main, "已授权需要的权限，应该可以正常使用", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(coordinatorLayout_main, "发现某个权限未授权，可能影响正常使用", Snackbar.LENGTH_SHORT).setAction("授权") { initPermissions() }.show()
        }
    }

    fun initPermissions() {
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
}
