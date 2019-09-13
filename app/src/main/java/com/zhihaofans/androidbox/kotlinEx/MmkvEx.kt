package com.zhihaofans.androidbox.kotlinEx

import com.tencent.mmkv.MMKV

/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-09-13 14:19

 */
fun Any.mmkv(id: String? = null) = if (id == null) MMKV.defaultMMKV() else MMKV.mmkvWithID(id)

fun MMKV.isExist(keyId: String): Boolean {
    this.allKeys().apply {
        return if (this.isEmpty()) {
            false
        } else {
            this.indexOf(keyId) >= 0
        }
    }
}

fun MMKV.isNotExist(keyId: String): Boolean = this.isExist(keyId)