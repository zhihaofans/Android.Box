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
import com.orhanobut.logger.Logger
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.SharedPreferencesUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.GlobalSettingMod
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val sysUtil = SystemUtil()
    private val globalSetting = GlobalSettingMod()
    private val updateWebUrl = "https://fir.im/fkw1"
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v" + AppUtils.getVersionName(this@MainActivity)
        setSupportActionBar(toolbar_main)
        SharedPreferencesUtils.init(this)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> {
                    val settings = mutableListOf(
                            getString(R.string.text_setting_use_cct_for_web) + ":" +
                                    sysUtil.booleen2string(globalSetting.forceUseChromeCustomTabs(), getString(R.string.text_yes), getString(R.string.text_no)),
                            getString(R.string.text_setting_open_image_url_with_buildin_viewer) + ":" +
                                    sysUtil.booleen2string(globalSetting.imageUrlOpenWithBuiltinViewer(), getString(R.string.text_yes), getString(R.string.text_no))
                    )
                    selector(getString(R.string.text_setting), settings, { _, i ->
                        when (i) {
                            0 -> {
                                globalSetting.forceUseChromeCustomTabs(!(globalSetting.forceUseChromeCustomTabs()))
                                Snackbar.make(coordinatorLayout_main,
                                        getString(R.string.text_setting_use_cct_for_web) + ":" +
                                                sysUtil.booleen2string(globalSetting.forceUseChromeCustomTabs(), getString(R.string.text_yes), getString(R.string.text_no)),
                                        Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            1 -> {

                            }
                        }
                    })
                }
                R.id.menu_manual_update -> {
                    sysUtil.browseWeb(this@MainActivity, updateWebUrl)
                }
            }
            true
        }
        val listData = mutableListOf<String>(
                getString(R.string.text_qrcode),
                getString(R.string.text_androidsdk),
                getString(R.string.text_appmanagement),
                getString(R.string.text_newsbox),
                getString(R.string.text_weather),
                getString(R.string.text_bilibili),
                "ServerChan"
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
                            "Android 8.1 (API 27, Oreo 奥利奥)"
                    )
                    val nowSdk = Build.VERSION.SDK_INT
                    selector("你是${sdks[nowSdk - 1]}", sdks) { _, i ->
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
                3 -> startActivity<NewsBoxActivity>()
                4 -> startActivity<WeatherActivity>()
                5 -> startActivity<BilibiliActivity>()
                6 -> startActivity<ServerChanActivity>()
            }
        }
        //新的更新方式
        buglyInit()
        //指纹验证
        /*
        if (BiometricPromptCompat.isHardwareDetected(this)) {
            Snackbar.make(coordinatorLayout_main, "支持指纹", Snackbar.LENGTH_SHORT).show()
            val biometricPrompt = BiometricPromptCompat.Builder(this)
                    .setTitle("标题")
                    .setSubtitle("副标题")
                    .setDescription("描述：吧啦吧啦吧啦吧啦吧啦……")
                    .setNegativeButton("使用密码") { dialog, which ->
                        toast("你请求了密码解锁。")
                    }
                    .build()
            val cancellationSignal = CancellationSignal()
            cancellationSignal.setOnCancelListener({
                toast("onCancel")
            })
            biometricPrompt.authenticate(cancellationSignal, this)
        } else {
            Snackbar.make(coordinatorLayout_main, "不支持指纹", Snackbar.LENGTH_SHORT).show()
        }
        */
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
                                Snackbar.make(coordinatorLayout_main, result, Snackbar.LENGTH_LONG).setAction(R.string.text_more, {
                                    val acts = mutableListOf<String>(getString(R.string.text_open), getString(R.string.text_copy), getString(R.string.text_share))
                                    selector("", acts, { _, index ->
                                        when (index) {
                                            0 -> sysUtil.browseWeb(this@MainActivity, result)
                                            1 -> ClipboardUtils.copy(this@MainActivity, result)
                                            2 -> share(result)
                                        }
                                    })
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
/*
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        Snackbar.make(coordinatorLayout_main, "指纹验证错误(Code:$errorCode) $errString", Snackbar.LENGTH_LONG).show()
        Logger.e("指纹验证错误(Code:$errorCode) $errString")
    }

    override fun onAuthenticationSucceeded(result: BiometricPromptCompat.IAuthenticationResult) {
        Snackbar.make(coordinatorLayout_main, "指纹验证成功", Snackbar.LENGTH_SHORT).show()
        Logger.d("指纹验证成功")

    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        Snackbar.make(coordinatorLayout_main, "onAuthenticationHelp(Code:$helpCode) $helpString", Snackbar.LENGTH_LONG).show()
        Logger.d("onAuthenticationHelp(Code:$helpCode) $helpString")
    }

    override fun onAuthenticationFailed() {
        Snackbar.make(coordinatorLayout_main, "指纹验证失败", Snackbar.LENGTH_LONG).show()
        Logger.d("onAuthenticationHelp")

    }*/

    private fun buglyInit() {
        Bugly.init(applicationContext, "a71e8c60bc", true) //初始化
        Beta.enableNotification = true //设置在通知栏显示下载进度
        Beta.autoDownloadOnWifi = true //设置Wifi下自动下载
        Beta.enableHotfix = false //关闭热更新能力
    }
}
