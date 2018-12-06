package com.zhihaofans.androidbox.mod

import android.annotation.SuppressLint
import com.orhanobut.logger.Logger
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


/**
 * @author: zhihaofans

 * @date: 2018-11-16 15:44

 */
class X5WebMod {
    private var x5WebView: WebView? = null
    @SuppressLint("SetJavaScriptEnabled")
    fun init(x5Web: WebView) {
        x5WebView = x5Web
        x5WebView?.apply {
            // WebViewClient
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                    webView.loadUrl(url)
                    return true
                }

                override fun onReceivedSslError(p0: WebView, p1: SslErrorHandler, p2: SslError) {
                    super.onReceivedSslError(p0, p1, p2)
                    p1.proceed()
                }
            }
            // Setting
            val webSetting = settings
            webSetting.allowFileAccess = true
            webSetting.setAllowUniversalAccessFromFileURLs(true)
            webSetting.javaScriptEnabled = true
            webSetting.loadWithOverviewMode = true
            webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            webSetting.setSupportZoom(true)
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    Logger.d("onProgressChanged")
                }

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    Logger.d("onReceivedTitle")
                }
            }
        }
    }

    fun loadUrl(url: String) = x5WebView?.loadUrl(url)
    fun getWebView() = x5WebView
    fun getNowUrl() = x5WebView?.url
    fun canGoBack() = x5WebView?.canGoBack()
    fun goBack() = x5WebView?.goBack()
}