package com.zhihaofans.androidbox.util

import com.orhanobut.logger.Logger
import io.zhihao.library.android.util.AppUtil

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-08-12 11:49

 */
class LogUtil {
    companion object {
        fun d(any: Any?) {
            if (AppUtil.isDebug()) Logger.d(any)
        }

        fun e(string: String) {
            if (AppUtil.isDebug()) Logger.e(string)
        }

        fun i(string: String) {
            if (AppUtil.isDebug()) Logger.i(string)
        }
    }
}