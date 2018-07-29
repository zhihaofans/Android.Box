package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.wx.android.common.util.ClipboardUtils
import com.wx.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.database.AppDownFeed
import com.zhihaofans.androidbox.gson.GithubReleaseItemAsset
import com.zhihaofans.androidbox.mod.AppDownMod
import com.zhihaofans.androidbox.util.ConvertUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_app_down.*
import kotlinx.android.synthetic.main.content_app_down.*
import kotlinx.android.synthetic.main.content_qrcode.view.*
import kotlinx.android.synthetic.main.content_server_chan.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick

class AppDownActivity : AppCompatActivity() {
    private val appDownSiteParser = AppDownMod.SiteParser()
    private val sysUtil = SystemUtil()
    private val savePath: String = sysUtil.getDownloadPath().path
    private val siteParser = AppDownMod.SiteParser()
    private val convertUtil = ConvertUtil()
    private val dataBase = AppDownMod.DataBase()
    private var appFeeds = mutableListOf<AppDownFeed>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_down)
        setSupportActionBar(toolbar)
        initList()
        fab.setOnClickListener { view ->
            val fabAction = mutableListOf("Add feed", getString(R.string.text_check_update))
            selector(getString(R.string.title_activity_app_down), fabAction) { _: DialogInterface, i: Int ->
                when (i) {
                    0 -> add()
                    1 -> checkAllUpdate()
                }
            }

        }
    }

    private fun initList(): Boolean {
        val listData = mutableListOf<String>()
        appFeeds = dataBase.getAppFeeds()
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
                message = "Version:${clickedApp.version}\nUpdate time:${clickedApp.updateTime}"
                negativeButton(R.string.text_delete) {
                    checkUpdate(index)
                }
                positiveButton(R.string.text_check_update) {
                    del(index)
                }
            }
        }
        return true
    }

    private fun add() {
        val feedSiteList = mutableListOf("Github release")
        var site = ""
        var idOne = "id"
        var idTwo = idOne
        selector("Site", feedSiteList) { _: DialogInterface, i: Int ->
            when (i) {
                0 -> {
                    site = "GITHUB_RELEASES"
                    idOne = "Author"
                    idTwo = "Project"
                }
            }
        }
        if (site.isEmpty()) {
            toast("Unknown site")
        } else {
            alert {
                title = "Add feed"
                customView {
                    verticalLayout {
                        textView(idOne)
                        val inputOne = editText("feilongfl")
                        textView(idTwo)
                        val inputTwo = editText("Cimoc")
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
            }
        }
    }

    private fun addFeed(site: String, idOne: String, idTwo: String? = null) {
        doAsync {
            val appDownList = appDownSiteParser.getApp(site, idOne, idTwo)
            Logger.d("appDownList:${appDownList?.size}")
            uiThread {
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
                            val nameList = mutableListOf<String>()
                            val appDownListNew = mutableListOf<MutableMap<String, Any>>()
                            appDownList.map {
                                val thisName = it["name"] as String?
                                val thisTagName = it["tag_name"] as String?
                                if (thisName.isNullOrEmpty()) {
                                    if (thisTagName.isNullOrEmpty()) {
                                        Logger.d("thisName and thisTagName isNullOrEmpty")
                                    } else {
                                        nameList.add(thisTagName ?: "Null")
                                        appDownListNew.add(it)
                                    }
                                } else {
                                    nameList.add(thisTagName ?: "Null")
                                    appDownListNew.add(it)
                                }
                            }
                            Logger.d("nameList:$nameList")
                            selector(appDownListNew.size.toString() + " update(s)", nameList) { _, i ->
                                val updateItem = appDownListNew[i]
                                val updateName = updateItem["name"] as String
                                val htmlUrl = updateItem["html_url"] as String
                                val description = updateItem["description"] as String
                                val fileList = updateItem["file_list"] as MutableList<*>
                                alert {
                                    title = updateName
                                    message = description
                                    negativeButton("打开网页") {
                                        sysUtil.browse(this@AppDownActivity, htmlUrl)
                                    }
                                    positiveButton(R.string.text_download) {
                                        when (fileList.size) {
                                            0 -> snackbar("No file")
                                            1 -> {
                                                val downFile = fileList[0] as GithubReleaseItemAsset
                                                val fileUrl = downFile.browser_download_url
                                                val fileName = downFile.name
                                                val fileDownloadedCount = downFile.download_count
                                                val fileSize: String = convertUtil.fileSizeInt2string(downFile.size)
                                                alert {
                                                    title = fileName
                                                    message = "Save to $savePath\nSize: $fileSize\nDownload times: $fileDownloadedCount"
                                                    negativeButton("浏览器打开") {
                                                        browse(fileUrl)
                                                    }
                                                    positiveButton(R.string.text_download) {
                                                        downloadFile(fileUrl, fileName)
                                                    }
                                                }.show()
                                            }
                                        }
                                    }
                                }.show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }
        }
    }

    private fun checkAllUpdate() {
        //TODO:check app feeds update

    }

    private fun checkUpdate(index: Int) {
        //TODO:check app feed update

    }

    private fun del(index: Int) {
        //TODO:delete app feed
    }

    private fun snackbar(text: String, longTime: Boolean = false) {
        Snackbar.make(coordinatorLayout_appdown, text, if (longTime) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).show()
    }

    private fun snackbarFun(text: String, longTime: Boolean = false, button: String = "Button", listener: View.OnClickListener) {
        Snackbar.make(coordinatorLayout_appdown, text, if (longTime) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).setAction(button, listener).show()
    }

    private fun downloadFile(url: String, fileName: String) {
        val downloadPath = "$savePath/$fileName"
        val loadingProgressBarDownload: ProgressDialog = progressDialog(message = fileName, title = "Downloading...")
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
