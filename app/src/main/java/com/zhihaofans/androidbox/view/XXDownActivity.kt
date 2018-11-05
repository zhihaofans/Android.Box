package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.data.XXDownSiteList
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.removeAllItems
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.mod.XXDownMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_xxdown.*
import kotlinx.android.synthetic.main.content_xxdown.*
import org.jetbrains.anko.*

class XXDownActivity : AppCompatActivity() {
    private var resultList = mutableListOf<XXDownResultUrlData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xxdown)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val defaultUrlList = mutableListOf(
                    "http://www.acfun.cn/v/ac4612358",
                    "https://www.bilibili.com/video/av32758674/"
            )
            selector("Site", XXDownSiteList.getNameList()) { _: DialogInterface, i: Int ->
                alert {
                    title = XXDownSiteList.getName(i)
                    message = "请输入地址"
                    customView {
                        verticalLayout {
                            val et = editText(defaultUrlList[i])
                            et.setSingleLine(true)
                            okButton {
                                val input = et.text.toString()
                                if (input.isEmpty()) {
                                    snackbar(view, "空白内容")
                                } else {
                                    val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                                    loadingProgressBar.setCancelable(false)
                                    loadingProgressBar.setCanceledOnTouchOutside(false)
                                    loadingProgressBar.show()
                                    doAsync {
                                        val xxDownResultData = XXDownMod.get(i, input)
                                        uiThread {
                                            if (xxDownResultData == null) {
                                                loadingProgressBar.dismiss()
                                                snackbar(coordinatorLayout_xxdown, "错误：返回结果为NULL")
                                            } else {
                                                if (xxDownResultData.success) {
                                                    resultList.clear()
                                                    listView_xxdown.removeAllItems()
                                                    resultList = xxDownResultData.url.toMutableList()
                                                    listView_xxdown.init(this@XXDownActivity, resultList.map { i -> i.url })
                                                    listView_xxdown.setOnItemClickListener { _, _, pos, _ ->
                                                        SystemUtil.browse(this@XXDownActivity, resultList[pos].url)
                                                    }
                                                    loadingProgressBar.dismiss()
                                                    snackbar(coordinatorLayout_xxdown, "加载完毕，共${resultList.size}个结果")
                                                } else {
                                                    loadingProgressBar.dismiss()
                                                    snackbar(coordinatorLayout_xxdown, "错误：${xxDownResultData.message}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.show()
            }
        }
    }

}
