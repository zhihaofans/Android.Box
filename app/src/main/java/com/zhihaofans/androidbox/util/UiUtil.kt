package com.zhihaofans.androidbox.util

import android.content.Context
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-03-24 15:17

 */
class UiUtil {
    companion object {
        fun mdSelector(context: Context, title: String, list: List<String>) = MaterialDialog.Builder(context).title(title).items(list)
    }
}