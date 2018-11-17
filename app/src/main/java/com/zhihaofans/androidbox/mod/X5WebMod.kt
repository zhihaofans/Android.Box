package com.zhihaofans.androidbox.mod

import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-16 15:44

 */
class X5WebMod(x5Web: WebView) {
    private val x5WebView = x5Web
    fun init() {
        x5WebView.apply {
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
        }
    }

    fun loadUrl(url: String) = x5WebView.loadUrl(url)
    fun getWebView() = x5WebView
    fun getNowUrl(): String = x5WebView.url
}