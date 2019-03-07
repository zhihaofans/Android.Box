package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.ServerChanGson
import com.zhihaofans.androidbox.mod.AppSettingMod
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.ToastUtil
import dev.utils.app.DialogUtils
import okhttp3.*
import org.jetbrains.anko.*
import java.io.IOException
import java.util.*


class ServerChanActivity : AppCompatActivity() {
    private val g = Gson()
    //private val serverChanKey = "SCU6647T00deca519cb008cd7e66b6da08d8fd5058c8159b9e0cc"
    private val appSettingMod = AppSettingMod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_server_chan)
        setSupportActionBar(toolbar)*/
        appSettingMod.init(this)
        val savedKey: String? = appSettingMod.serverChanKey
        if (savedKey.isNullOrEmpty()) {
            updateKey()
        } else {
            appSettingMod.serverChanKey = savedKey
            init()
        }
    }

    private fun init() {
        var defaultTitle = ""
        var defaultDesp = ""
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st != null) {
                defaultTitle = "来自Android.Box的转发消息"
                defaultDesp = st
            }
        }
        askPush(defaultTitle, defaultDesp)
    }

    private fun updateKey(oldKey: String? = null) {
        alert("", "请输入SCKEY") {
            customView {
                verticalLayout {
                    val editTextTitle = editText(oldKey).apply { setSingleLine(true) }
                    positiveButton(R.string.text_save) {
                        val inputKey: String = editTextTitle.text.toString()
                        if (inputKey.isNotEmpty()) {
                            appSettingMod.serverChanKey = inputKey
                            if (appSettingMod.serverChanKey == inputKey) {
                                ToastUtil.success("保存成功")
                                init()
                            } else {
                                ToastUtil.error("保存失败")
                                finish()
                            }
                        } else {
                            ToastUtil.error("SCKEY不能为空")
                            updateKey(oldKey)
                        }
                    }
                    negativeButton("打开官网") {
                        browse("https://sc.ftqq.com/")
                        finish()
                    }
                }
            }
            onCancelled {
                ToastUtil.warning(R.string.text_cancel)
                finish()
            }
        }.show()
    }

    private fun askPush(title: String = "", desp: String = "") {
        if (appSettingMod.serverChanKey.isNullOrEmpty()) {
            updateKey(appSettingMod.serverChanKey)
        } else {
            alert("Title必须输入", "Server Chan") {
                customView {
                    verticalLayout {
                        textView("Title:(最多256字符)")
                        val editTextTitle = editText(title)
                        textView("Description:(最长64Kb,支持MarkDown)")
                        val editTextDesp = editText(desp).apply {
                            setSingleLine(true)
                            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(256))
                        }
                        editTextTitle.setOnEditorActionListener { v, actionId, event ->
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if (editTextTitle.text.isNotEmpty()) {
                                    SystemUtil.viewGetFocusable(editTextDesp)
                                }
                            }
                            false
                        }
                        editTextDesp.setSingleLine(false)
                        // editText_desp.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        editTextDesp.setHorizontallyScrolling(false)
                        editTextDesp.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(64000))
                        positiveButton(R.string.text_send) {
                            val input_title: String = editTextTitle.text.toString()
                            val input_desp: String = editTextDesp.text.toString()
                            if (input_title.isNotEmpty() && input_title.length <= 256) {
                                pushMsg(appSettingMod.serverChanKey!!, input_title, input_desp)
                            }
                        }
                        negativeButton("修改SCKEY") {
                            updateKey(appSettingMod.serverChanKey)
                        }
                    }
                }
                onCancelled {
                    ToastUtil.warning(R.string.text_cancel)
                    finish()
                }
            }.show()
        }
    }

    private fun pushMsg(pushKey: String, title: String, desp: String = "By com.zhihaofans.androidbox") {
        val pushUrl = "https://sc.ftqq.com/$pushKey.send"
        Logger.d("pushUrl:$pushUrl\ntitle:$title\ndesp:$desp")
        val body: RequestBody = FormBody.Builder()
                .add("text", title)
                .add("desp", desp)
                .build()
        val request: Request = Request.Builder().url(pushUrl).post(body).build()
        val client = OkHttpClient()
        val call_cid = client.newCall(request)
        val loadingProgressBar = DialogUtils.createProgressDialog(this, "Loading...", "Please wait a bit…")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        call_cid.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                runOnUiThread {
                    loadingProgressBar.dismiss()
                    ToastUtil.error("连接失败")
                    e.printStackTrace()
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    val webCode = response.code()
                    when (webCode) {
                        200 -> {
                            val resBody = response.body()
                            if (resBody != null) {
                                val responseStr = resBody.string()
                                Logger.d("responseStr:$responseStr")
                                if (responseStr.isEmpty()) {
                                    ToastUtil.error("服务器返回空白数据")

                                } else {
                                    if (response.header("content-type") == "text/html;charset=utf-8") {
                                        try {
                                            val scGson = g.fromJson(responseStr, ServerChanGson::class.java)
                                            when (scGson.errno) {
                                                0 -> ToastUtil.success("OK")
                                                else -> ToastUtil.error("Error ${scGson.errno}:${scGson.errmsg}")
                                            }
                                        } catch (e: Exception) {
                                            ToastUtil.error("解析数据失败")
                                            Logger.e("responseStr:\nresponseStr")
                                            e.printStackTrace()
                                        }
                                    } else {
                                        ToastUtil.error("Error (content-type)")
                                    }
                                }

                            } else {
                                ToastUtil.error("空白结果")
                            }
                        }
                        else -> ToastUtil.error("错误代码 $webCode")
                    }
                    loadingProgressBar.dismiss()
                    finish()
                }
            }
        })
    }
}
