package com.zhihaofans.androidbox.util

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.zhihaofans.androidbox.kotlinEx.materialDialog

/**

 * @author: zhihaofans

 * @date: 2019-01-03 23:26

 */
class MDAlertUtil {
    companion object {
        fun basic(mContext: Context, title: String, message: String): MaterialDialog {
            return mContext.materialDialog().show {
                title(text = title)
                message(text = message)
            }
        }

        fun basic(mContext: Context, title: Int, message: Int): MaterialDialog {
            return mContext.materialDialog().show {
                title(title)
                message(message)
            }
        }
    }
}