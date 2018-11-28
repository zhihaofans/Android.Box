package com.zhihaofans.androidbox.kotlinEx

import android.graphics.Bitmap
import java.io.FileOutputStream

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-26 00:52

 */
fun Bitmap.saveFile(savePath: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): Boolean {
    return this.compress(format, 100, FileOutputStream(savePath))
}