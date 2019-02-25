package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.orhanobut.logger.Logger
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.isActionSend
import com.zhihaofans.androidbox.kotlinEx.startsWithList
import com.zhihaofans.androidbox.util.FileUtil
import com.zhihaofans.androidbox.util.IntentUtil
import com.zhihaofans.androidbox.util.XUIUtil
import dev.utils.app.UriUtils
import dev.utils.app.toast.ToastTintUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


/**

 * @author: zhihaofans

 * @date: 2019-02-18 10:26

 */

class Share2SaveActivity : Activity() {
    private var saveText: String? = null
    private val xuiUtil = XUIUtil(this)
    private var loadingDialog: LoadingDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        val mIntent = intent
        if (mIntent.isActionSend) {
            when (mIntent.type) {
                null -> {
                    xuiUtil
                }
                "text/plain" -> {
                    saveText = mIntent.getStringExtra(Intent.EXTRA_TEXT)
                    if (saveText.isNullOrEmpty()) {
                        toast("空白文本，保存失败")
                        finish()
                    } else {
                        loadingDialog = xuiUtil.materialDialogLoadingDialog("保存中", R.drawable.ic_save)
                        val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share.txt", "text/plain")
                        startActivityForResult(saveIntent, 0)
                    }
                }
                else -> {

                }
            }
        } else {
            toast("只能发送内容给我")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        // Save file
                        if (resultData != null) {
                            val uri = resultData.data
                            when {
                                uri == null -> {
                                    ToastTintUtils.error("获取保存路径失败，即将退出")
                                    finish()
                                }
                                saveText.isNullOrEmpty() -> {
                                    ToastTintUtils.error("空白文本，不保存，即将退出")
                                    finish()
                                }
                                else -> {
                                    Logger.i("Uri: $uri")
                                    val downloadPathList = listOf("content://com.android.providers.downloads.documents/", "content://downloads/")
                                    if (uri.toString().startsWithList(downloadPathList)) {
                                        ToastTintUtils.error("保存失败，不支持保存到'下载内容'里，请选择具体目录，即将退出")
                                        finish()
                                    } else {
                                        var realUri: String? = null
                                        try {
                                            realUri = UriUtils.getFilePathByUri(this, uri)
                                            if (realUri.isNullOrEmpty()) {
                                                ToastTintUtils.error("真实保存路径空白，即将退出")
                                                finish()
                                            } else {
                                                Logger.i("realUri: $realUri")
                                                doAsync {
                                                    val saveSu = FileUtil.saveFile(realUri, saveText!!)
                                                    uiThread {
                                                        loadingDialog?.recycle()
                                                        if (saveSu) {
                                                            xuiUtil.materialDialog("保存成功").apply {
                                                                cancelListener {
                                                                    finish()
                                                                }
                                                                onPositive { _, _ ->
                                                                    finish()
                                                                }
                                                            }.show()
                                                        } else {
                                                            xuiUtil.materialDialog("保存失败").apply {
                                                                cancelListener {
                                                                    finish()
                                                                }
                                                                onPositive { _, _ ->
                                                                    finish()
                                                                }
                                                            }.show()
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Logger.i("realUri: (Exception)")
                                            ToastTintUtils.error("获取真实保存路径失败，即将退出")
                                            finish()
                                        }
                                    }
                                }
                            }
                        } else {
                            toast("未知代码，即将退出")
                            finish()
                        }
                    }
                    else -> {
                        toast("未知代码，即将退出")
                        finish()
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                toast(R.string.text_canceled_by_user)
                finish()
            }
        }
    }

}