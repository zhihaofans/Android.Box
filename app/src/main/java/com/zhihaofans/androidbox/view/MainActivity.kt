package com.zhihaofans.androidbox.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.FileUtils
import com.wx.android.common.util.SharedPreferencesUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.FirimUpdateGson
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val g = Gson()
    private val sysUtil = SystemUtil()
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main.subtitle = "v" + AppUtils.getVersionName(this@MainActivity)
        setSupportActionBar(toolbar_main)
        SharedPreferencesUtils.init(this@MainActivity)
        //val rxPermissions = RxPermissions(this)
        qrcode.setActivity(this@MainActivity)
        toolbar_main.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_remove_temp_files -> FileUtils.deleteFile(externalCacheDir.absolutePath + "/update/")
            }
            true
        }
        val listData = mutableListOf<String>(
                getString(R.string.text_qrcode),
                getString(R.string.text_androidsdk),
                getString(R.string.text_appmanagement),
                getString(R.string.text_newsbox),
                getString(R.string.text_weather),
                getString(R.string.text_bilibili)
        )
        listView_main.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, listData)
        listView_main.onItemClick { _, _, index, _ ->
            when (index) {
                0 -> {
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
                2 -> startActivity<AppManagementActivity>()
                3 -> startActivity<NewsBoxActivity>()
                4 -> startActivity<WeatherActivity>()
                5 -> startActivity<BilibiliActivity>()
            }
        }
        checkUpdate(this@MainActivity)
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
                                            0 -> sysUtil.chromeCustomTabs(this@MainActivity, result)
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

    private fun checkUpdate(context: Context) {
        //删除上一次更新下载的安装包
        if (AppUtils.getVersionCode(this@MainActivity).toString() == SharedPreferencesUtils.getString("update_version")) {
            val temp_str = SharedPreferencesUtils.getString("download_file_path")
            if (FileUtils.isFileExist(temp_str)) {
                FileUtils.deleteFile(temp_str)
                SharedPreferencesUtils.remove("download_file_path")
                SharedPreferencesUtils.remove("update_version")
            }
        }
        val client = OkHttpClient()
        val url = "http://api.fir.im/apps/latest/com.zhihaofans.androidbox?api_token=d719843e48e9a1dbd46d45390f58c35f"
        request.url(url)
        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Snackbar.make(coordinatorLayout_main, "检测更新失败", Snackbar.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body()
                var responseStr = ""
                if (resBody != null) {
                    responseStr = resBody.string()
                    Logger.d(responseStr)
                    val firimUpdateGson: FirimUpdateGson = g.fromJson(responseStr, FirimUpdateGson::class.java)
                    Logger.d(AppUtils.getVersionCode(this@MainActivity).toString())
                    val onlineVersionCode: Int? = firimUpdateGson.version.toIntOrNull()
                    if (onlineVersionCode is Int) {
                        Logger.d("$onlineVersionCode\nInt")
                    } else {
                        Logger.d(onlineVersionCode)
                    }
                    if (onlineVersionCode == null) {
                        Logger.e(onlineVersionCode)
                    } else if (onlineVersionCode > AppUtils.getVersionCode(this@MainActivity)) {
                        FileUtils.makeDirs(context.externalCacheDir.absolutePath + "/update/")
                        val downloadFilePath = context.externalCacheDir.absolutePath + "/update/" + AppUtils.getPackageName(this@MainActivity) + "_" + firimUpdateGson.versionShort + ".apk"
                        runOnUiThread {
                            Snackbar.make(coordinatorLayout_main, "发现更新，是否要更新？", Snackbar.LENGTH_LONG).setAction("更新", {
                                alert {
                                    title = "${firimUpdateGson.versionShort}(${firimUpdateGson.version})"
                                    message = "更新时间:${sysUtil.time2date(firimUpdateGson.updated_at.toLong() * 1000)}\n更新日志:${firimUpdateGson.changelog}"
                                    positiveButton(R.string.text_update, {
                                        val loading = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                                        loading.setCanceledOnTouchOutside(false)
                                        FileDownloader.getImpl().create(firimUpdateGson.install_url)
                                                .setPath(downloadFilePath)
                                                .setListener(object : FileDownloadListener() {
                                                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                                        Logger.d("FileDownloader\npending\n$firimUpdateGson.install_url\nBytes$soFarBytes/$totalBytes")
                                                    }

                                                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                                                        Logger.d("FileDownloader\nconnected\n$firimUpdateGson.install_url\nBytes:$soFarBytes/$totalBytes")
                                                    }

                                                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                                        Logger.d("FileDownloader\nconnected\n$firimUpdateGson.install_url\nBytes:$soFarBytes/$totalBytes")
                                                    }

                                                    override fun blockComplete(task: BaseDownloadTask?) {
                                                        Logger.d("FileDownloader\nblockComplete\n$firimUpdateGson.install_url")
                                                    }

                                                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                                                        Logger.d("FileDownloader\nretry\n$firimUpdateGson.install_url\nBytes:$soFarBytes\nTimes:$retryingTimes")
                                                    }

                                                    override fun completed(task: BaseDownloadTask) {
                                                        Logger.d("FileDownloader\ncompleted\n$firimUpdateGson.install_url\n$downloadFilePath")
                                                        runOnUiThread {
                                                            loading.dismiss()
                                                            SharedPreferencesUtils.put("update_version", firimUpdateGson.versionShort)
                                                            SharedPreferencesUtils.put("download_file_path", downloadFilePath)
                                                        }
                                                        sysUtil.installApk(this@MainActivity, downloadFilePath)
                                                    }

                                                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                                        Logger.d("FileDownloader\npaused\n$firimUpdateGson.install_url\nBytes:$soFarBytes/$totalBytes")
                                                    }

                                                    override fun error(task: BaseDownloadTask, e: Throwable) {
                                                        Logger.e("FileDownloader\nerror\n$firimUpdateGson.install_url\n${e.message}")
                                                        runOnUiThread {
                                                            loading.dismiss()
                                                            Snackbar.make(coordinatorLayout_main, "更新失败", Snackbar.LENGTH_SHORT).show()
                                                        }
                                                        e.printStackTrace()
                                                    }

                                                    override fun warn(task: BaseDownloadTask) {
                                                        Logger.w("FileDownloader\nwarn\n$firimUpdateGson.install_url\n$downloadFilePath")

                                                    }
                                                }).start()
                                    })
                                    negativeButton("打开网页", {
                                        sysUtil.chromeCustomTabs(this@MainActivity, firimUpdateGson.update_url)
                                    })
                                }.show()
                            }).show()
                        }
                    }
                }
            }
        })
    }

}
