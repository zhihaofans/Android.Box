package com.zhihaofans.androidbox.util

import android.content.Context
import android.text.InputType
import android.view.View
import com.xuexiang.xui.utils.SnackbarUtils
import com.xuexiang.xui.utils.WidgetUtils
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.zhihaofans.androidbox.R
import io.zhihao.library.android.kotlinEx.isNotNullAndEmpty
import io.zhihao.library.android.util.AppUtil


/**
 * Created by zhihaofans on 2019/1/25.
 */
class XUIUtil(context: Context) {
    // Wiki: https://github.com/xuexiangjys/XUI/wiki/
    private val mContext = context

    private fun materialDialogBuilder() = MaterialDialog.Builder(mContext)
    //文本
    fun materialDialogInputString(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                  neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_TEXT, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInputString(title: String, content: String, positiveButton: String,
                                  neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_TEXT, "", "", positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInputString(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                  neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_TEXT, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    //不带符号整数
    fun materialDialogInputInt(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                               neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_NUMBER, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInputInt(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                               neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(icon, title, content, InputType.TYPE_CLASS_NUMBER, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    //带符号整数
    fun materialDialogInputIntSigned(title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
                                     neativeButton: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED, inputHint, inputPreFill, positiveButton, neativeButton, cancelable)
    }

    fun materialDialogInputIntSigned(icon: Int, title: String, content: String, inputHint: String, inputPreFill: String, positiveButton: String,
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

    fun materialDialogInput(title: String, content: String, inputType: Int, inputHint: String, inputPreFill: String, positiveText: String,
                            negativeText: String, cancelable: Boolean = false): MaterialDialog.Builder {
        return materialDialogInput(null, title, content, inputType, inputHint, inputPreFill, positiveText, negativeText, cancelable)
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
            if (content.isNotNullAndEmpty()) content(content!!)
            positiveText(positiveText)
            if (negativeText.isNotNullAndEmpty()) negativeText(negativeText!!)
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

    fun snackbar(view: View, message: String) {
        return SnackbarUtils.Short(view, message).show()
    }

    fun snackbarDanger(view: View, message: String) {
        return SnackbarUtils.Short(view, message).danger().show()
    }

    fun snackbarLong(view: View, message: String) {
        return SnackbarUtils.Long(view, message).show()
    }

    fun snackbarLongDanger(view: View, message: String) {
        return SnackbarUtils.Long(view, message).danger().show()
    }

    fun snackbar(view: View, message: Int) {
        return SnackbarUtils.Short(view, AppUtil.getString(message)).show()
    }

    fun snackbarDanger(view: View, message: Int) {
        return SnackbarUtils.Short(view, AppUtil.getString(message)).danger().show()
    }

    fun snackbarLong(view: View, message: Int) {
        return SnackbarUtils.Long(view, AppUtil.getString(message)).show()
    }

    fun snackbarLongDanger(view: View, message: Int) {
        return SnackbarUtils.Long(view, AppUtil.getString(message)).danger().show()
    }

    fun selector(title: String, items: List<String>): MaterialDialog.Builder {
        return MaterialDialog.Builder(mContext)
                .title(title)
                .items(items)
        //.itemsCallback({ dialog, view, which, text -> Toast(which + ": " + text) })
        //.show()
    }

    fun selector(title: Int, items: List<String>): MaterialDialog.Builder {
        return MaterialDialog.Builder(mContext)
                .title(title)
                .items(items)
        //.itemsCallback({ dialog, view, which, text -> Toast(which + ": " + text) })
        //.show()
    }
}