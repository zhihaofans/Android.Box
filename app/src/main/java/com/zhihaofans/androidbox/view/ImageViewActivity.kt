package com.zhihaofans.androidbox.view

import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.FileUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.snackbar
import dev.utils.app.ContentResolverUtils
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.content_image_view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import java.io.File


class ImageViewActivity : AppCompatActivity() {
    private var imageUrl: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        setSupportActionBar(toolbar)
        try {
            imageUrl = intent.extras.getString("image", null)
            val imageTitle = intent.extras.getString("title", null)
            if (imageUrl == null) {
                toast("null image")
                finish()
            } else {
                if (!(imageTitle.isNullOrEmpty())) {
                    this@ImageViewActivity.title = imageTitle
                }
                initImage(imageUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Try to get image uri fail.")
            finish()
        }
    }

    private fun initImage(imageUri: String?) {
        if (imageUri.isNullOrEmpty()) {
            toast("Empty image")
            finish()
        } else {
            val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
            loadingProgressBar.setCancelable(false)
            loadingProgressBar.setCanceledOnTouchOutside(false)
            loadingProgressBar.show()
            val uri = Uri.parse(imageUri)
            val builder = GenericDraweeHierarchyBuilder(resources)
            val hierarchy = builder
                    .setProgressBarImage(ProgressBarDrawable())
                    .build()
            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build()
            val controllerListener: ControllerListener<ImageInfo> = object : BaseControllerListener<ImageInfo>() {
                override fun onIntermediateImageSet(id: String, imageInfo: ImageInfo?) {
                    Logger.d("Intermediate image received")
                }

                override fun onFailure(id: String, throwable: Throwable) {
                    throwable.printStackTrace()
                    toast("Error:" + throwable.message.toString())
                    loadingProgressBar.dismiss()
                }

                override fun onFinalImageSet(id: String, imageInfo: ImageInfo?, anim: Animatable?) {
                    if (imageInfo == null) {
                        return
                    }
                    val qualityInfo = imageInfo.qualityInfo
                    Logger.d("Final image received! " + "Size ${imageInfo.width} x ${imageInfo.height}\n" +
                            "Quality level ${qualityInfo.quality}, good enough: ${qualityInfo.isOfGoodEnoughQuality}, full quality: ${qualityInfo.isOfFullQuality}\n"
                    )

                    val layoutParams = imageView.layoutParams
                    layoutParams.width = linearLayout_imageview.width
                    layoutParams.height = (linearLayout_imageview.width.toFloat() / (imageInfo.width.toFloat() / imageInfo.height.toFloat())).toInt()
                    imageView.layoutParams = layoutParams
                    loadingProgressBar.dismiss()
                    imageView.onLongClick { it ->
                        if (!(imageUrl.isNullOrEmpty())) {
                            val selectorItemList = mutableListOf(
                                    "下载",
                                    "浏览器打开",
                                    "复制图片地址"
                            )
                            selector("", selectorItemList) { _, i ->
                                when (i) {
                                    0 -> {
                                        XXPermissions.with(this@ImageViewActivity)
                                                .permission(Permission.Group.STORAGE)
                                                .request(object : OnPermission {
                                                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                                                        if (isAll) {
                                                            val fileName = FileUtils.getFileName(imageUrl)
                                                            download(fileName, 0)
                                                        } else {
                                                            com.google.android.material.snackbar.Snackbar.make(coordinatorLayout_imageView, "需要储存权限", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).setAction("授权") {
                                                                XXPermissions.gotoPermissionSettings(this@ImageViewActivity, true)
                                                            }.show()
                                                        }
                                                    }

                                                    override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                                                        com.google.android.material.snackbar.Snackbar.make(coordinatorLayout_imageView, "需要储存权限", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).setAction("授权") {
                                                            XXPermissions.gotoPermissionSettings(this@ImageViewActivity, true)
                                                        }.show()
                                                    }
                                                })
                                    }
                                    1 -> browse(imageUrl.toString())
                                    2 -> {
                                        ClipboardUtils.copy(this@ImageViewActivity, imageUrl)
                                        com.google.android.material.snackbar.Snackbar.make(coordinatorLayout_imageView, "ok", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setControllerListener(controllerListener)
                    .setOldController(imageView.controller)
                    .setUri(uri)
                    .build()
            imageView.hierarchy = hierarchy
            imageView.controller = controller
        }
    }

    private fun download(fileName: String, engine: Int) {
        val downloadPath: String = SystemUtil.getPicturePathString() + "/Android.Box/$fileName"
        Logger.d("downloadPath:$downloadPath")
        val loadingProgressBarDownload = progressDialog(message = fileName, title = "Downloading...")
        loadingProgressBarDownload.setCancelable(false)
        loadingProgressBarDownload.setCanceledOnTouchOutside(false)
        loadingProgressBarDownload.show()
        when (engine) {
            0 -> {
                SystemUtil.download(imageUrl!!, downloadPath, object : FileDownloadListener() {
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
                        if (ContentResolverUtils.notifyMediaStore(File(task.targetFilePath))) {
                            snackbar(coordinatorLayout_imageView, "已通知系统相册刷新缓存")
                        } else {
                            snackbar(coordinatorLayout_imageView, "已通知系统相册刷新缓存，但系统返回刷新失败，请手动刷新")
                        }
                        alert {
                            title = "下载完成"
                            message = "文件路径:" + task.targetFilePath
                            negativeButton(R.string.text_copy) {
                                ClipboardUtils.copy(this@ImageViewActivity, task.targetFilePath)
                                toast("复制成功")
                            }
                            positiveButton(R.string.text_open) {
                                try {
                                    SystemUtil.openImageFile(this@ImageViewActivity, task.targetFilePath)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    snackbar(coordinatorLayout_imageView, "打开失败")
                                }
                            }
                        }.show()

                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        loadingProgressBarDownload.dismiss()
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        e.printStackTrace()
                        Logger.d("Download error\nfileName:" + task.filename)
                        com.google.android.material.snackbar.Snackbar.make(coordinatorLayout_imageView, "下载失败", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    }

                    override fun warn(task: BaseDownloadTask) {}
                })
            }
            1 -> {
                val downloadId = SystemUtil.downloadAndroid(this@ImageViewActivity, imageUrl!!, downloadPath, fileName)

            }
        }
    }
}
