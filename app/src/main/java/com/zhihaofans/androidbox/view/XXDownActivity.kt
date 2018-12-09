package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.XXDownResultData
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.kotlinEx.*
import com.zhihaofans.androidbox.mod.ItemNameMod
import com.zhihaofans.androidbox.mod.UrlMod
import com.zhihaofans.androidbox.mod.XXDownMod
import com.zhihaofans.androidbox.mod.XXDownSiteList
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_xxdown.*
import kotlinx.android.synthetic.main.content_xxdown.*
import org.jetbrains.anko.*

class XXDownActivity : AppCompatActivity() {
    private var resultList = mutableListOf<XXDownResultUrlData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xxdown)
        setSupportActionBar(toolbar_xxdown)
        fab_xxdown.setOnClickListener { view ->
            val menuList = listOf(
                    "自动识别", "手动选择站点"
            )
            selector("", menuList) { _: DialogInterface, menuI: Int ->
                when (menuI) {
                    0 -> {
                        //TODO:自动识别
                        alert {
                            customView {
                                val inputUrl = editText()
                                okButton {
                                    when {
                                        inputUrl.string().isEmpty() -> {
                                            coordinatorLayout_xxdown.snackbar("请输入内容")
                                        }
                                        inputUrl.string().isUrl() -> {
                                            val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                                            loadingProgressBar.setCancelable(false)
                                            loadingProgressBar.setCanceledOnTouchOutside(false)
                                            loadingProgressBar.show()
                                            doAsync {
                                                val result = auto(inputUrl.string())
                                                uiThread {
                                                    if (result == null) {
                                                        coordinatorLayout_xxdown.snackbar("解析失败，不支持该网址")
                                                    } else {
                                                        initListView(result, loadingProgressBar)
                                                    }
                                                }
                                            }
                                        }
                                        else -> {
                                            coordinatorLayout_xxdown.snackbar("请输入正确的网址")
                                        }
                                    }
                                }
                            }
                        }.show()
                    }
                    1 -> {
                        val defaultUrlList = listOf(
                                UrlMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL + "ac4612358",
                                UrlMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL + "av32758674"
                        )
                        selector("Site", XXDownSiteList.getNameList()) { _: DialogInterface, siteI: Int ->
                            alert {
                                title = XXDownSiteList.getName(siteI)
                                message = "请输入地址"
                                customView {
                                    verticalLayout {
                                        val et = editText(defaultUrlList[siteI])
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
                                                    val xxDownResultData = this@XXDownActivity.auto(input)
                                                    uiThread {
                                                        initListView(xxDownResultData, loadingProgressBar)
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

        }
    }

    private fun auto(url: String): XXDownResultData? {
        return when {
            url.startsWith(UrlMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL) -> {
                XXDownMod.get(ItemNameMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL, url)
            }
            url.startsWith(UrlMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL) -> {
                XXDownMod.get(ItemNameMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL, url)
            }
            else -> null
        }
    }

    private fun initListView(data: XXDownResultData?, loadingProgressDialog: ProgressDialog) {
        if (data == null) {
            loadingProgressDialog.dismiss()
            snackbar(coordinatorLayout_xxdown, "错误：返回结果为NULL")
        } else {
            if (data.success) {
                resultList.clear()
                listView_xxdown.removeAllItems()
                resultList = data.url.toMutableList()
                listView_xxdown.init(this@XXDownActivity, resultList.map { i -> i.url })
                listView_xxdown.setOnItemClickListener { _, _, pos, _ ->
                    SystemUtil.browse(this@XXDownActivity, resultList[pos].url)
                }
                loadingProgressDialog.dismiss()
                snackbar(coordinatorLayout_xxdown, "加载完毕，共${resultList.size}个结果")
            } else {
                loadingProgressDialog.dismiss()
                snackbar(coordinatorLayout_xxdown, "错误：${data.message}")
            }
        }
    }
}
