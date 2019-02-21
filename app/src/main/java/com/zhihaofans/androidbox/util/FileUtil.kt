package com.zhihaofans.androidbox.util

import com.zhihaofans.androidbox.kotlinEx.remove
import dev.utils.common.FileUtils

/**
 * Created by zhihaofans on 2019/2/21.
 */
class FileUtil {
    companion object {
        fun saveFile(filePath: String, content: String): Boolean {
            val fileName = FileUtils.getFileName(filePath)
            val saveTo = filePath.remove(fileName)
            return FileUtils.saveFile(saveTo, fileName, content)
        }
    }
}