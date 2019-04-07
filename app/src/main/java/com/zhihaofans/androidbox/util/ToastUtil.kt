package com.zhihaofans.androidbox.util

import android.widget.Toast
import com.orhanobut.logger.Logger
import dev.utils.app.toast.ToastTintUtils
import io.zhihao.library.android.util.AppUtil

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

        fun success(text: Int) = ToastUtil.success(AppUtil.getString(text))
        fun error(text: Int, logger: Boolean = true) = ToastUtil.error(AppUtil.getString(text), logger)
        fun info(text: Int) = ToastUtil.info(AppUtil.getString(text))
        fun normal(text: Int) = ToastUtil.normal(AppUtil.getString(text))
        fun warning(text: Int) = ToastUtil.warning(AppUtil.getString(text))

        fun successLong(text: String = "") = ToastTintUtils.success(text, Toast.LENGTH_LONG)
        fun errorLong(text: String = "", logger: Boolean = true) {
            ToastTintUtils.error(text, Toast.LENGTH_LONG)
            if (logger) Logger.e(text)
        }

        fun infoLong(text: String = "") = ToastTintUtils.info(text, Toast.LENGTH_LONG)
        fun normalLong(text: String = "") = ToastTintUtils.normal(text, Toast.LENGTH_LONG)
        fun warningLong(text: String = "") = ToastTintUtils.warning(text, Toast.LENGTH_LONG)

        fun successLong(text: Int) = ToastUtil.successLong(AppUtil.getString(text))
        fun errorLong(text: Int, logger: Boolean = true) = ToastUtil.errorLong(AppUtil.getString(text), logger)


        fun infoLong(text: Int) = ToastUtil.infoLong(AppUtil.getString(text))
        fun normalLong(text: Int) = ToastUtil.normalLong(AppUtil.getString(text))
        fun warningLong(text: Int) = ToastUtil.warningLong(AppUtil.getString(text))
    }
}