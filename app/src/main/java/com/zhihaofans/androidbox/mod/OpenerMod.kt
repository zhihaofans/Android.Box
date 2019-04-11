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
        return when {
            mIntent == null -> false
            mIntent!!.isActionView -> true
            mIntent!!.isTypeTextPlain -> mIntent!!.getTextPlain().isUrl()
            mIntent!!.isActionProcessText -> mIntent!!.getProcessText().isUrl()
            else -> false
        }
    }

    fun getIntent(): Intent? = mIntent
    fun getUrl(): URL? {
        return if (mIntent.isNull() || !isUrl()) {
            null
        } else {
            val mText = when {
                mIntent!!.isActionView -> mIntent!!.data.toURL()
                mIntent!!.isTypeTextPlain -> if (mIntent!!.getTextPlain().isNullOrEmpty()) {
                    null
                } else {
                    mIntent!!.getTextPlain()!!.toUrl()
                }
                mIntent!!.isActionProcessText -> if (mIntent!!.getProcessText().isNullOrEmpty()) {
                    null
                } else {
                    mIntent!!.getProcessText()!!.toUrl()
                }
                else -> null
            }
            if (mText.isNull()) null else mText
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