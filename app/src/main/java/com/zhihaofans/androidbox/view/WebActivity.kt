package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.isNotNullAndEmpty
import com.zhihaofans.androidbox.mod.X5WebMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.content_web.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.share
import org.jetbrains.anko.toast

class WebActivity : AppCompatActivity() {
    private var webUrl: String? = null
    private var webTitle: String? = null
    private val x5Web = X5WebMod(webView)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        setSupportActionBar(toolbar_web)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab_web.setOnClickListener {
            val menuList = listOf<String>(
                    getString(R.string.text_refresh),
                    getString(R.string.text_share),
                    getString(R.string.text_open_in_other_browser)
            )
            selector("Menu", menuList) { _: DialogInterface, i: Int ->
                when (i) {
                    0 -> webView.reload()
                    1 -> share(webView.url)
                    2 -> SystemUtil.chromeCustomTabs(this, x5Web.getNowUrl())
                }
            }
        }
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        linearLayout_webview.removeView(webView)
        webView.destroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {
        x5Web.init()
        try {
            if (intent.extras !== null) {
                webUrl = intent.extras!!.getString("url", null)
                webTitle = intent.extras!!.getString("title", null)
                if (webUrl.isNullOrEmpty()) {
                    toast("url is null or empty")
                    finish()
                } else {
                    x5Web.loadUrl(webUrl!!)
                    if (webTitle.isNotNullAndEmpty()) {
                        this@WebActivity.title = webTitle
                    }
                }
            } else {
                toast("intent.extras == null")
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Try to get web uri fail.")
            finish()
        }
    }


}
