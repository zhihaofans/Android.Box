package com.zhihaofans.androidbox.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.xuexiang.xqrcode.XQRCode
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.content_qrcode.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*


class QrcodeActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private val sysUtil = SystemUtil()
    private var methodId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        setSupportActionBar(toolbar)
        qrcode.setActivity(this@QrcodeActivity, true)

        fab_qrcode.setOnClickListener { view ->
            qrcodeMenu()
        }
        checkMethod()
        imageView_qrcode.onClick {
            qrcodeMenu()
        }
        button_open.onClick {
            if (editText_qrcode_content.text.isNotEmpty()) {
                try {
                    sysUtil.browseWeb(this@QrcodeActivity, editText_qrcode_content.text.toString())
                } catch (e: Exception) {
                    toast("打开失败，错误的地址")
                    e.printStackTrace()
                }
            }
        }
        button_copy.onClick { if (editText_qrcode_content.text.isNotEmpty()) ClipboardUtils.copy(this@QrcodeActivity, editText_qrcode_content.text.toString()) }
        button_share.onClick { if (editText_qrcode_content.text.isNotEmpty()) share(editText_qrcode_content.text.toString()) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    0 -> {
                        val qrResult = qrcode.handleScanResult(data)
                        when (qrResult["code"]) {
                            "0" -> {
                                val qrData = qrResult["qrcodeData"].toString()
                                Logger.d(qrData)
                                editText_qrcode_content.setText(qrData)
                            }
                        }
                    }
                    1, 2 -> {
                        if (qrcode.isInstallQrPlugin && data != null) {
                            Logger.d(data.extras)
                            if (data.hasExtra("data")) {
                                val result: String = data.getStringExtra("data")
                                Logger.d(result)
                                editText_qrcode_content.setText(result)
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                when (methodId) {
                    null -> Snackbar.make(coordinatorLayout_qrcode, R.string.text_canceled_by_user, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        toast(R.string.text_canceled_by_user)
                        finish()
                    }
                }
            }
        }
    }

    private fun qrcodeMenu() = selector(getString(R.string.text_qrcode), mutableListOf(
            getString(R.string.text_qrcode_scan),
            getString(R.string.text_qrcode_generate)
    )) { _, i ->
        when (i) {
            0 -> getCameraPermission()
            1 -> generateQR()
        }
    }


    private fun checkMethod() {
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st != null) {
                editText_qrcode_content.setText(st)
                methodId = "QRCODE_GENERATE_BY_SHARE"
                generateQR()
            }
        } else {
            try {
                methodId = intent.extras.getString("method", null)
                if (!methodId.isNullOrEmpty()) {
                    when (methodId) {
                        "QRCODE_SCAN" -> getCameraPermission()
                        "QRCODE_GENERATE" -> generateQR()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateQR() {
        var qrcodeContentInput = editText_qrcode_content.text.toString()
        alert(getString(R.string.text_qrcode_content), getString(R.string.text_qrcode_generate)) {
            customView {
                verticalLayout {
                    val editText_input = editText(qrcodeContentInput)
                    editText_input.setSingleLine(true)
                    yesButton {
                        qrcodeContentInput = editText_input.text.toString()
                        if (qrcodeContentInput.isNotEmpty()) {
                            val qrcodeData = XQRCode.createQRCodeWithLogo(qrcodeContentInput, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                            //val qrcodeData: Bitmap? = XQRCode.newQRCodeBuilder(qrcodeContentInput).build()
                            if (qrcodeData !== null) {
                                imageView_qrcode.setImageDrawable(BitmapDrawable(resources, qrcodeData))
                            } else {
                                Snackbar.make(coordinatorLayout_qrcode, "生成二维码失败，返回null数据", Snackbar.LENGTH_SHORT).show()
                            }
                        } else {
                            Snackbar.make(coordinatorLayout_qrcode, "请输入内容", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            onCancelled {
                when (methodId) {
                    null -> Snackbar.make(coordinatorLayout_qrcode, R.string.text_cancel, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        toast(R.string.text_cancel)
                        finish()
                    }
                }
            }
        }.show()
    }

    private fun getCameraPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.CAMERA, getString(R.string.text_permission_camera), R.drawable.permission_ic_camera))
        HiPermission.create(this@QrcodeActivity)
                .permissions(permissionItems)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onClose() {
                        Logger.i("onClose")
                        Snackbar.make(coordinatorLayout_qrcode, "用户关闭权限申请", Snackbar.LENGTH_SHORT)
                    }

                    override fun onFinish() {
                        qrcode.scan(0)
                    }

                    override fun onDeny(permission: String, position: Int) {
                        Logger.d("onDeny")
                        Snackbar.make(coordinatorLayout_qrcode, "相机权限申请失败", Snackbar.LENGTH_SHORT)
                    }

                    override fun onGuarantee(permission: String, position: Int) {
                        Logger.d("onGuarantee")
                        Snackbar.make(coordinatorLayout_qrcode, "Error:onGuarantee($position)", Snackbar.LENGTH_SHORT)
                    }
                })
    }
}
