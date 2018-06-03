package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.content_image_view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast


class ImageViewActivity : AppCompatActivity() {
    private var imageUrl: String? = ""
    private val sysUtil = SystemUtil()
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
            val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
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
                    imageView.onLongClick {
                        if (!(imageUrl.isNullOrEmpty())) {
                            val selectorItemList = mutableListOf(
                                    "下载",
                                    "浏览器打开",
                                    "复制图片地址"
                            )
                            selector("", selectorItemList, { _, i ->
                                when (i) {
                                    0 -> Snackbar.make(coordinatorLayout_imageView, "暂不支持", Snackbar.LENGTH_SHORT).show()
                                    1 -> sysUtil.chromeCustomTabs(this@ImageViewActivity, imageUrl.toString())
                                    2 -> {
                                        ClipboardUtils.copy(this@ImageViewActivity, imageUrl)
                                        Snackbar.make(coordinatorLayout_imageView, getString(R.string.text_ok), Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            })
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
}
