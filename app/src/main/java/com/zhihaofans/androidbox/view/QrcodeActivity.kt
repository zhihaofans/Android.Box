package com.zhihaofans.androidbox.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.orhanobut.logger.Logger
import com.xuexiang.xqrcode.XQRCode
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.saveFile
import com.zhihaofans.androidbox.kotlinEx.saveFile1
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.kotlinEx.string
import com.zhihaofans.androidbox.mod.QrcodeMod
import com.zhihaofans.androidbox.mod.UrlMod
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.IntentUtil
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.AppUtils
import dev.utils.app.ContentResolverUtils
import dev.utils.app.DialogUtils
import dev.utils.app.image.BitmapUtils
import dev.utils.common.DateUtils
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.content_qrcode.*
import org.jetbrains.anko.*
import java.io.File
import java.util.*


class QrcodeActivity : AppCompatActivity() {
    private val qrcode = QrcodeMod()
    private var methodId: String? = null
    private var clipboardUtil: ClipboardUtil? = null
    private var hasQrcode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        setSupportActionBar(toolbar_qrcode)
        qrcode.setActivity(this@QrcodeActivity, true)
        clipboardUtil = ClipboardUtil(this)
        fab_qrcode.setOnClickListener {
            if (hasQrcode) {
                val fabMenu = mutableListOf("打开二维码图片文件", "保存二维码图片")
                selector("", fabMenu) { _, i ->
                    when (i) {
                        0 -> openFile()
                        1 -> {
                            try {
                                val qrcodeImage = BitmapUtils.drawable2Bitmap(imageView_qrcode.image)
                                if (qrcodeImage == null) {
                                    coordinatorLayout_qrcode.snackbar("空白二维码，保存失败")
                                } else {
                                    val progressDialog = DialogUtils.createProgressDialog(this, "Saving...", "Please wait a bit…")
                                    progressDialog.show()
                                    doAsync {
                                        val fileName = UrlMod.APP_PICTURE_DOWNLOAD_PATH + "qrcode_" +
                                                DateUtils.getDateNow("yyyy_MM_dd_HH_mm_ss").replace(" ", "_").replace("-", "_") +
                                                ".png"
                                        var saveSu = qrcodeImage.saveFile(fileName)
                                        if (!saveSu) {
                                            Logger.e("qrcodeImage.saveFile(fileName) = false")
                                            saveSu = qrcodeImage.saveFile1(fileName)
                                            Logger.d("qrcodeImage.saveFile1(fileName) = $saveSu")
                                        }
                                        uiThread {
                                            DialogUtils.closeDialog(progressDialog)
                                            toast("保存" + saveSu.string("至$fileName", "失败"))
                                            ContentResolverUtils.notifyMediaStore(File(fileName))
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                coordinatorLayout_qrcode.snackbar("保存失败，发生未知错误")
                            }
                        }
                    }
                }
            } else {
                openFile()
            }

        }
        checkMethod()
        if (AppUtils.isAppDebug()) {
            editText_qrcode_content.setText(UrlMod.UPDATE_FIR_IM)
            generateQR()
        }
        imageView_qrcode.setOnClickListener {
            getCameraPermission()
        }
        button_open.setOnClickListener {
            if (editText_qrcode_content.text.isNotEmpty()) {
                try {
                    //SystemUtil.browse(this@QrcodeActivity, editText_qrcode_content.text.toString())
                    startActivity<Browser2BrowserActivity>("uri" to editText_qrcode_content.text.toString())
                } catch (e: Exception) {
                    toast("打开失败，错误的地址")
                    //throw RuntimeException("No a correct url.", e)
                    e.printStackTrace()
                }
            }
        }
        button_copy.setOnClickListener { if (editText_qrcode_content.text.isNotEmpty()) clipboardUtil?.copy(editText_qrcode_content.text.toString()) }
        button_share.setOnClickListener { if (editText_qrcode_content.text.isNotEmpty()) share(editText_qrcode_content.text.toString()) }
        editText_qrcode_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                generateQR()
            }
        })
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
                            if (uri == null) {
                                coordinatorLayout_qrcode.snackbar("解析失败(uri=null)")
                            } else {
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
                if (intent.extras !== null) {
                    methodId = intent.extras!!.getString("method", null)
                    if (!methodId.isNullOrEmpty()) {
                        when (methodId) {
                            "QRCODE_SCAN" -> getCameraPermission()
                            "QRCODE_GENERATE" -> generateQR()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateQR(firstRun: Boolean = false) {
        val qrcodeContentInput = editText_qrcode_content.text.toString()
        if (qrcodeContentInput.isNotEmpty()) {
            val qrcodeData = XQRCode.createQRCodeWithLogo(qrcodeContentInput, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            if (qrcodeData !== null) {
                imageView_qrcode.setImageDrawable(BitmapDrawable(resources, qrcodeData))
                hasQrcode = true
            } else {
                coordinatorLayout_qrcode.snackbar("生成二维码失败，返回null数据")
            }
        } else {
            if (!firstRun) coordinatorLayout_qrcode.snackbar("请输入内容")
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
                            try {
                                qrcode.scan(0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                coordinatorLayout_qrcode.snackbar("启动失败")
                            }
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
        try {
            val photoPickerIntent = IntentUtil.getChooseImageFileIntent()
            startActivityForResult(photoPickerIntent, 666)
        } catch (e: Exception) {
            e.printStackTrace()
            coordinatorLayout_qrcode.snackbar("打开文件失败")
        }

        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //intent.type = "image/*"//设置类型，我这里是任意类型，任意后缀的可以这样写。
        //intent.addCategory(Intent.CATEGORY_OPENABLE)
        //startActivityForResult(intent, 666)
    }
}
