package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.wx.android.common.util.ClipboardUtils
import com.wx.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.mod.AppDownMod
import com.zhihaofans.androidbox.util.ConvertUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_app_down.*
import kotlinx.android.synthetic.main.content_app_down.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick

class AppDownActivity : AppCompatActivity() {
    private val appDownSiteParser = AppDownMod.SiteParser()
    private val sysUtil = SystemUtil()
    private val savePath: String = sysUtil.getDownloadPath().path
    private val siteParser = AppDownMod.SiteParser()
    private val convertUtil = ConvertUtil()
    private var appFeeds = mutableListOf<AppDownFeed>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_down)
        setSupportActionBar(toolbar)
        initList()
        fab.setOnClickListener { view ->
            val fabAction = mutableListOf("Add feed", getString(R.string.text_check_update), getString(R.string.text_delete))
            selector(getString(R.string.title_activity_app_down), fabAction) { _: DialogInterface, i: Int ->
                when (i) {
                    0 -> add()
                    1 -> checkAllUpdate()
                    2 -> {
                        val fabAction = mutableListOf("Add feed", getString(R.string.text_check_update), getString(R.string.text_delete))
                        selector(getString(R.string.title_activity_app_down), fabAction) { _: DialogInterface, i: Int ->
                            when (i) {
                                0 -> add()
                                1 -> checkAllUpdate()
                            }
                        }
                    }
                }
            }

        }
    }

    private fun initList(): Boolean {
        listView_app.adapter = null
        val listData = mutableListOf<String>()
        appFeeds = AppDownMod.DataBase().getAppFeeds()
        if (appFeeds.size == 0) {
            Logger.d("appFeeds.size=0")
            return false
        }
        appFeeds.map {
            listData.add(it.name)
        }
        listView_app.adapter = sysUtil.listViewAdapter(this@AppDownActivity, listData)
        listView_app.onItemClick { _, _, index, _ ->
            val clickedApp = appFeeds[index]
            alert {
                title = clickedApp.name
                message = "Version: ${clickedApp.version}\nUpdate time: ${clickedApp.updateTime}"
                negativeButton(R.string.text_delete) {
                    del(index)
                }
                positiveButton(R.string.text_check_update) {
                    checkUpdate(appFeeds[index])
                }
            }.show()
        }
        return true
    }

    private fun add() {
        val feedSiteList = mutableListOf("Github release")
        var site: String
        selector("Site", feedSiteList) { _: DialogInterface, i: Int ->
            when (i) {
                0 -> {
                    site = "GITHUB_RELEASES"
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
                                    when (site) {
                                        "GITHUB_RELEASES" -> {
                                            if (idOne.isEmpty() || idTwo.isEmpty()) {
                                                toast("请输入内容")
                                            } else {
                                                addFeed(site, idOne, idTwo)
                                            }
                                        }
                                        else -> {
                                            toast("Unknown site")
                                        }
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
        val loadingProgressBarAddFeed = indeterminateProgressDialog(message = "$site : " + if (idTwo.isNullOrEmpty()) idOne else idTwo, title = "Loading...")
        loadingProgressBarAddFeed.setCancelable(false)
        loadingProgressBarAddFeed.setCanceledOnTouchOutside(false)
        loadingProgressBarAddFeed.show()
        doAsync {
            val appDownList = appDownSiteParser.getAppUpdates(site, idOne, idTwo)
            Logger.d("appDownList:${appDownList?.size}")
            uiThread {
                loadingProgressBarAddFeed.dismiss()
                try {
                    when {
                        appDownList == null -> {
                            snackbar("appDownList is null")
                            Logger.e("appDownList is null")
                        }
                        appDownList.size == 0 -> {
                            snackbar("appDownList is empty")
                            Logger.e("appDownList is empty")
                        }
                        else -> {
                            var appName = idTwo ?: idOne
                            alert {
                                title = "请输入名称用于备注，不输入则为默认名称"
                                message = "$site:$idOne${if (idTwo.isNullOrEmpty()) "" else "/$idTwo"}\n共有 ${appDownList.size} 个更新。"
                                customView {
                                    verticalLayout {
                                        val inputName = editText(appName)
                                        okButton {
                                            appName = if (inputName.text.isEmpty()) appName else inputName.text.toString()
                                            if (AppDownMod.DataBase().addFeed(appName, appDownList[0])) {
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
                }
            }
        }
    }

    private fun del(index: Int) {
        //TODO:delete app feed
        val deleteFeed = appFeeds[index]
        if (AppDownMod.DataBase().delFeed(index, deleteFeed)) {
            snackbar("删除成功，刷新订阅列表")
        } else {
            snackbar("删除失败，刷新订阅列表")
        }
        initList()
    }

    private fun checkAllUpdate() {
        //TODO:check app feeds update
        appFeeds.map {
            checkUpdate(it)
        }

    }

    private fun checkUpdate(appDownFeed: AppDownFeed) {
        //TODO:check app feed update

    }

    private fun snackbar(text: String, longTime: Boolean = false) {
        Snackbar.make(coordinatorLayout_appdown, text, if (longTime) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).show()
    }

    private fun snackbarFun(text: String, longTime: Boolean = false, button: String = "Button", listener: View.OnClickListener) {
        Snackbar.make(coordinatorLayout_appdown, text, if (longTime) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).setAction(button, listener).show()
    }

    private fun downloadFile(url: String, fileName: String) {
        val downloadPath = "$savePath/$fileName"
        val loadingProgressBarDownload = progressDialog(message = fileName, title = "Downloading...")
        loadingProgressBarDownload.setCancelable(false)
        loadingProgressBarDownload.setCanceledOnTouchOutside(false)
        loadingProgressBarDownload.show()
        sysUtil.download(url, downloadPath, object : FileDownloadListener() {
            override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                loadingProgressBarDownload.setTitle("Pending...")
            }

            override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                loadingProgressBarDownload.setTitle("Connected")

            }

            override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                if (totalBytes > 0) {
                    loadingProgressBarDownload.max = totalBytes
                    loadingProgressBarDownload.progress = soFarBytes
                } else {
                    loadingProgressBarDownload.max = 0
                    loadingProgressBarDownload.progress = 1
                }
            }

            override fun blockComplete(task: BaseDownloadTask?) {}

            override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                loadingProgressBarDownload.setTitle("Retry")
                loadingProgressBarDownload.setMessage("Times: $retryingTimes")
            }

            override fun completed(task: BaseDownloadTask) {
                loadingProgressBarDownload.dismiss()
                alert {
                    title = "下载完成"
                    message = "文件路径:" + task.targetFilePath
                    positiveButton(R.string.text_copy) {
                        ClipboardUtils.copy(this@AppDownActivity, task.targetFilePath)
                        toast("复制成功")
                    }
                }.show()

            }

            override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                loadingProgressBarDownload.dismiss()
            }

            override fun error(task: BaseDownloadTask, e: Throwable) {
                e.printStackTrace()
                com.orhanobut.logger.Logger.d("Download error\nfileName:" + task.filename)
                Snackbar.make(coordinatorLayout_appdown, "下载失败", Snackbar.LENGTH_SHORT).show()
            }

            override fun warn(task: BaseDownloadTask) {}
        })
    }
}
