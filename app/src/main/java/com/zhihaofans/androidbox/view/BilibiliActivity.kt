package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.*

import kotlinx.android.synthetic.main.activity_bilibili.*
import kotlinx.android.synthetic.main.content_bilibili.*
import okhttp3.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.io.IOException
//import org.xml.sax
class BilibiliActivity : AppCompatActivity() {
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    private val g = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilibili)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val listData = mutableListOf(
                "弹幕用户hash查用户uid"
        )
        listView_bilibili.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData)
        listView_bilibili.onItemClick { _, _, index, _ ->
            when (index) {
                0 -> {
                    //弹幕用户hash查用户id
                    var input = EditText(this@BilibiliActivity)
                    alert("", "请输入弹幕里的用户hash") {
                        customView {
                            input = editText("")
                        }
                        yesButton {
                            if (input.text.isNullOrEmpty()) {
                                Snackbar.make(coordinatorLayout_bilibili, "请输入弹幕里的用户hash", Snackbar.LENGTH_SHORT).show()
                            } else {
                                val url = "http://biliquery.typcn.com/api/user/hash/${input.text}"
                                val client = OkHttpClient()
                                val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                                loadingProgressBar.setCancelable(false)
                                loadingProgressBar.setCanceledOnTouchOutside(false)
                                loadingProgressBar.show()
                                request.url(url)
                                Logger.d("url:$url")
                                val call = client.newCall(request.build())
                                call.enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        runOnUiThread {
                                            loadingProgressBar.dismiss()
                                            Snackbar.make(coordinatorLayout_bilibili, "获取弹幕列表失败", Snackbar.LENGTH_SHORT).show()
                                        }
                                        e.printStackTrace()
                                    }

                                    @Throws(IOException::class)
                                    override fun onResponse(call: Call, response: Response) {
                                        val resBody = response.body()
                                        var responseStr = ""
                                        runOnUiThread {
                                            loadingProgressBar.dismiss()
                                        }
                                        if (resBody != null) {
                                            responseStr = resBody.string()
                                            if (responseStr.isEmpty()) {
                                                runOnUiThread {
                                                    Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                val bilibiliDanmuGetHashGson = g.fromJson(responseStr, BilibiliDanmuGetHashGson::class.java) as BilibiliDanmuGetHashGson
                                                if (bilibiliDanmuGetHashGson.error == 0) {
                                                    val data = bilibiliDanmuGetHashGson.data
                                                    if (data.size == 0) {
                                                        runOnUiThread {
                                                            Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回结果列表空白", Snackbar.LENGTH_SHORT).show()
                                                        }
                                                    } else if (data.size == 1) {
                                                        jx(data, client)
                                                    } else {
                                                        runOnUiThread {
                                                            Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回结果太多，解析失败", Snackbar.LENGTH_LONG).setAction("只解析第一个",
                                                                    {
                                                                        jx(data, client)
                                                                    }
                                                            ).show()
                                                        }
                                                    }
                                                } else {
                                                    Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回错误代码(${bilibiliDanmuGetHashGson.error})", Snackbar.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回结果出现代码错误", Snackbar.LENGTH_SHORT).show()
                                        }
                                        Logger.d(responseStr)
                                    }
                                })

                            }
                        }
                        noButton { }

                    }.show()

                }
            }
        }
    }

    private fun jx(data: MutableList<BilibiliDanmuGetHashItemGson>, client: OkHttpClient) {
        val uid = data[0].id
        Snackbar.make(coordinatorLayout_bilibili, "获取成功，用户uid为$uid", Snackbar.LENGTH_SHORT).setAction(getString(R.string.text_more), {
            val acts = mutableListOf(
                    "获取用户信息"
            )
            selector("", acts, { _, index ->
                when (index) {
                    0 -> {
                        val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                        loadingProgressBar.setCancelable(false)
                        loadingProgressBar.setCanceledOnTouchOutside(false)
                        loadingProgressBar.show()
                        val uInfoUrl = "https://api.bilibili.com/x/web-interface/card?mid=$uid&type=json"
                        request.url(uInfoUrl)
                        Logger.d("url:$uInfoUrl")
                        val call = client.newCall(request.build())
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                runOnUiThread {
                                    loadingProgressBar.dismiss()
                                    Snackbar.make(coordinatorLayout_bilibili, "获取用户信息失败", Snackbar.LENGTH_SHORT).show()
                                }
                                e.printStackTrace()
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                val resBody = response.body()
                                var responseStr = ""
                                runOnUiThread {
                                    loadingProgressBar.dismiss()
                                    if (resBody != null) {
                                        responseStr = resBody.string()
                                        if (responseStr.isEmpty()) {
                                            Snackbar.make(coordinatorLayout_bilibili, "获取用户信息失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()

                                        } else {
                                            val bilibiliUserInfoResultGson = g.fromJson(responseStr, BilibiliUserInfoResultGson::class.java) as BilibiliUserInfoResultGson
                                            if (bilibiliUserInfoResultGson.code == 0) {
                                                val userCard: BilibiliUserInfoResultCardGson = bilibiliUserInfoResultGson.data.card
                                                if (userCard.mid != uid.toString()) {
                                                    Snackbar.make(coordinatorLayout_bilibili, "发生错误，所请求的id与服务器返回用户id不同(请求id:$uid|返回id:${userCard.mid}|返回用户昵称:${userCard.name})", Snackbar.LENGTH_SHORT).show()

                                                } else {
                                                    val acts_result = mutableListOf(
                                                            "昵称:${userCard.name}",
                                                            "uid:${userCard.mid}",
                                                            "性别:${userCard.sex}",
                                                            "头像地址:${userCard.face}",
                                                            "关注:${userCard.attention}",
                                                            "粉丝:${userCard.fans}",
                                                            "等级:${userCard.level_info.current_level}"
                                                    )
                                                    selector("", acts_result, { _, index_a ->
                                                        when (index_a) {
                                                            0, 1, 2, 6 -> {
                                                                _c(acts_result[index_a])
                                                            }
                                                            3 -> {
                                                                selector("", mutableListOf(
                                                                        getString(R.string.text_open),
                                                                        getString(R.string.text_copy),
                                                                        getString(R.string.text_share)
                                                                ), { _, index_b ->
                                                                    when (index_b) {
                                                                        0 -> browse(userCard.face)
                                                                        1 -> _c(userCard.face)
                                                                        2 -> share(userCard.face)
                                                                    }
                                                                })
                                                            }
                                                            4 -> {
                                                                if (userCard.attention > 0) {
                                                                    val followers: MutableList<String> = userCard.attentions.map { it.toString() }.toMutableList()
                                                                    selector("uid按关注倒序显示", followers, { _, index_c ->
                                                                        browse("https://space.bilibili.com/${followers[index_c]}")
                                                                    })
                                                                } else {
                                                                    Snackbar.make(coordinatorLayout_bilibili, "Ta没有关注", Snackbar.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                            5 -> {
                                                                if (userCard.fans > 0) {
                                                                    Snackbar.make(coordinatorLayout_bilibili, "暂不支持查看粉丝列表", Snackbar.LENGTH_SHORT).show()
                                                                } else {
                                                                    Snackbar.make(coordinatorLayout_bilibili, "Ta没有粉丝", Snackbar.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    })

                                                }
                                            } else {
                                                Snackbar.make(coordinatorLayout_bilibili, "返回错误(${bilibiliUserInfoResultGson.message})", Snackbar.LENGTH_SHORT).show()

                                            }
                                        }
                                    }
                                }
                            }
                        })

                    }
                }
            })
        }
        ).show()
    }

    private fun _c(string: String) {//复制到剪切板
        ClipboardUtils.copy(this@BilibiliActivity, string)
        Snackbar.make(coordinatorLayout_bilibili, "已复制到剪切板", Snackbar.LENGTH_SHORT).show()

    }
}
