package com.zhihaofans.androidbox.view

import android.app.PendingIntent
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.mod.UrlMod
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.NotificationUtil
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.ContentResolverUtils
import dev.utils.common.FileUtils
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.content_image_view.*

import org.jetbrains.anko.*
import java.io.File


class ImageViewActivity : AppCompatActivity() {
    private var imageUrl: String? = null
    private var clipboardUtil: ClipboardUtil? = null
    private val notificationUtil = NotificationUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        setSupportActionBar(toolbar_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        clipboardUtil = ClipboardUtil(this)
        try {
            if (intent.extras !== null) {
                imageUrl = intent.extras!!.getString("image", null)
                val imageTitle = intent.extras!!.getString("title", null)
                if (imageUrl == null) {
                    toast("null image")
                    finish()
                } else {
                    if (!(imageTitle.isNullOrEmpty())) {
                        this@ImageViewActivity.title = imageTitle
                    }
                    initImage(imageUrl)
                }
            } else {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Try to get image uri fail.")
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initImage(imageUri: String?) {
        if (imageUri.isNullOrEmpty()) {
            toast("Empty image")
            finish()
        } else {
            val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "下载中...")
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
                        loadingProgressBar.dismiss()
                        return
                    }
                    val qualityInfo = imageInfo.qualityInfo
                    Logger.d("Final image received! " +
                            "Size ${imageInfo.width} x ${imageInfo.height}\n" +
                            "Quality level ${qualityInfo.quality}, good enough: ${qualityInfo.isOfGoodEnoughQuality}, full quality: ${qualityInfo.isOfFullQuality}\n"
                    )

                    val layoutParams = imageView.layoutParams
                    layoutParams.width = linearLayout_imageview.width
                    layoutParams.height = (linearLayout_imageview.width.toFloat() / (imageInfo.width.toFloat() / imageInfo.height.toFloat())).toInt()
                    imageView.layoutParams = layoutParams
                    imageView.setOnClickListener {
                        if (!imageUrl.isNullOrEmpty()) {
                            val selectorItemList = mutableListOf(
                                    getString(R.string.text_download),
                                    "浏览器打开",
                                    getString(R.string.text_copy),
                                    getString(R.string.text_share)
                            )
                            selector("", selectorItemList) { _, i ->
                                when (i) {
                                    0 -> {
                                        val downloadMenu = listOf(
                                                "调用adm pro来下载", "使用自带下载"
                                        )
                                        selector("选择下载引擎", downloadMenu) { _, ii ->
                                            when (ii) {
                                                0 -> {
                                                    when {
                                                        OtherAppMod.admProDownload(this@ImageViewActivity, imageUrl) -> coordinatorLayout_imageView.snackbar("调用adm成功")
                                                        OtherAppMod.admProDownload1(this@ImageViewActivity, imageUrl) -> coordinatorLayout_imageView.snackbar("调用adm成功")
                                                        else -> coordinatorLayout_imageView.snackbar("调用adm失败")
                                                    }
                                                }
                                                1 -> {
                                                    XXPermissions.with(this@ImageViewActivity)
                                                            .permission(Permission.Group.STORAGE)
                                                            .request(object : OnPermission {
                                                                override fun hasPermission(granted: List<String>, isAll: Boolean) {
                                                                    if (isAll) {
                                                                        val fileName = FileUtils.getFileName(imageUrl)
                                                                        download(fileName, 0)
                                                                    } else {
                                                                        Snackbar.make(coordinatorLayout_imageView, "需要储存权限", Snackbar.LENGTH_SHORT).setAction("授权") {
                                                                            XXPermissions.gotoPermissionSettings(this@ImageViewActivity, true)
                                                                        }.show()
                                                                    }
                                                                }

                                                                override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                                                                    Snackbar.make(coordinatorLayout_imageView, "需要储存权限", Snackbar.LENGTH_SHORT).setAction("授权") {
                                                                        XXPermissions.gotoPermissionSettings(this@ImageViewActivity, true)
                                                                    }.show()
                                                                }
                                                            })
                                                }
                                            }
                                        }
                                    }
                                    1 -> browse(imageUrl.toString())
                                    2 -> {
                                        clipboardUtil?.copy(imageUrl!!)
                                        Snackbar.make(coordinatorLayout_imageView, "ok", Snackbar.LENGTH_SHORT).show()
                                    }
                                    3 -> share(imageUrl!!)
                                }
                            }
                        }
                    }
                    loadingProgressBar.dismiss()
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
        notificationUtil.init(this@ImageViewActivity)
        val downloadPath: String = UrlMod.APP_PICTURE_DOWNLOAD_PATH + fileName
        Logger.d("downloadPath:$downloadPath")
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "下载中...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        when (engine) {
            0 -> {
                val notification = notificationUtil.createProgress("正在下载", fileName)
                if (notification == null) {
                    notificationUtil.create("错误！", "创建下载通知失败")
                }
                SystemUtil.download(imageUrl!!, downloadPath, object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {

                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        val progressStr = if (totalBytes > 0) {
                            if (notification !== null) {
                                notificationUtil.setProgressNotificationLength(notification, soFarBytes, totalBytes)
                            }
                            "Downloading:$soFarBytes/$totalBytes"
                        } else {
                            "Downloading"
                        }
                    }

                    override fun blockComplete(task: BaseDownloadTask?) {}

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                    }

                    override fun completed(task: BaseDownloadTask) {
                        if (notification !== null) notificationUtil.delete(notification.notificationId)
                        val stackBuilder = TaskStackBuilder.create(this@ImageViewActivity)
                        val resultPendingIntent = stackBuilder.apply {
                            addNextIntent(SystemUtil.getOpenImageFileIntent(this@ImageViewActivity, File(task.targetFilePath)))
                        }.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                        if (resultPendingIntent == null) {
                            notificationUtil.create("错误！", "创建打开图片通知失败")
                        } else {
                            notificationUtil.createIntent("下载完毕", "点击打开图片", resultPendingIntent, true)
                        }
                        if (ContentResolverUtils.notifyMediaStore(File(task.targetFilePath))) {
                            snackbar(coordinatorLayout_imageView, "已通知系统相册刷新缓存")
                        } else {
                            snackbar(coordinatorLayout_imageView, "已通知系统相册刷新缓存，但系统返回刷新失败，请手动刷新")
                        }
                        loadingProgressBar.dismiss()
                        alert {
                            title = "下载完成"
                            message = "文件路径:" + task.targetFilePath
                            negativeButton(R.string.text_copy) {
                                clipboardUtil?.copy(task.targetFilePath)
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
                        loadingProgressBar.dismiss()
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        e.printStackTrace()
                        Logger.d("Download error\nfileName:" + task.filename)
                        loadingProgressBar.dismiss()
                        Snackbar.make(coordinatorLayout_imageView, "下载失败", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun warn(task: BaseDownloadTask) {}
                })
            }
            else -> {
                loadingProgressBar.dismiss()
                coordinatorLayout_imageView.snackbar("未知下载引擎")
            }
        }
    }
}
