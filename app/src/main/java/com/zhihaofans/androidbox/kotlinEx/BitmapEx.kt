package com.zhihaofans.androidbox.kotlinEx

import android.graphics.Bitmap
import com.orhanobut.logger.Logger
import dev.utils.common.FileUtils
import java.io.File
import java.io.FileOutputStream

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-26 00:52

 */
fun Bitmap.saveFile(savePath: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): Boolean {
    val file = File(savePath)
    val dir = file.parent
    return if (FileUtils.isFileExists(dir)) {
        try {
            this.compress(format, 100, FileOutputStream(savePath))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    } else {
        false
    }
}