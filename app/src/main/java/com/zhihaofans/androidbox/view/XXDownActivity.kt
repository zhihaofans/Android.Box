package com.zhihaofans.androidbox.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.XXDownResultData
import com.zhihaofans.androidbox.data.XXDownResultUrlData
import com.zhihaofans.androidbox.mod.ItemIdMod
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.mod.UrlMod
import com.zhihaofans.androidbox.mod.XXDownMod
import com.zhihaofans.androidbox.util.LogUtil
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.DialogUtils
import io.zhihao.library.android.kotlinEx.*
import kotlinx.android.synthetic.main.activity_xxdown.*
import kotlinx.android.synthetic.main.content_xxdown.*
import org.jetbrains.anko.*
import java.net.URL

class XXDownActivity : AppCompatActivity() {
    private var resultList = mutableListOf<XXDownResultUrlData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xxdown)
        setSupportActionBar(toolbar_xxdown)
        fab_xxdown.setOnClickListener {
            input()
        }
        initShare()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    private fun input() {
        alert {
            customView {
                val inputUrl = editText()
                okButton {
                    when {
                        inputUrl.string.isEmpty() -> {
                            Snackbar.make(coordinatorLayout_xxdown, "请输入内容", Snackbar.LENGTH_SHORT).setAction("重新输入") {
                                input()
                            }.show()
                        }
                        inputUrl.string.isUrl() -> {
                            start(inputUrl.string)
                        }
                        else -> {
                            Snackbar.make(coordinatorLayout_xxdown, "请输入正确的网址", Snackbar.LENGTH_SHORT).setAction("重新输入") {
                                input()
                            }.show()
                        }
                    }
                }
            }
        }.show()
    }

    private fun start(url: String) {
        val loadingProgressBar = DialogUtils.createProgressDialog(this, "下载中...", "Please wait a bit…")
        loadingProgressBar.setCancelable(false)
        loadingProgressBar.setCanceledOnTouchOutside(false)
        loadingProgressBar.show()
        doAsync {
            val result = auto(url.toUrl())
            uiThread {
                if (result == null) {
                    loadingProgressBar.dismiss()
                    Snackbar.make(coordinatorLayout_xxdown, "解析失败，不支持该网址", Snackbar.LENGTH_SHORT).setAction("重新输入") {
                        input()
                    }.show()
                    LogUtil.d("解析失败，不支持该网址")
                } else {
                    initListView(loadingProgressBar, result)
                }
            }
        }
    }

    private fun auto(url: URL): XXDownResultData? {
        val mUrl = url.toString()
        LogUtil.d(mUrl)
        LogUtil.d(url.host)
        return when {
            mUrl.startsWith(UrlMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL) -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_ACFUN_VIDEO_THUMBNAIL, url) //ACfun video thumbnail
            mUrl.startsWith(UrlMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL) -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_BILIBILI_VIDEO_THUMBNAIL, url) //Bilibili video thumbnail
            else -> when (url.host) {
                UrlMod.XXDOWN_SITE_HOST_GITHUB_RELEASE -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_GITHUB_RELEASE, url) //Github release
                UrlMod.XXDOWN_SITE_HOST_INSTAGRAM -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_INSTAGRAM, url) //Instagram
                UrlMod.XXDOWN_SITE_HOST_TWITTER -> XXDownMod.get(ItemIdMod.XXDOWN_SITE_TWITTER, url) //Twitter
                else -> null
            }
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
                listView_xxdown.init(resultList.map { i -> i.url })
                listView_xxdown.setOnItemClickListener { _, _, pos, _ ->
                    val itemUrl = resultList[pos].url
                    val menus = listOf(
                            "浏览", "下载"
                    )
                    selector("", menus) { _, ii ->
                        when (ii) {
                            0 -> SystemUtil.browse(this@XXDownActivity, itemUrl)
                            1 -> when {
                                OtherAppMod.admAutoDownload(itemUrl) -> coordinatorLayout_xxdown.snackbar("调用adm下载成功")
                                else -> coordinatorLayout_xxdown.snackbar("调用adm下载失败")
                            }
                        }
                    }

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
            mIntent.isActionSend && mIntent.type == "text/plain" -> {
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
            mIntent.isActionView -> {
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
