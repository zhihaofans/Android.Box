package com.zhihaofans.androidbox.mod

import com.zhihaofans.androidbox.data.TextInputItemData
import com.zhihaofans.androidbox.data.TextInputListData
import io.zhihao.library.android.util.EncodeUtil

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-05-21 21:51

 */
class TextMod {
    companion object {
        val mList = listOf(
                TextInputListData("UrlEncode", listOf(
                        TextInputItemData("输入文本", "string")
                ))
        )

        fun getUrlEncode(string: String, encode: Boolean = true): String {
            return when {
                string.isEmpty() -> string
                encode -> EncodeUtil.urlEncode(string) ?: string
                else -> EncodeUtil.urlDecode(string) ?: string
            }
        }
    }
}