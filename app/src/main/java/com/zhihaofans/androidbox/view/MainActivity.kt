package com.zhihaofans.androidbox.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wx.android.common.util.FileUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.QrcodeMod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v0.0.1"
        setSupportActionBar(toolbar_main)
        val rxPermissions = RxPermissions(this)
        qrcode.setActivity(this@MainActivity)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> startActivity<SettingActivity>()
            }
            true
        }
        button_dotnomedia.onClick {
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe({ granted ->
                        if (granted) {
                            alert {
                                customView {
                                    verticalLayout {
                                        textView("路径:")
                                        var path = editText {
                                            setText("${getExternalStorageDirectory().absolutePath}/")
                                        }.text.toString()
                                        yesButton {
                                            if (path.isNotEmpty()) {
                                                if (!path.endsWith("/")) path += "/"
                                                if (FileUtils.isFileExist(path)) {
                                                    Snackbar.make(coordinatorLayout_main, "该路径是个文件不是文件夹，生成文件失败",
                                                            Snackbar.LENGTH_SHORT).show()
                                                } else {
                                                    if (FileUtils.isFolderExist(path)) {
                                                        val fileName = path + ".nomedia"
                                                        if (FileUtils.isFileExist(fileName)) {
                                                            Snackbar.make(coordinatorLayout_main, "文件已存在，不需要生成",
                                                                    Snackbar.LENGTH_SHORT).show()
                                                        } else {
                                                            if (FileUtils.writeFile(fileName, "")) {
                                                                Snackbar.make(coordinatorLayout_main, "生成文件成功",
                                                                        Snackbar.LENGTH_SHORT).show()
                                                            } else {
                                                                Snackbar.make(coordinatorLayout_main, "生成文件失败",
                                                                        Snackbar.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    } else {
                                                        Snackbar.make(coordinatorLayout_main, "该文件夹不存在，要先创建吗？",
                                                                Snackbar.LENGTH_LONG)
                                                                .setAction("创建并生成文件", {

                                                                }).show()
                                                    }
                                                }
                                            }
                                        }
                                        noButton { }
                                    }
                                }
                            }.show()
                        } else {
                            Snackbar.make(coordinatorLayout_main, "生成文件失败，没有储存权限。", Snackbar.LENGTH_SHORT)
                                    .setAction("授权", {
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        intent.data = Uri.fromParts("package", packageName, null)
                                        startActivity(intent)
                                    })
                                    .show()
                        }
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
                    snackbar("调用二维码插件失败")
                }
            }
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

    // 自定义扩展函数
    private fun snackbar(message: CharSequence, showLongTime: Boolean = false) {
        var showTime = Snackbar.LENGTH_SHORT
        if (showLongTime) showTime = Snackbar.LENGTH_LONG
        Snackbar.make(coordinatorLayout_main, message, showTime).show()
    }

    private fun snackbar(message: Int, showLongTime: Boolean = false) {
        snackbar(getString(message), showLongTime)
    }
}
