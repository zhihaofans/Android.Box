package com.zhihaofans.androidbox.mod

import android.content.Intent
import com.zhihaofans.androidbox.kotlinEx.toURL
import io.zhihao.library.android.kotlinEx.*
import java.net.URL

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-22 21:29

 */
class OpenerMod {
    private var mIntent: Intent? = null
    fun init(intent: Intent) {
        this.mIntent = intent
    }

    fun isInitFinished() = mIntent !== null
    fun isUrl(): Boolean {
        val tf = when {
            mIntent == null -> false
            mIntent!!.isActionView -> true
            mIntent!!.isTypeTextPlain -> mIntent!!.getTextPlain().isUrl()
            else -> false
        }
        return tf
    }

    fun getIntent(): Intent? = mIntent
    fun getUrl(): URL? {
        return if (mIntent.isNull()) {
            null
        } else {
            val mText = mIntent!!.getTextPlain()
            if (mText.isNullOrEmpty()) mIntent!!.data.toURL() else mText.toUrl()
        }
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