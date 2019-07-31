package com.zhihaofans.androidbox.util

import android.R
import android.app.Dialog
import android.content.Context
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog


/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-24 15:17

 */
class UiUtil {
    companion object {
        fun mdSelector(context: Context, title: String, list: List<String>) = MaterialDialog.Builder(context).title(title).items(list)
        fun awesomeInfo(mContext: Context, title: String, message: String? = null, buttonText: String = mContext.getString(R.string.ok), cancelable: Boolean = true): Dialog? {
            return AwesomeInfoDialog(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                    .setCancelable(cancelable)
                    .setPositiveButtonText(buttonText)
                    .show()
        }

        fun awesomeError(mContext: Context, title: String, message: String? = null, buttonText: String = mContext.getString(R.string.ok), cancelable: Boolean = true): Dialog? {
            return AwesomeErrorDialog(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    //.setDialogIconAndColor(R.drawable.error, R.color.white)
                    .setCancelable(cancelable)
                    .setButtonText(buttonText)
                    .show()
        }

        fun awesomeWarning(mContext: Context, title: String, message: String? = null, positiveButtonText: String = mContext.getString(R.string.ok), cancelable: Boolean = true): Dialog? {
            return AwesomeInfoDialog(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                    .setCancelable(cancelable)
                    .setPositiveButtonText(positiveButtonText)
                    .show()
        }

        fun awesomeSuccess(mContext: Context, title: String, message: String? = null, positiveButtonText: String = mContext.getString(R.string.ok), cancelable: Boolean = true): Dialog? {
            return AwesomeInfoDialog(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                    .setCancelable(cancelable)
                    .setPositiveButtonText(positiveButtonText)
                    .show()
        }
    }
}