package com.zhihaofans.androidbox.util

import android.widget.Toast
import com.orhanobut.logger.Logger
import dev.utils.app.AppUtils
import dev.utils.app.toast.ToastTintUtils

/**

 * @author: zhihaofans

 * @date: 2019-02-25 22:16

 */
class ToastUtil {
    companion object {
        fun success(text: String = "") = ToastTintUtils.success(text)
        fun error(text: String = "", logger: Boolean = true) {
            ToastTintUtils.error(text)
            if (logger) Logger.e(text)
        }

        fun info(text: String = "") = ToastTintUtils.info(text)
        fun normal(text: String = "") = ToastTintUtils.normal(text)
        fun warning(text: String = "") = ToastTintUtils.warning(text)

        fun success(text: Int) = ToastUtil.success(AppUtils.getString(text))
        fun error(text: Int, logger: Boolean = true) = ToastUtil.error(AppUtils.getString(text), logger)
        fun info(text: Int) = ToastUtil.info(AppUtils.getString(text))
        fun normal(text: Int) = ToastUtil.normal(AppUtils.getString(text))
        fun warning(text: Int) = ToastUtil.warning(AppUtils.getString(text))

        fun successLong(text: String = "") = ToastTintUtils.success(text, Toast.LENGTH_LONG)
        fun errorLong(text: String = "", logger: Boolean = true) {
            ToastTintUtils.error(text, Toast.LENGTH_LONG)
            if (logger) Logger.e(text)
        }

        fun infoLong(text: String = "") = ToastTintUtils.info(text, Toast.LENGTH_LONG)
        fun normalLong(text: String = "") = ToastTintUtils.normal(text, Toast.LENGTH_LONG)
        fun warningLong(text: String = "") = ToastTintUtils.warning(text, Toast.LENGTH_LONG)

        fun successLong(text: Int) = ToastUtil.successLong(AppUtils.getString(text))
        fun errorLong(text: Int, logger: Boolean = true) = ToastUtil.errorLong(AppUtils.getString(text), logger)


        fun infoLong(text: Int) = ToastUtil.infoLong(AppUtils.getString(text))
        fun normalLong(text: Int) = ToastUtil.normalLong(AppUtils.getString(text))
        fun warningLong(text: Int) = ToastUtil.warningLong(AppUtils.getString(text))
    }
}