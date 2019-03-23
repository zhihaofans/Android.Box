package com.zhihaofans.androidbox.mod

import android.content.Intent
import com.zhihaofans.androidbox.kotlinEx.*
import java.net.URL

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-22 21:29

 */
class OpenerMod(intent: Intent) {
    private val mIntent = intent
    fun isUrl(): Boolean {
        return when {
            mIntent.isActionView -> true
            mIntent.isTypeTextPlain -> mIntent.getTextPlain().isUrl()
            else -> false
        }
    }

    fun getUrl(): URL? {
        val mText = mIntent.getTextPlain()
        return if (mText.isNullOrEmpty()) null else mText.toUrl()
    }

    class UrlOpener(url: URL) {
        private val mUrl = url
        fun isBilibili(): Boolean {
            val bilibiliHost = listOf(
                    "bilibili.com"
            )
            return bilibiliHost.indexOf(mUrl.host) >= 0
        }

    }
}