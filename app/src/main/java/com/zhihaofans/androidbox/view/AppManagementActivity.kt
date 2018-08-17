package com.zhihaofans.androidbox.view

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.FileUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.ListViewAdapter
import com.zhihaofans.androidbox.util.ConvertUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_app_management.*
import kotlinx.android.synthetic.main.content_app_management.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.util.*


class AppManagementActivity : AppCompatActivity() {
    private var appList = ArrayList<Map<String, Any>>()
    private val sysUtil = SystemUtil()
    private val convertUtil = ConvertUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)
        setSupportActionBar(toolbar)
        appListInit()
        this@AppManagementActivity.title = getStr(R.string.text_appmanagement)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }


    private fun appListInit(onlyUserApp: Boolean = false) {
        listView_app.visibility = ListView.INVISIBLE
        val loading = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        val pm = packageManager
        appList = ArrayList()
        loading.setCanceledOnTouchOutside(false)
        //loading.setCancelable(false)
        val backG = doAsync {
            //得到PackageManager对象
            val packs = pm.getInstalledPackages(0)
            //得到系统 安装的所有程序包的PackageInfo对象
            val appCount = packs.size
            Logger.d("appList\nlist------>$appCount")
            for (pi in packs) {
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
                //循环读取存到HashMap,再增加到ArrayList.一个HashMap就是一项
            }
            //参数:Context,ArrayList(item的集合),item的layout,包含ArrayList中Hashmap的key的数组,key所对应的值相对应的控件id
            uiThread { it ->
                listView_app.adapter = ListViewAdapter(this@AppManagementActivity, appList, R.layout.piitem, arrayOf("icon", "appName", "packageName"), intArrayOf(R.id.icon, R.id.appName, R.id.packageName))
                loading.hide()
                listView_app.visibility = ListView.VISIBLE
                listView_app.onItemClick { _, _, index, _ ->
                    val childItem = appList[index]
                    val thisPackageInfo: PackageInfo = childItem["packageInfo"] as PackageInfo
                    val thisAppInfo: ApplicationInfo = thisPackageInfo.applicationInfo
                    val thisAppName: String = thisAppInfo.loadLabel(pm).toString()
                    val thisAppIcon: Drawable = thisAppInfo.loadIcon(pm)
                    val thisAppPackageName: String = thisAppInfo.packageName
                    val thisAppVersionName: String = thisPackageInfo.versionName
                    val thisAppVersionCode: Int = thisPackageInfo.versionCode
                    val thisAppFirstInstallTime: String = convertUtil.unixTime2date(thisPackageInfo.firstInstallTime)
                    val thisAppLastUpdateTime: String = convertUtil.unixTime2date(thisPackageInfo.lastUpdateTime)
                    val thisApkPath: String = thisPackageInfo.applicationInfo.sourceDir
                    val thisApkSize: Int = FileUtils.getFileSize(thisApkPath).toInt() //TODO:以后加入自动转换单位功能
                    /*Logger.d(childItem["icon"])
                    alert {
                        customView {
                            verticalLayout {
                                imageView(childItem["icon"] as Drawable)
                                textView(childItem["appName"] as String)
                                textView(childItem["packageName"] as String)
                            }
                        }
                        okButton { }
                    }.show()*/

                    val act_app = listOf(getStr(R.string.text_app_info), getStr(R.string.text_app_apk), getStr(R.string.text_icon))
                    selector(childItem["appName"] as String, act_app) { _, i ->
                        when (i) {
                            0 -> {
                                //app info
                                val list_a = listOf(
                                        thisAppName,
                                        thisAppPackageName,
                                        "$thisAppVersionName ($thisAppVersionCode)",
                                        thisApkPath,
                                        convertUtil.fileSizeInt2string(thisApkSize),
                                        thisAppFirstInstallTime,
                                        thisAppLastUpdateTime
                                )
                                val list_b = mutableListOf(
                                        getStr(R.string.text_app_name),
                                        getStr(R.string.text_app_packagename),
                                        getStr(R.string.text_app_version),
                                        getStr(R.string.text_app_apkpath),
                                        getStr(R.string.text_app_size),
                                        getStr(R.string.text_app_firstinstalltime),
                                        getStr(R.string.text_app_lastupdatetime)
                                )
                                val act_appInfo = mutableListOf<String>()
                                var _a = 0
                                list_b.map {
                                    act_appInfo.add(it + ":" + list_a[_a])
                                    _a++
                                }
                                Logger.d(act_appInfo)
                                //TODO:应用选项
                                selector(getStr(R.string.text_app_info), act_appInfo) { _, ii ->
                                    alert {
                                        customView {
                                            verticalLayout {
                                                val input: String = editText(list_a[ii]).text.toString()
                                                neutralPressed(R.string.text_share) {
                                                    share(input)
                                                }
                                                negativeButton(R.string.text_cancel) {

                                                }
                                                positiveButton(R.string.text_copy) {
                                                    ClipboardUtils.copy(this@AppManagementActivity, input)
                                                    Snackbar.make(coordinatorLayout_app, "ok", Snackbar.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }.show()
                                }
                            }

                            1 -> {
                                //apk file
                                /*
                                            val act_apk = listOf(getStr(R.string.text_app_info), getStr(R.string.text_app_apk), getStr(R.string.text_icon))
                                            selector(childItem["appName"] as String, act_app, { _, ii ->

                                            })
                                */
                                toast("未完成")
                            }

                            2 -> {
                                //app icon
                            }
                        }
                    }
                }

            }

        }
        loading.setOnCancelListener {
            backG.cancel(false)
            loading.hide()
            finish()
        }

    }

    private fun getStr(strCode: Int): String {
        return getString(strCode)
    }


}
