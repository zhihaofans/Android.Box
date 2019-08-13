package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.ListViewAdapter
import com.zhihaofans.androidbox.util.LogUtil
import com.zhihaofans.androidbox.util.ToastUtil
import dev.utils.app.image.ImageUtils
import dev.utils.common.FileUtils
import io.zhihao.library.android.kotlinEx.getAppName
import io.zhihao.library.android.kotlinEx.snackbar
import io.zhihao.library.android.util.*
import kotlinx.android.synthetic.main.activity_app_management.*
import kotlinx.android.synthetic.main.content_app_management.*
import org.jetbrains.anko.*
import java.util.*


class AppManagementActivity : AppCompatActivity() {
    private var appList = mutableListOf<Map<String, Any>>()

    private var uninstallAppPackageName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)
        setSupportActionBar(toolbar_app)
        appListInit()
        this@AppManagementActivity.title = getString(R.string.text_appmanagement)
    }


    private fun appListInit(onlyUserApp: Boolean = false) {
        listView_app_management.visibility = ListView.INVISIBLE
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "加载中...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        val pm = packageManager
        //得到PackageManager对象
        appList = mutableListOf()
        doAsync {
            // 排序
            val packs = pm.getInstalledPackages(0).sortedBy {
                it.getAppName()
            }
            //得到系统 安装的所有程序包的PackageInfo对象
            LogUtil.d("appList\nlist------>${packs.size}")
            packs.map { pi ->
                val map = hashMapOf<String, Any>()
                map["icon"] = pi.applicationInfo.loadIcon(pm)
                //图标
                map["appName"] = pi.applicationInfo.loadLabel(pm)
                //应用名
                map["packageName"] = pi.packageName
                map["packageInfo"] = pi
                //包名
                if (pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    appList.add(map)//如果非系统应用，则添加至appList
                } else if (!onlyUserApp) {
                    appList.add(map)//如果系统应用，当 onlyUserApp==false 时添加至appList

                }
                pi
                //循环读取存到HashMap,再增加到ArrayList.一个HashMap就是一项
            }
            //参数:Context,ArrayList(item的集合),item的layout,包含ArrayList中Hashmap的key的数组,key所对应的值相对应的控件id
            uiThread {
                listView_app_management.adapter = ListViewAdapter(this@AppManagementActivity, appList, R.layout.piitem, arrayOf("icon", "appName", "packageName"), intArrayOf(R.id.icon, R.id.appName, R.id.packageName))
                loadingProgressBar.dismiss()
                listView_app_management.visibility = ListView.VISIBLE
                listView_app_management.setOnItemClickListener { _, _, index, _ ->
                    val childItem = appList[index]
                    val thisPackageInfo: PackageInfo = childItem["packageInfo"] as PackageInfo
                    val thisAppInfo: ApplicationInfo = thisPackageInfo.applicationInfo
                    val thisAppName: String = thisAppInfo.loadLabel(pm).toString()
                    val thisAppIcon: Drawable = thisAppInfo.loadIcon(pm)
                    val thisAppPackageName: String = thisAppInfo.packageName
                    val thisAppVersionName: String = thisPackageInfo.versionName
                    val thisAppVersionCode: Int = AppUtil.getAppVersionCode(thisAppInfo.packageName)
                            ?: -1
                    val thisAppFirstInstallTime: String = DatetimeUtil.unixTime2date(thisPackageInfo.firstInstallTime, Locale.CHINA)
                    val thisAppLastUpdateTime: String = DatetimeUtil.unixTime2date(thisPackageInfo.lastUpdateTime, Locale.CHINA)
                    val thisApkPath: String = thisPackageInfo.applicationInfo.sourceDir
                    val thisApkSize = FileUtil.getFileSize(thisApkPath)
                    val actApp = listOf(getString(R.string.text_app_info), getString(R.string.text_app_apk), getString(R.string.text_icon), getString(R.string.text_app_uninstall))
                    selector(childItem["appName"] as String, actApp) { _, i ->
                        when (i) {
                            0 -> {
                                //app info
                                val listA = listOf(
                                        thisAppName,
                                        thisAppPackageName,
                                        "$thisAppVersionName ($thisAppVersionCode)",
                                        thisApkPath,
                                        FileUtil.fileSizeLong2string(thisApkSize),
                                        thisAppFirstInstallTime,
                                        thisAppLastUpdateTime
                                )
                                val listB = mutableListOf(
                                        getString(R.string.text_app_name),
                                        getString(R.string.text_app_packagename),
                                        getString(R.string.text_app_version),
                                        getString(R.string.text_app_apkpath),
                                        getString(R.string.text_app_size),
                                        getString(R.string.text_app_firstinstalltime),
                                        getString(R.string.text_app_lastupdatetime)
                                )
                                val actAppInfo = mutableListOf<String>()
                                var indexA = 0
                                listB.map { item ->
                                    actAppInfo.add("$item:" + listA[indexA])
                                    indexA++
                                }
                                LogUtil.d(actAppInfo)
                                selector(getString(R.string.text_app_info), actAppInfo) { _, ii ->
                                    alert {
                                        customView {
                                            verticalLayout {
                                                val input: String = editText(listA[ii]).text.toString()
                                                neutralPressed(R.string.text_share) {
                                                    share(input)
                                                }
                                                negativeButton(R.string.text_cancel) {

                                                }
                                                positiveButton(R.string.text_copy) {
                                                    ClipboardUtil.copy(input)
                                                    coordinatorLayout_app.snackbar("ok")

                                                }
                                            }
                                        }
                                    }.show()
                                }
                            }

                            1 -> {
                                val apkPath = AppUtil.getAppPath(thisAppPackageName)
                                val apkLength = if (apkPath.isNullOrEmpty()) "null" else FileUtil.fileSizeLong2string(FileUtil.getFileSize(apkPath))
                                val saveTo = FileUtil.getDownloadPathString() + "Android.Box/"
                                val savePath = "$saveTo$thisAppName-$thisAppPackageName-$thisAppVersionName.apk"
                                alert {
                                    title = "是否导出安装包"
                                    message = "应用名称：$thisAppName\n包名：$thisAppPackageName\n版本：$thisAppVersionName ($thisAppVersionCode)\n安装包大小：$apkLength\n保存至：$saveTo"
                                    noButton { coordinatorLayout_app.snackbar("取消导出") }
                                    yesButton {
                                        doAsync {
                                            val exportSu = FileUtils.copyFile(apkPath, savePath, true)
                                            runOnUiThread {
                                                if (exportSu) {
                                                    coordinatorLayout_app.snackbar("OK:$savePath")
                                                } else {
                                                    coordinatorLayout_app.snackbar("no")
                                                }
                                            }
                                        }
                                    }
                                }.show()
                            }

                            2 -> {
                                val saveTo = FileUtil.getDownloadPathString() + "Android.Box/"
                                val savePath = "$saveTo$thisAppName-$thisAppPackageName-icon.png"
                                alert {
                                    title = "是否导出应用图标"
                                    message = "应用名称：$thisAppName\n包名：$thisAppPackageName\n版本：$thisAppVersionName ($thisAppVersionCode)\n保存至：$saveTo"
                                    noButton { coordinatorLayout_app.snackbar("取消导出") }
                                    yesButton {
                                        doAsync {
                                            val exportSu = ImageUtils.save(thisAppIcon.toBitmap(), savePath, Bitmap.CompressFormat.PNG)
                                            runOnUiThread {
                                                if (exportSu) {
                                                    coordinatorLayout_app.snackbar("OK:$savePath")
                                                } else {
                                                    coordinatorLayout_app.snackbar("no")
                                                }
                                            }
                                        }
                                    }
                                }.show()
                            }
                            3 -> {
                                val uninstallAppIntent = IntentUtil.getUninstallAppIntent(thisAppPackageName, true)
                                when {
                                    !AppUtil.isAppInstalled(thisAppPackageName) -> ToastUtil.error("未安装")
                                    AppUtil.isSystemApp(thisAppPackageName) -> ToastUtil.error("不支持卸载系统应用")
                                    uninstallAppIntent == null || !IntentUtil.isIntentAvailable(uninstallAppIntent) -> ToastUtil.error("跳转卸载失败")
                                    else -> {
                                        uninstallAppPackageName = thisAppPackageName
                                        try {
                                            startActivityForResult(uninstallAppIntent, 0)
                                            ToastUtil.info("正在卸载")
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            ToastUtil.error("发生异常，跳转卸载失败")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        if (uninstallAppPackageName.isNullOrEmpty()) {
                            ToastUtil.error("错误，欲卸载应用包名为空白")
                        } else {
                            if (AppUtil.isAppInstalled(uninstallAppPackageName!!)) {
                                ToastUtil.error("错误，应用未卸载")
                            } else {
                                ToastUtil.success("应用已卸载")
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> ToastUtil.warning(R.string.text_canceled_by_user)
        }
        uninstallAppPackageName = null
    }

}
