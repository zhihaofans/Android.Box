package com.zhihaofans.androidbox.util

import android.content.Context
import android.text.InputType
import android.view.View
import com.xuexiang.xui.utils.SnackbarUtils
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R


/**
 * Created by zhihaofans on 2019/1/25.
 */
class XUIUtil(context: Context) {
    private val mContext = context
    fun materialDialogInput4Int(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput4Int(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInput(icon: Int?, title: String, content: String, inputType: Int, inputHint: String, inputPreFill: String, positiveButton: String,
                            neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return MaterialDialog.Builder(mContext)
                .iconRes(icon ?: R.mipmap.ic_launcher)
                .title(title)
                .content(content)
                .inputType(inputType)
                .input(
                        inputHint,
                        inputPreFill,
                        false,
                        ({ dialog, input -> })
                )
                .positiveText(positiveButton)
                .negativeText(neativeButton)
                .cancelable(cancelable)
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