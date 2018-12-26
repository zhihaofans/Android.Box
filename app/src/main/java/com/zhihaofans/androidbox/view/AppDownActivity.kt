package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.lxj.xpopup.XPopup
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.AppDownFeed
import com.zhihaofans.androidbox.kotlinEx.logD
import com.zhihaofans.androidbox.kotlinEx.longSnackbar
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.mod.AppDownMod
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_app_down.*
import kotlinx.android.synthetic.main.content_app_down.*
import org.jetbrains.anko.*

class AppDownActivity : AppCompatActivity() {
    private val savePath: String = SystemUtil.getDownloadPathString() + "Android.Box/"
    private var appFeeds = mutableListOf<AppDownFeed>()
    private val dataBase = AppDownMod.DataBase()
    private val siteParser = AppDownMod.SiteParser()
    private val other = AppDownMod.Other()
    private var clipboardUtil: ClipboardUtil? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_down)
        setSupportActionBar(toolbar_appdown)
        siteParser.init(this@AppDownActivity)
        clipboardUtil = ClipboardUtil(this@AppDownActivity)
        initList()
        fab_appdown.setOnClickListener { view ->
            val fabAction = mutableListOf("添加订阅", getString(R.string.text_delete), "数据库操作")
            selector(getString(R.string.title_activity_app_down), fabAction) { _: DialogInterface, i: Int ->
                when (i) {
                    0 -> add()
                    1 -> {
                        initList()
                        selector(getString(R.string.text_delete), dataBase.getAppfeedNameList()) { _: DialogInterface, appIndex: Int ->
                            alert {
                                title = getString(R.string.text_delete) + "?"
                                message = appFeeds[appIndex].name
                                yesButton {
                                    del(appIndex)
                                }
                                noButton { }
                            }.show()
                        }
                    }
                    2 -> {
                        selector("数据库", listOf(getString(R.string.text_import), getString(R.string.text_export))) { _: DialogInterface, ii: Int ->
                            when (ii) {
                                0 -> importDB()
                                1 -> exportDB()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initList() {
        listView_app.adapter = null
        try {
            appFeeds = dataBase.getAppFeeds()
            if (appFeeds.size == 0) {
                Logger.d("appFeeds.size=0")
                snackbar("列表空白")
            } else {
                listView_app.adapter = SystemUtil.listViewAdapter(this@AppDownActivity, dataBase.getAppfeedNameList())
                listView_app.setOnItemClickListener { _, _, index, _ ->
                    val clickedApp = appFeeds[index]
                    alert {
                        title = clickedApp.name
                        message = getString(R.string.text_app_version) + ": ${clickedApp.version}\n" + getString(R.string.text_app_lastupdatetime) + ": ${clickedApp.updateTime}"
                        negativeButton(R.string.text_download) { _ ->
                            selector("", mutableListOf("下载", "浏览器打开")) { _, act: Int ->
                                when (act) {
                                    0 -> {
                                        XXPermissions.with(this@AppDownActivity)
                                                .permission(Permission.Group.STORAGE)
                                                .request(object : OnPermission {
                                                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                                                        if (isAll) {
                                                            val fileList = clickedApp.fileList
                                                            selector(getString(R.string.text_download), fileList.map { it.name }) { _: DialogInterface, fileIndex: Int ->
                                                                val file = fileList[fileIndex]
                                                                val fileExt = ".apk"
                                                                val fileNameList = mutableListOf(
                                                                        clickedApp.name + fileExt,
                                                                        clickedApp.name + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + file.name,
                                                                        clickedApp.site + "_" + clickedApp.name + "_" + clickedApp.version + "_" + file.name,
                                                                        clickedApp.packageName + fileExt,
                                                                        clickedApp.packageName + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.packageName + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.packageName + "_" + clickedApp.version + fileExt,
                                                                        clickedApp.site + "_" + clickedApp.id_one + "_" +
                                                                                (if (clickedApp.id_two == null) "" else clickedApp.id_two + "_") + clickedApp.version + "_" + file.name
                                                                ).map {
                                                                    if (it.endsWith(".apk")) {
                                                                        it
                                                                    } else {
                                                                        "$it.apk"
                                                                    }
                                                                }
                                                                selector("文件名格式", fileNameList) { _, fileNameIndex: Int ->
                                                                    var fileName = fileNameList[fileNameIndex]
                                                                    if (!fileName.endsWith(".apk")) fileName += ".apk"
                                                                    alert {
                                                                        title = getString(R.string.text_download) + "?"
                                                                        customView {
                                                                            verticalLayout {
                                                                                val input = editText(fileName)
                                                                                positiveButton(R.string.text_download) {
                                                                                    val url = file.url
                                                                                    downloadFile(url, input.text.toString())
                                                                                }
                                                                            }
                                                                        }
                                                                    }.show()

                                                                }
                                                            }
                                                        } else {
                                                            snackbar("未授权储存权限，无法下载")
                                                        }
                                                    }

                                                    override fun noPermission(denied: List<String>, quick: Boolean) {
                                                        snackbar("未授权储存权限，无法下载")
                                                    }
                                                }
                                                )
                                    }
                                    1 -> browse(clickedApp.webUrl)
                                }
                            }

                        }
                        positiveButton(R.string.text_check_update) {
                            checkUpdate(index)
                        }
                    }.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("初始化失败，请尝试清空应用数据")
            finish()
        }
    }

    private fun add() {
        val sites = siteParser.getSiteIds()
        val feedSiteList = siteParser.getSiteNames()
        var site: String
        val alertTitle = "Add feed"
        selector("Site", feedSiteList) { _: DialogInterface, i: Int ->
            when (i) {
                0 -> {
                    site = sites[i]
                    alert {
                        title = "Add feed"
                        customView {
                            verticalLayout {
                                textView("Author")
                                val inputOne = editText("zhihaofans")
                                textView("Project")
                                val inputTwo = editText("Android.box")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    val idTwo = inputTwo.text.toString()
                                    if (idOne.isEmpty() || idTwo.isEmpty()) {
                                        toast("请输入内容")
                                    } else {
                                        addFeed(site, idOne, idTwo)
                                    }
                                }
                            }
                        }
                    }.show()
                }
                1 -> {
                    site = sites[i]
                    alert {
                        title = "Add feed"
                        customView {
                            verticalLayout {
                                textView("Package name:")
                                val inputOne = editText(if (SystemUtil.debug(this@AppDownActivity)) "com.linroid.zlive" else "com.zhihaofans.shortcutapp")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    if (idOne.isEmpty()) {
                                        toast("请输入包名")
                                    } else {
                                        addFeed(site, idOne)
                                    }


                                }
                            }
                        }
                    }.show()
                }
                2 -> {
                    site = sites[i]
                    alert {
                        title = alertTitle
                        customView {
                            verticalLayout {
                                textView("Package")
                                val inputOne = editText("com.zhihaofans.androidbox")
                                textView("Api token")
                                val inputTwo = editText("")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    val idTwo = inputTwo.text.toString()
                                    if (idOne.isEmpty() || idTwo.isEmpty()) {
                                        toast("请输入内容")
                                    } else {
                                        addFeed(site, idOne, idTwo)
                                    }
                                }
                            }
                        }
                    }.show()
                }
                3 -> {
                    site = sites[i]
                    alert {
                        title = alertTitle
                        customView {
                            verticalLayout {
                                textView("Package")
                                val inputOne = editText("com.wandoujia.phoenix2")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    if (idOne.isEmpty()) {
                                        toast("请输入内容")
                                    } else {
                                        addFeed(site, idOne)
                                    }
                                }
                            }
                        }
                    }.show()
                }
                4 -> {
                    site = sites[i]
                    alert {
                        title = alertTitle
                        customView {
                            verticalLayout {
                                textView("Package")
                                val inputOne = editText("com.tencent.android.qqdownloader")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    if (idOne.isEmpty()) {
                                        toast("请输入内容")
                                    } else {
                                        addFeed(site, idOne)
                                    }
                                }
                            }
                        }
                    }.show()
                }
                5 -> {
                    site = sites[i]
                    alert {
                        title = alertTitle
                        customView {
                            verticalLayout {
                                textView("Package")
                                val inputOne = editText("fkw1")
                                okButton {
                                    val idOne = inputOne.text.toString()
                                    if (idOne.isEmpty()) {
                                        toast("请输入内容")
                                    } else {
                                        addFeed(site, idOne)
                                    }
                                }
                            }
                        }
                    }.show()
                }
            }

        }
    }

    private fun addFeed(site: String, idOne: String, idTwo: String? = null) {
        val loadingProgressBarAddFeed = indeterminateProgressDialog(message = "$site : " +
                if (idTwo.isNullOrEmpty()) idOne else idTwo, title = "Loading...")
        loadingProgressBarAddFeed.setCancelable(false)
        loadingProgressBarAddFeed.setCanceledOnTouchOutside(false)
        loadingProgressBarAddFeed.show()
        doAsync {
            val appInfoResult = siteParser.getApp(site, idOne, idTwo)
            uiThread { it ->
                loadingProgressBarAddFeed.dismiss()
                if (appInfoResult == null) {
                    snackbarE("错误，返回结果为null，我觉得是代码的问题")
                } else {
                    try {
                        if (!appInfoResult.success) {
                            snackbarE("错误，代码${appInfoResult.code}(${appInfoResult.message})")
                        } else {
                            val appInfo = appInfoResult.result
                            if (appInfo == null) {
                                snackbarE("错误，返回结果为null")
                            } else {
                                var appName = appInfo.appName
                                alert {
                                    title = "请输入名称用于备注，不输入则为默认名称"
                                    message = "$site:$idOne${if (idTwo.isNullOrEmpty()) "" else "/$idTwo"}"
                                    customView {
                                        verticalLayout {
                                            val inputName = editText(appName)
                                            okButton {
                                                appName = if (inputName.text.isEmpty()) appName else inputName.text.toString()
                                                if (dataBase.addFeed(other.appInfo2AppFeed(appName, appInfo))) {
                                                    snackbar("添加成功，刷新订阅列表")
                                                } else {
                                                    snackbar("添加失败，刷新订阅列表")
                                                }
                                                initList()
                                            }
                                        }
                                    }
                                }.show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast("Error")
                    }
                }
            }
        }
    }

    private fun del(index: Int) {
        val deleteFeed = appFeeds[index]
        if (dataBase.delFeed(index, deleteFeed)) {
            snackbar("删除成功，刷新订阅列表")
        } else {
            snackbar("删除失败，刷新订阅列表")
        }
        initList()
    }

    private fun checkUpdate(index: Int) {
        val loadingProgressBarUpdate = indeterminateProgressDialog(appFeeds[index].name, "Checking update...")
        loadingProgressBarUpdate.setCancelable(false)
        loadingProgressBarUpdate.setCanceledOnTouchOutside(false)
        loadingProgressBarUpdate.show()
        var appDownFeed = appFeeds[index]
        doAsync {
            val appInfoResult = siteParser.getApp(appDownFeed.site, appDownFeed.id_one, appDownFeed.id_two)
            uiThread {
                loadingProgressBarUpdate.dismiss()
                if (appInfoResult == null) {
                    snackbarE("检测更新失败，返回结果为null，我觉得是代码的问题")
                } else {
                    if (!appInfoResult.success) {
                        snackbarE("检查更新错误，代码${appInfoResult.code}(${appInfoResult.message})")
                    } else {
                        val appInfo = appInfoResult.result
                        if (appInfo == null) {

                        } else {
                            if (appInfo.version == appDownFeed.version) {
                                if (appInfo.updateTime == appDownFeed.updateTime) {
                                    snackbar("没有变动")
                                } else {
                                    appDownFeed = other.appInfo2AppFeed(appDownFeed.name, appInfo)
                                    snackbar("发现最近一个版本更新时间发生变化")
                                }
                            } else {
                                appDownFeed = other.appInfo2AppFeed(appDownFeed.name, appInfo)
                                snackbar("发现新版本")
                            }
                            appFeeds[index] = appDownFeed
                            dataBase.updateFeedList(appFeeds)
                            initList()
                        }
                    }
                }
            }
        }
    }


    private fun importDB() {
        if (clipboardUtil == null) {
            coordinatorLayout_appdown.snackbar("加载剪切板失败")
        } else {
            val pasteText = clipboardUtil!!.paste()
            alert {
                title = "导入数据库"
                message = if (pasteText.isNullOrEmpty()) "请输入数据库备份文本" else "已自动粘贴剪切板文本"
                customView {
                    verticalLayout {
                        val input = editText(pasteText)
                        input.setSingleLine(true)
                        positiveButton(R.string.text_import) {
                            if (dataBase.importJson(input.text.toString())) {
                                initList()
                                snackbar("导入成功")
                            } else {
                                snackbar("导入失败，请检查备份是否完整")
                            }
                        }
                    }
                }
            }.show()
        }
    }

    private fun exportDB() {
        val db = dataBase.export2json()
        Logger.d("export2json:$db")
        alert {
            title = getString(R.string.text_export)
            customView {
                verticalLayout {
                    val input = editText(db)
                    input.setSingleLine(true)
                }
                positiveButton(R.string.text_copy) {
                    if (clipboardUtil == null) {
                        snackbar("加载剪切板失败")
                    } else {
                        clipboardUtil!!.copy(db)
                        snackbar(R.string.text_copy)
                    }
                }
            }
        }.show()
    }

    private fun snackbar(text: Int, longTime: Boolean = false) {
        if (longTime) {
            coordinatorLayout_appdown.longSnackbar(text)
        } else {
            coordinatorLayout_appdown.snackbar(text)
        }
    }

    private fun snackbar(text: String, longTime: Boolean = false) {
        if (longTime) {
            coordinatorLayout_appdown.longSnackbar(text)
        } else {
            coordinatorLayout_appdown.snackbar(text)
        }
    }

    private fun snackbarE(text: String, longTime: Boolean = false) {
        Logger.e(text)
        snackbar(text, longTime)
    }

    private fun snackbar(text: String, longTime: Boolean = false, button: String = "Button", listener: View.OnClickListener) {
        Snackbar.make(coordinatorLayout_appdown, text, if (longTime) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).setAction(button, listener).show()
    }


    private fun downloadFile(url: String, fileName: String) {
        when {
            url.isEmpty() -> snackbar("下载失败：下载地址空白")
            fileName.isEmpty() -> snackbar("下载失败：文件名空白")
            else -> {
                val filePath = savePath + fileName
                XPopup.get(this).asLoading().dismissOnBackPressed(false).dismissOnTouchOutside(false).show()
                SystemUtil.download(url, filePath, object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        logD("Pending...")
                    }

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                        logD("Connected")

                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                    override fun blockComplete(task: BaseDownloadTask?) {}

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                        logD("Retry,Times: $retryingTimes")
                    }


                    override fun completed(task: BaseDownloadTask) {
                        XPopup.get(this@AppDownActivity).dismiss()
                        alert {
                            title = "下载完成"
                            message = "文件路径:" + task.targetFilePath
                            positiveButton(R.string.text_copy) {
                                if (clipboardUtil == null) {
                                    snackbar("加载剪切板失败")
                                } else {
                                    clipboardUtil!!.copy(task.targetFilePath)
                                    snackbar("复制成功")
                                }
                            }
                            negativeButton(R.string.text_open) {
                            }
                        }.show()

                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        XPopup.get(this@AppDownActivity).dismiss()
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        e.printStackTrace()
                        Logger.d("Download error\nfileName:" + task.filename)
                        XPopup.get(this@AppDownActivity).dismiss()
                        coordinatorLayout_appdown.snackbar("下载失败")
                    }

                    override fun warn(task: BaseDownloadTask) {}
                })
            }
        }
    }
}
