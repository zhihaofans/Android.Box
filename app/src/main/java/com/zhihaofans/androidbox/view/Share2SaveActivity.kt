package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import com.orhanobut.logger.Logger
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.hasNotChild
import com.zhihaofans.androidbox.kotlinEx.isActionSend
import com.zhihaofans.androidbox.kotlinEx.remove
import com.zhihaofans.androidbox.kotlinEx.startsWithList
import com.zhihaofans.androidbox.util.*
import dev.utils.app.UriUtils
import dev.utils.app.image.BitmapUtils
import dev.utils.app.toast.ToastTintUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**

 * @author: zhihaofans

 * @date: 2019-02-18 10:26

 */

class Share2SaveActivity : Activity() {
    private val xuiUtil = XUIUtil(this)
    private var loadingDialog: LoadingDialog? = null
    private var mIntent: Intent? = null
    private var mimeType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        mIntent = intent
        try {
            if (mIntent == null) {
                ToastUtil.error("Intent = null")
                finish()
            } else if (mIntent!!.isActionSend) {
                if (mIntent!!.type.isNullOrEmpty()) {
                    ToastUtil.error("不支持分享这个类型的东西")
                    finish()
                } else {
                    mimeType = mIntent!!.type!!
                    when {
                        mimeType == "text/plain" -> {
                            val saveText = mIntent!!.getStringExtra(Intent.EXTRA_TEXT)
                            if (saveText.isNullOrEmpty()) {
                                ToastUtil.error("空白文本，保存失败")
                                finish()
                            } else {
                                loadingDialog = xuiUtil.materialDialogLoadingDialog("保存中", R.drawable.ic_save)
                                val time = DatetimeUtil.unixTimeStampMill().toString()
                                val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share-$time.txt", "text/plain")
                                mimeType = "text"
                                startActivityForResult(saveIntent, 0)
                            }
                        }
                        mimeType.startsWith("image/") -> {
                            val image = mIntent!!.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
                            if (image == null) {
                                ToastUtil.error("空白图片，保存失败")
                                finish()
                            } else {
                                loadingDialog = xuiUtil.materialDialogLoadingDialog("保存中", R.drawable.ic_save)
                                val fileNameExtList = listOf("webp", "png", "jpeg")
                                var fileNameExt = mimeType.remove("image/")
                                if (fileNameExtList.hasNotChild(fileNameExt)) fileNameExt = "png"
                                val time = DatetimeUtil.unixTimeStampMill().toString()
                                val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share-$time.$fileNameExt", mimeType)
                                startActivityForResult(saveIntent, 0)
                            }
                        }
                        else -> {
                            ToastUtil.error("不支持分享这个类型的东西")
                            finish()
                        }
                    }
                }
            } else if (mIntent!!.extras !== null) {
                val mType: String? = mIntent!!.extras!!.getString("type", null)
                when (mType) {
                    null -> {
                        ToastUtil.error("null type")
                        finish()
                    }
                    "text" -> {
                        val saveText: String? = mIntent!!.extras!!.getString("text", null)
                        if (saveText.isNullOrEmpty()) {
                            ToastUtil.error("空白文本，保存失败")
                            finish()
                        } else {
                            loadingDialog = xuiUtil.materialDialogLoadingDialog("保存中", R.drawable.ic_save)
                            val time = DatetimeUtil.unixTimeStampMill().toString()
                            val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share-$time.txt", "text/plain")
                            mimeType = "text"
                            Logger.d(saveIntent)
                            if (IntentUtil.isIntentHasAppToLaunch(saveIntent)) {
                                startActivityForResult(saveIntent, 0)
                            } else {
                                ToastUtil.error("找不到可以调用的文档")
                                finish()
                            }
                        }
                    }
                    "image_link" -> {
                        val image: String? = mIntent!!.extras!!.getString("image_link", null)
                        if (image.isNullOrEmpty()) {
                            ToastUtil.error("空白图片地址，保存失败")
                            finish()
                        } else {
                            loadingDialog = xuiUtil.materialDialogLoadingDialog("保存中", R.drawable.ic_save)
                            val fileNameExtList = listOf("webp", "png", "jpeg")
                            var fileNameExt = mimeType.remove("image/")
                            if (fileNameExtList.hasNotChild(fileNameExt)) fileNameExt = "png"
                            val time = DatetimeUtil.unixTimeStampMill().toString()
                            val saveIntent = IntentUtil.getSaveFileByDocumentIntent("share-$time.$fileNameExt", mimeType)
                            Logger.d(saveIntent)
                            if (IntentUtil.isIntentHasAppToLaunch(saveIntent)) {
                                startActivityForResult(saveIntent, 0)
                            } else {
                                ToastUtil.error("找不到可以调用的文档")
                                finish()
                            }
                        }
                    }
                }
            } else {
                ToastUtil.error("发生未知错误")
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("发生未知错误")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        afterCreateFile(resultData)
                    }
                    else -> {
                        ToastUtil.error("未知代码，即将退出")
                        finish()
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                ToastUtil.warning(R.string.text_canceled_by_user)
                finish()
            }
        }
    }

    private fun afterCreateFile(resultData: Intent?) {
        // Save file
        if (resultData != null) {
            val uri = resultData.data
            when (uri) {
                null -> {
                    ToastTintUtils.error("获取保存路径失败，即将退出")
                    finish()
                }
                else -> {
                    Logger.i("Uri: $uri")
                    val downloadPathList = listOf("content://com.android.providers.downloads.documents/", "content://downloads/")
                    if (uri.toString().startsWithList(downloadPathList)) {
                        ToastTintUtils.error("保存失败，不支持保存到'下载内容'里，请选择具体目录，即将退出")
                        finish()
                    } else {
                        val saveUri: String?
                        try {
                            saveUri = UriUtils.getFilePathByUri(this, uri)
                            if (saveUri.isNullOrEmpty()) {
                                ToastTintUtils.error("真实保存路径空白，即将退出")
                                finish()
                            } else {
                                Logger.i("realUri: $saveUri")
                                doAsync {
                                    when {
                                        mimeType == "text/plain" -> {
                                            val saveText = mIntent!!.getStringExtra(Intent.EXTRA_TEXT)
                                            if (saveText.isNullOrEmpty()) {
                                                ToastTintUtils.error("空白文本，不保存，即将退出")
                                                finish()

                                            } else {
                                                val saveSu = FileUtil.saveFile(saveUri, saveText)
                                                uiThread {
                                                    afterSave(saveSu)
                                                }
                                            }
                                        }
                                        mimeType.startsWith("image/") -> {
                                            val image = mIntent!!.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                                            Logger.d("image:$image")
                                            val inputStream = contentResolver.openInputStream(image)
                                            val bmp = BitmapFactory.decodeStream(inputStream)
                                            inputStream?.close()
                                            //val imagePath = UriUtils.getFilePathByUri(this@Share2SaveActivity, image)
                                            //Logger.d("imagePath:$imagePath")
                                            //val saveSu = FileUtils.copyFile(imagePath, saveUri, true)
                                            val format = when (mIntent!!.type!!.remove("image/")) {
                                                "png" -> Bitmap.CompressFormat.PNG
                                                "jpeg" -> Bitmap.CompressFormat.JPEG
                                                "webp" -> Bitmap.CompressFormat.WEBP
                                                else -> Bitmap.CompressFormat.PNG
                                            }
                                            val saveSu = BitmapUtils.saveBitmapToSDCard(bmp, saveUri, format, 100)
                                            uiThread {
                                                afterSave(saveSu)
                                            }
                                        }
                                        else -> {
                                            ToastUtil.error("发生未知错误")
                                            finish()
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
            ToastUtil.error("未知代码，即将退出")
            finish()
        }
    }

    private fun afterSave(su: Boolean) {
        loadingDialog?.recycle()
        if (su) {
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