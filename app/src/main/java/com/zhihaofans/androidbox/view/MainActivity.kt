package com.zhihaofans.androidbox.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.QrcodeMod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v0.0.1"
        setSupportActionBar(toolbar_main)
        //val rxPermissions = RxPermissions(this)
        qrcode.setActivity(this@MainActivity)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> startActivity<SettingActivity>()
            }
            true
        }

        button_androidsdk.onClick {
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
                    "Android 8.1 (API 27, Oreo 奥利奥)"
            )
            val nowSdk = Build.VERSION.SDK_INT
            selector("你是${sdks[nowSdk - 1]}", sdks, { _, i ->
                val acts = listOf(getString(R.string.text_copy), getString(R.string.text_share))

                selector(sdks[i], acts, { _, ii ->
                    when (ii) {
                        0 -> {
                            ClipboardUtils.copy(this@MainActivity, sdks[i])
                            Snackbar.make(coordinatorLayout_main, R.string.text_finish, Snackbar.LENGTH_SHORT).show()
                        }
                        1 -> share(sdks[i])
                    }
                })
            })
        }

        button_qrcode.onClick {
            val qrcodePlugin = qrcode.getInstalledPlugin(this@MainActivity)
            Logger.d("Qrcode Plugin:$qrcodePlugin")
            if (qrcodePlugin < 1) {
                Snackbar.make(coordinatorLayout_main, R.string.text_no_install_need_plugin, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.text_install, {
                            val countries = listOf("二维码扫描(mark.qrcode)", "H5扫码器(org.noear.scan.H5_SCAN)")
                            selector("", countries, { _, i ->
                                when (i) {
                                    0 -> browse("https://www.coolapk.com/apk/mark.qrcode")
                                    1 -> browse("https://www.coolapk.com/apk/org.noear.scan")
                                }
                            })
                        }).show()
            } else {
                try {
                    qrcode.scan(qrcodePlugin)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(coordinatorLayout_main, R.string.text_use_qrplugin_fail, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        button_appmanagement.onClick {
            startActivity<AppManagementActivity>()
        }

        button_newsbox.onClick {
            startActivity<NewsBoxActivity>()
        }
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
                                Snackbar.make(coordinatorLayout_main, result, Snackbar.LENGTH_LONG).setAction(R.string.text_share, {
                                    share(result)
                                }).show()
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

}
