package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.zhihaofans.androidbox.data.UpdateData
import com.zhihaofans.androidbox.kotlinEx.fromWebGetJson
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.AppUtils
import dev.utils.common.FileUtils
import org.jetbrains.anko.browse

/**
 * @author: zhihaofans

 * @date: 2018-11-12 19:42

 */
class ZhihaofansMod {
    private var mContext: Context? = null
    private val g = Gson()

    fun init(context: Context) {
        this.mContext = context
    }

    inner class Update {
        private var updateData: UpdateData? = null
        private val updateUrl = "https://android-1251125924.cos.ap-guangzhou.myqcloud.com/" +
                "com.zhihaofans.androidbox/update.json"

        fun copyUpdateFileToLocal(): Boolean {
            if (mContext == null) return false
            return try {
                val file = mContext!!.assets.open("update.json")
                FileUtils.copyFile(
                        file,
                        SystemUtil.getAppPrivateDirectory(mContext!!) + "files/update.json",
                        true
                )
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun checkUpdate(): Boolean? {
            updateData = g.fromWebGetJson(updateUrl, UpdateData::class.java)
            return if (updateData == null) {
                null
            } else {
                updateData!!.version.code > AppUtils.getAppVersionCode()
            }
        }

        fun getUpdateData(): UpdateData? = updateData
        fun openUpdateWeb() {
            if (mContext == null) throw Exception("Context is null, need user 'init()' to initialize")
            mContext!!.browse("")

        }
    }


}