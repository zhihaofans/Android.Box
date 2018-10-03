package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.xuexiang.xqrcode.XQRCode
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.snackbar
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.content_qrcode.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.share
import org.jetbrains.anko.toast
import java.util.*


class QrcodeActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
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
            if (editText_qrcode_content.text.isEmpty()) {
                getCameraPermission()
            } else {
                qrcodeMenu()
            }
        }
        button_open.onClick {
            if (editText_qrcode_content.text.isNotEmpty()) {
                try {
                    SystemUtil.browse(this@QrcodeActivity, editText_qrcode_content.text.toString())
                } catch (e: Exception) {
                    toast("打开失败，错误的地址")
                    //throw RuntimeException("No a correct url.", e)
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
                    666 -> {
                        if (data != null) {
                            val uri = data.data//得到uri，后面就是将uri转化成file的过程。
                            val realUri = SystemUtil.getRealFilePath(this@QrcodeActivity, uri)
                            Logger.d("uri:${uri.path}")
                            Logger.d("realUri:$realUri")
                            val qrcodeResult = XQRCode.getAnalyzeQRCodeResult(realUri)
                            if (qrcodeResult == null) {
                                snackbar(coordinatorLayout_qrcode, "解析失败(qrcodeResult=null)")
                            } else {
                                val qrcodeStr = qrcodeResult.text
                                Logger.d("qrcodeStr:$qrcodeStr")
                                editText_qrcode_content.setText(qrcodeStr)
                                snackbar(coordinatorLayout_qrcode, "解析完毕")
                            }
                        } else {
                            snackbar(coordinatorLayout_qrcode, "返回空白数据")
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
            getString(R.string.text_qrcode_generate),
            "Open image file"
    )) { _, i ->
        when (i) {
            0 -> getCameraPermission()
            1 -> generateQR()
            2 -> openFile()
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
        val qrcodeContentInput = editText_qrcode_content.text.toString()
        if (qrcodeContentInput.isNotEmpty()) {
            val qrcodeData = XQRCode.createQRCodeWithLogo(qrcodeContentInput, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            if (qrcodeData !== null) {
                imageView_qrcode.setImageDrawable(BitmapDrawable(resources, qrcodeData))
            } else {
                Snackbar.make(coordinatorLayout_qrcode, "生成二维码失败，返回null数据", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(coordinatorLayout_qrcode, "请输入内容", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun getCameraPermission() {
        XXPermissions.with(this)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.REQUEST_INSTALL_PACKAGES, Permission.SYSTEM_ALERT_WINDOW) //支持请求安装权限和悬浮窗权限
                .permission(Permission.Group.CAMERA) //支持多个权限组进行请求，不指定则默以清单文件中的危险权限进行请求
                .request(object : OnPermission {
                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                        if (isAll) {
                            qrcode.scan(0)
                        } else {
                            Snackbar.make(coordinatorLayout_qrcode, "未授权储存权限，无法扫码", Snackbar.LENGTH_SHORT).setAction("授权") { getCameraPermission() }.show()
                        }
                    }

                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        Snackbar.make(coordinatorLayout_qrcode, "未授权储存权限，无法扫码", Snackbar.LENGTH_SHORT).setAction("授权") { getCameraPermission() }.show()
                    }
                })
    }

    private fun openFile() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 666)

        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //intent.type = "image/*"//设置类型，我这里是任意类型，任意后缀的可以这样写。
        //intent.addCategory(Intent.CATEGORY_OPENABLE)
        //startActivityForResult(intent, 666)
    }
}
