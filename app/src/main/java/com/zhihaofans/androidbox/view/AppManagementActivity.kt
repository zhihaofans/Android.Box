package com.zhihaofans.androidbox.view

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_app_management.*
import kotlinx.android.synthetic.main.content_app_management.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.util.*


class AppManagementActivity : AppCompatActivity() {
    private var appList = ArrayList<Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)
        setSupportActionBar(toolbar)
        appListInit()
        this@AppManagementActivity.title = getString(R.string.text_appmanagement)
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
                map["applicationInfo"] = pi.applicationInfo

                //包名
                if (pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    appList.add(map)//如果非系统应用，则添加至appList
                } else {
                    if (!onlyUserApp) {
                        appList.add(map)//如果非系统应用，则添加至appList
                    }
                }
                //循环读取存到HashMap,再增加到ArrayList.一个HashMap就是一项
            }
            //参数:Context,ArrayList(item的集合),item的layout,包含ArrayList中Hashmap的key的数组,key所对应的值相对应的控件id
            uiThread {
                listView_app.adapter = ListViewAdapter(this@AppManagementActivity, appList, R.layout.piitem, arrayOf("icon", "appName", "packageName"), intArrayOf(R.id.icon, R.id.appName, R.id.packageName))
                loading.hide()
                listView_app.visibility = ListView.VISIBLE
                listView_app.onItemClick { _, _, index, _ ->
                    val childItem = appList[index]
                    Logger.d(childItem["icon"])
                    alert {
                        customView {
                            verticalLayout {
                                imageView(childItem["icon"] as Drawable)
                                textView(childItem["appName"] as String)
                                textView(childItem["packageName"] as String)
                            }
                        }
                        okButton { }
                    }.show()
                    val acts = listOf(getString(R.string.text_app_info), getString(R.string.text_app_apk))
                    selector(childItem["appName"] as String, acts, { _, ii ->
                        when (ii) {
                            0 -> {
                                ClipboardUtils.copy(this@MainActivity, sdks[i])
                                Snackbar.make(coordinatorLayout_main, R.string.text_finish, Snackbar.LENGTH_SHORT).show()
                            }
                            1 -> share(sdks[i])
                        }
                    })
                }

            }

        }
        loading.setOnCancelListener {
            backG.cancel(false)
            loading.hide()
            finish()
        }

    }

}
