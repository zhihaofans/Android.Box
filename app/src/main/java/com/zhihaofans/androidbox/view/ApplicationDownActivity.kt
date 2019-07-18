package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.ApplicationDownMod
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.snackbar
import io.zhihao.library.android.kotlinEx.string
import kotlinx.android.synthetic.main.activity_application_down.*
import org.jetbrains.anko.selector
import java.io.File


class ApplicationDownActivity : AppCompatActivity() {
    private val xuiUtil = XUIUtil(this)
    private val appDownMod = ApplicationDownMod(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_down)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val menuList = listOf("init")
            selector("", menuList) { _: DialogInterface, i: Int ->
                when (i) {
                    0 -> init()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PRDownloader.cancelAll()
    }

    private fun a(mView: View) {
        xuiUtil.materialDialogInputString("结果", "",
                getString(R.string.text_search), getString(R.string.text_cancel), true)
                .apply {
                    onPositive { mDialog, _ ->
                        searchApp(mDialog.inputEditText!!.string)
                    }
                    onNegative { _, _ ->
                        snackbar(mView, getString(R.string.text_canceled_by_user))
                    }
                }.show()
    }

    private fun init() {
        val filePath = appDownMod.cache.pathList.bucketSourceCacheFilePath
        val fileUrl = appDownMod.urlList.bucketSource
        download(fileUrl, filePath, 0)
    }

    private fun searchApp(searchKey: String) {

    }

    private fun updateBucketSourceCache(data: String) {
        appDownMod.cache.setBucketSource(data)
    }

    private fun updateBucketCache(url: String) {
        appDownMod.cache.setBucketSource(data)
    }

    private fun download(url: String, filePath: String, taskIndex: Int) {
        val mFile = File(filePath)
        val dirPath = mFile.parent
        val fileName = mFile.name
        download(url, dirPath, fileName, taskIndex)
    }

    private fun download(url: String, dirPath: String, fileName: String, taskIndex: Int) {
        val downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener {

                }
                .setOnPauseListener {
                    ToastUtil.warning("Download Pause")
                }
                .setOnCancelListener {
                    ToastUtil.error(R.string.text_canceled_by_user, true)
                }
                .setOnProgressListener {

                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        when (taskIndex) {
                            0 ->
                        }
                    }

                    override fun onError(error: Error) {
                        val errorText = when {
                            error.isConnectionError -> "Connection Error"
                            error.isServerError -> "Server Error"
                            else -> "Unknown Error"
                        }
                        ToastUtil.error("Download Error:$errorText", true)
                    }
                })
    }
}
