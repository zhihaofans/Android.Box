package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.browse
import com.zhihaofans.androidbox.mod.OpenerMod
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.util.UiUtil
import io.zhihao.library.android.kotlinEx.*
import kotlinx.android.synthetic.main.activity_opener.*
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import java.net.URL


class OpenerActivity : AppCompatActivity() {
    private val openerMod = OpenerMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opener)
        setSupportActionBar(toolbar_opener)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        try {
            initIntent()
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("Exception")
            finish()
        }
    }

    private fun initIntent() {
        val mIntent = intent
        openerMod.init(mIntent)
        when {
            !openerMod.isInitFinished() -> {
                ToastUtil.error("初始化失败即将退出")
                finish()
            }
            openerMod.isUrl() -> urlMenu()
            mIntent.isActionSend -> {
                when {
                    mIntent.isTypeTextPlain -> textMenu()
                    else -> {
                        ToastUtil.error("未知分享内容")
                        finish()
                    }
                }
            }
            mIntent.isActionProcessText -> textMenu()
            else -> {
                ToastUtil.error("?")
                finish()
            }
        }
    }

    private fun urlMenu() {
        if (!openerMod.isInitFinished()) {
            ToastUtil.error("初始化失败即将退出")
            finish()
        } else if (openerMod.getUrl().isNull()) {
            ToastUtil.error("urlMenu:分享文本为空")
            finish()
        } else {
            val mUrl = openerMod.getUrl()!!
            UiUtil.mdSelector(this, "要干嘛", listOf("打开", "分享", "下载"))
                    .cancelListener {
                        ToastUtil.warning(R.string.text_canceled_by_user)
                        finish()
                    }
                    .itemsCallback { _, _, i, _ ->
                        when (i) {
                            0 -> {
                                //TODO:打开
                                //browse(mUrl)
                                urlOpener(mUrl)
                                //finish()
                                /*
                                UiUtil.mdSelector(this, "要干嘛", listOf("打开", "分享", "下载")).cancelListener {
                                    ToastUtil.warning(R.string.text_canceled_by_user)
                                    finish()
                                }.itemsCallback { _, _, i, _ ->
                                    when (i) {
                                    }
                                }.show()*/
                            }
                            1 -> share(mUrl.toString())
                            2 -> {
                                if (OtherAppMod.admAutoDownload(mUrl)) {
                                    ToastUtil.info("尝试跳转至Advanced Download Manager")
                                } else {
                                    ToastUtil.error("跳转下载失败，请确认有安装《Advanced Download Manager Pro》或者《Advanced Download Manager》")
                                }
                            }
                            else -> {
                                ToastUtil.error("未知错误")
                            }
                        }
                        finish()
                    }.show()
        }
    }

    private fun urlOpener(url: URL) {
        val uri = url.toURI()
        when (uri.scheme) {
            "http", "https" -> {
                ToastUtil.info("开发中")
                browse(url)
            }
            else -> {
                ToastUtil.error("不支持该地址")
            }
        }
        finish()
    }

    private fun textMenu(textData: String? = null) {
        val mIntent = intent
        if (!openerMod.isInitFinished()) {
            ToastUtil.error("初始化失败即将退出")
            finish()
        } else {
            val text = if (textData == null) {
                var mText = mIntent.getTextPlain()
                if (mText.isNullOrEmpty()) {
                    mText = mIntent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                    if (mText.isNullOrEmpty()) {
                        null
                    } else {
                        mText
                    }
                } else {
                    mText
                }
            } else {
                textData
            }
            if (text.isNullOrEmpty()) {
                ToastUtil.error("textMenu:分享文本为空")
                finish()
            } else {
                UiUtil.mdSelector(this, "要干嘛", listOf("分享", "保存"))
                        .cancelListener {
                            ToastUtil.warning(R.string.text_canceled_by_user)
                            finish()
                        }
                        .itemsCallback { _, _, i, _ ->
                            when (i) {
                                0 -> share(text)
                                1 -> {
                                    UiUtil.mdSelector(this, "保存到哪里", listOf("保存到本地", "保存到收藏夹")).cancelListener {
                                        ToastUtil.warning(R.string.text_canceled_by_user)
                                        finish()
                                    }.cancelListener {
                                        ToastUtil.warning(R.string.text_canceled_by_user)
                                        finish()
                                    }.itemsCallback { _, _, ii, _ ->
                                        when (ii) {
                                            0 -> startActivity<Share2SaveActivity>("type" to "text", "text" to text)
                                            1 -> startActivity<FavoritesActivity>("type" to "text", "text" to text)
                                            else -> {
                                                ToastUtil.error("未知错误")
                                            }
                                        }
                                        finish()
                                    }.show()
                                }
                                else -> {
                                    ToastUtil.error("未知错误")
                                    finish()
                                }
                            }
                        }
                        .show()

            }
        }

    }

    private fun imageMenu() {

    }

    private fun videoMenu() {

    }
}
