package com.zhihaofans.androidbox.mod

import java.net.URL

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-09-28 22:52

 */
class UrlRedirectMod {
    companion object {
        fun urlRedirect(url: String): URL = urlRedirect(URL(url))
        fun urlRedirect(url: URL): URL {
            val redirectList = mutableMapOf(
                    "www.zhihu.com" to "www.zhihuvvv.com",
                    "sspai.com" to "beta.sspai.com",
                    "www.sspai.com" to "beta.sspai.com"
            )
            val newHost = if (redirectList[url.host] == null) {
                url.host
            } else {
                redirectList[url.host]
            }
            return URL(url.protocol, newHost, url.port, url.file)
        }
    }
}