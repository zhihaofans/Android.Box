package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.XXDownResultData
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.kotlinEx.*
import com.zhihaofans.androidbox.mod.ItemIdMod
import com.zhihaofans.androidbox.mod.UrlMod
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
        setSupportActionBar(toolbar_xxdown)
        fab_xxdown.setOnClickListener {
            alert {
                customView {
                    val inputUrl = editText()
                    okButton {
                        when {
                            inputUrl.string.isEmpty() -> {
                                coordinatorLayout_xxdown.snackbar("请输入内容")
                            }
                            inputUrl.string.isUrl() -> {
                                start(inputUrl.string)
                            }
                            else -> {
                                coordinatorLayout_xxdown.snackbar("请输入正确的网址")
                            }
                        }
                    }
                }
            }.show()
        }
        initShare()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("onDestroy")
        finish()
        Logger.d("finish")
    }

    private fun start(url: String) {
        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "下载中...")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        doAsync {
            val result = auto(url)
            uiThread {
                if (result == null) {
                    loadingProgressBar.dismiss()
                    coordinatorLayout_xxdown.snackbar("解析失败，不支持该网址")
                    Logger.d("解析失败，不支持该网址")
                } else {
                    initListView(loadingProgressBar, result)
                }
            }
        }
    }

    private fun auto(url: String): XXDownResultData? {
        return when {
            url.startsWith(UrlMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL) -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL, url) //ACfun video thumbnail
            url.startsWith(UrlMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL) -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL, url) //Bilibili video thumbnail
            url.startsWith(UrlMod.XXDOWN_SITE_GITHUB_RELEASE) -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_GITHUB_RELEASE, url) //Github release
            else -> null
        }
    }

    private fun initListView(loadingProgressBar: ProgressDialog, data: XXDownResultData?) {
        when {
            data == null -> {
                loadingProgressBar.dismiss()
                snackbar(coordinatorLayout_xxdown, "错误：返回结果为NULL")
            }

            data.success -> {
                resultList.clear()
                listView_xxdown.removeAllItems()
                resultList = data.url.toMutableList()
                listView_xxdown.init(this@XXDownActivity, resultList.map { i -> i.url })
                listView_xxdown.setOnItemClickListener { _, _, pos, _ ->
                    SystemUtil.browse(this@XXDownActivity, resultList[pos].url)
                }
                loadingProgressBar.dismiss()
                snackbar(coordinatorLayout_xxdown, "加载完毕，共${resultList.size}个结果")
            }
            else -> {
                loadingProgressBar.dismiss()
                snackbar(coordinatorLayout_xxdown, "错误：${data.message}")
            }
        }

    }

    private fun initShare() {
        val mIntent = intent
        when {
            mIntent.action == Intent.ACTION_SEND && mIntent.type == "text/plain" -> {
                val st = mIntent.getStringExtra(Intent.EXTRA_TEXT)
                if (st != null) {
                    if (st.isUrl()) {
                        start(st)
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            }
            mIntent.action == Intent.ACTION_VIEW -> {
                val uri = mIntent.data
                if (uri !== null) {
                    start(uri.toString())
                } else {
                    finish()
                }
            }
        }
    }
}
