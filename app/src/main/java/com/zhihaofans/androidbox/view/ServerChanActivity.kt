package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import com.haoge.easyandroid.easy.EasySharedPreferences
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.database.SaveDataSP
import com.zhihaofans.androidbox.gson.ServerChanGson
import com.zhihaofans.androidbox.util.SystemUtil
import okhttp3.*
import org.jetbrains.anko.*
import java.io.IOException
import java.util.*


class ServerChanActivity : AppCompatActivity() {
    private val g = Gson()
    //private val serverChanKey = "SCU6647T00deca519cb008cd7e66b6da08d8fd5058c8159b9e0cc"
    private var serverChanKey: String = ""
    private val saveDataSP = EasySharedPreferences.load(SaveDataSP::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_server_chan)
        setSupportActionBar(toolbar)*/
        val savedKey: String? = saveDataSP.server_chan_key
        if (savedKey.isNullOrEmpty()) {
            updateKey()
        } else {
            serverChanKey = savedKey!!
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

    private fun updateKey(oldKey: String = "") {
        alert("", "请输入SCKEY") {
            customView {
                verticalLayout {
                    val editText_title = editText(oldKey)
                    editText_title.setSingleLine(true)
                    positiveButton(R.string.text_save) {
                        val input_key: String = editText_title.text.toString()
                        if (input_key.isNotEmpty()) {
                            serverChanKey = input_key
                            saveDataSP.server_chan_key = input_key
                            saveDataSP.apply {
                                toast("保存成功")
                                init()
                            }
                        } else {
                            toast("SCKEY不能为空")
                            updateKey(oldKey)
                        }
                    }
                    negativeButton("打开官网") {
                        browse("https://sc.ftqq.com/")
                        updateKey(oldKey)
                    }
                }
            }
            onCancelled {
                toast(R.string.text_cancel)
                finish()
            }
        }.show()
    }

    private fun askPush(title: String = "", desp: String = "") {
        if (serverChanKey.isEmpty()) {
            updateKey(serverChanKey)
        } else {
            alert("Title必须输入", "Server Chan") {
                customView {
                    verticalLayout {
                        textView("Title:(最多256字符)")
                        val editText_title = editText(title)
                        textView("Description:(最长64Kb,支持MarkDown)")
                        val editText_desp = editText(desp)
                        editText_title.setSingleLine(true)
                        editText_title.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(256))
                        editText_title.setOnEditorActionListener { v, actionId, event ->
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if (editText_title.text.isNotEmpty()) {
                                    SystemUtil.viewGetFocusable(editText_desp)
                                }
                            }
                            false
                        }
                        editText_desp.setSingleLine(false)
                        // editText_desp.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        editText_desp.setHorizontallyScrolling(false)
                        editText_desp.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(64000))
                        positiveButton(R.string.text_send) {
                            val input_title: String = editText_title.text.toString()
                            val input_desp: String = editText_desp.text.toString()
                            if (input_title.isNotEmpty() && input_title.length <= 256) {
                                pushMsg(serverChanKey, input_title, input_desp)
                            }
                        }
                        negativeButton("修改SCKEY") {
                            updateKey(serverChanKey)
                        }
                    }
                }
                onCancelled {
                    toast(R.string.text_cancel)
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
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        call_cid.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                runOnUiThread {
                    loadingProgressBar.dismiss()
                    toast("连接失败")
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
                                    toast("服务器返回空白数据")

                                } else {
                                    if (response.header("content-type") == "text/html;charset=utf-8") {
                                        try {
                                            val scGson = g.fromJson(responseStr, ServerChanGson::class.java)
                                            when (scGson.errno) {
                                                0 -> toast("OK")
                                                else -> toast("Error ${scGson.errno}:${scGson.errmsg}")
                                            }
                                        } catch (e: Exception) {
                                            toast("解析数据失败")
                                            Logger.e("responseStr:\nresponseStr")
                                            e.printStackTrace()
                                        }
                                    } else {
                                        toast("Error (content-type)")
                                    }
                                }

                            } else {
                                toast("空白结果")
                            }
                        }
                        else -> toast("错误代码 $webCode")
                    }
                    loadingProgressBar.dismiss()
                    finish()
                }
            }
        })
    }
}
