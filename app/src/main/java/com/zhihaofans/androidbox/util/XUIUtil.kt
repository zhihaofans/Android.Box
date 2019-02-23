package com.zhihaofans.androidbox.util

import android.content.Context
import android.text.InputType
import android.view.View
import com.xuexiang.xui.utils.SnackbarUtils
import com.xuexiang.xui.utils.WidgetUtils
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.isNotNullAndEmpty


/**
 * Created by zhihaofans on 2019/1/25.
 */
class XUIUtil(context: Context) {
    // Wiki: https://github.com/xuexiangjys/XUI/wiki/
    private val mContext = context

    private fun materialDialogBuilder() = MaterialDialog.Builder(mContext)
    //文本
    fun materialDialogInput4String(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                   neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_TEXT, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput4String(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                   neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_TEXT, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    //非符号整数
    fun materialDialogInput4Int(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_NUMBER, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput4Int(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_NUMBER, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    //符号整数
    fun materialDialogInput4IntSigned(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                      neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput4IntSigned(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                      neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput(icon: Int?, title: String, content: String, inputType: Int, inputHint: String, inputPreFill: String, positiveText: String,
                            negativeText: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogBuilder()
                .iconRes(icon ?: R.mipmap.ic_launcher)
                .title(title)
                .content(content)
                .inputType(inputType)
                .input(inputHint, inputPreFill, false, ({ _, _ -> }))
                .positiveText(positiveText)
                .negativeText(negativeText)
                .cancelable(cancelable)
    }


    fun materialDialogLoadingDialog(title: String? = null, icon: Int? = null): LoadingDialog {
        return WidgetUtils.getLoadingDialog(mContext).apply {
            if (title.isNotNullAndEmpty()) setTitle(title)
            if (icon !== null) {
                setLoadingIcon(icon)
                setIconScale(0.4F)
            }
            setLoadingSpeed(8)
        }
    }

    fun materialDialog(title: String? = null, content: String? = null, positiveText: String = "OK",
                       negativeText: String? = null, icon: Int? = null): MaterialDialog.Builder {
        return materialDialogBuilder().apply {
            if (icon !== null) iconRes(icon)
            if (title.isNotNullAndEmpty()) title(title!!)
            if (icon !== null) content(content!!)
            positiveText(positiveText)
            if (title.isNotNullAndEmpty()) negativeText(negativeText!!)
        }
    }
/*
DEMO
    fun materialDialogInput() {
        MaterialDialog.Builder(getContext())
                .iconRes(R.drawable.icon_warning)
                .title(R.string.tip_warning)
                .content(R.string.content_warning)
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(
                        getString(R.string.hint_please_input_password),
                        "",
                        false,
                        ({ dialog, input -> Toast(input.toString()) }))
                .inputRange(3, 5)
                .positiveText(R.string.lab_continue)
                .negativeText(R.string.lab_change)
                .onPositive(({ dialog, which -> Toast("你输入了:" + dialog.getInputEditText()!!.getText().toString()) }))
                .cancelable(false)
                .show()
    }
    */

    fun snackbar(view: View, message: String): SnackbarUtils {
        return SnackbarUtils.Short(view, message)
    }

    fun snackbarDanger(view: View, message: String): SnackbarUtils {
        return snackbar(view, message).danger()
    }

    fun snackbarLong(view: View, message: String): SnackbarUtils {
        return SnackbarUtils.Short(view, message)
    }

    fun snackbarLongDanger(view: View, message: String): SnackbarUtils {
        return snackbar(view, message).danger()
    }

}