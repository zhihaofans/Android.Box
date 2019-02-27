package com.zhihaofans.androidbox.util

import android.widget.Toast
import dev.utils.app.AppUtils
import dev.utils.app.toast.ToastTintUtils

/**

 * @author: zhihaofans

 * @date: 2019-02-25 22:16

 */
class ToastUtil {
    companion object {
        fun success(text: String) = ToastTintUtils.success(text)
        fun error(text: String) = ToastTintUtils.error(text)
        fun info(text: String) = ToastTintUtils.info(text)
        fun normal(text: String) = ToastTintUtils.normal(text)
        fun warning(text: String) = ToastTintUtils.warning(text)

        fun success(text: Int) = ToastTintUtils.success(AppUtils.getString(text))
        fun error(text: Int) = ToastTintUtils.error(AppUtils.getString(text))
        fun info(text: Int) = ToastTintUtils.info(AppUtils.getString(text))
        fun normal(text: Int) = ToastTintUtils.normal(AppUtils.getString(text))
        fun warning(text: Int) = ToastTintUtils.warning(AppUtils.getString(text))

        fun successLong(text: String) = ToastTintUtils.success(text, Toast.LENGTH_LONG)
        fun errorLong(text: String) = ToastTintUtils.error(text, Toast.LENGTH_LONG)
        fun infoLong(text: String) = ToastTintUtils.info(text, Toast.LENGTH_LONG)
        fun normalLong(text: String) = ToastTintUtils.normal(text, Toast.LENGTH_LONG)
        fun warningLong(text: String) = ToastTintUtils.warning(text, Toast.LENGTH_LONG)

        fun successLong(text: Int) = ToastTintUtils.success(AppUtils.getString(text), Toast.LENGTH_LONG)
        fun errorLong(text: Int) = ToastTintUtils.error(AppUtils.getString(text), Toast.LENGTH_LONG)
        fun infoLong(text: Int) = ToastTintUtils.info(AppUtils.getString(text), Toast.LENGTH_LONG)
        fun normalLong(text: Int) = ToastTintUtils.normal(AppUtils.getString(text), Toast.LENGTH_LONG)
        fun warningLong(text: Int) = ToastTintUtils.warning(AppUtils.getString(text), Toast.LENGTH_LONG)
    }
}