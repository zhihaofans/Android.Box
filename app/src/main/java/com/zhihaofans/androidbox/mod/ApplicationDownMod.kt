package com.zhihaofans.androidbox.mod

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lxj.androidktx.core.isNetworkConnected
import com.zhihaofans.androidbox.util.HttpUtil
import dev.utils.app.PathUtils
import dev.utils.common.FileUtils
import io.zhihao.library.android.util.AndroidUtil
import io.zhihao.library.android.util.FileUtil
import io.zhihao.library.android.util.NetworkUtil
import org.jsoup.Jsoup
import java.io.IOException


/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-06-23 00:51

 */
class ApplicationDownMod(context: Context) {
    private val mContext = context
    private val cachePath = PathUtils.getInternalAppCodeCacheDir() + "/ApplicationDown/"
    val buckets = Buckets(mContext, cachePath)
    val cache = Cache(cachePath)
    val urlList = UrlList()
    fun getGooglePlayVersion(packageName: String, language: String = "en"): String? {
        return try {
            if (NetworkUtil.isConnected()) {
                Jsoup.connect("https://play.google.com/store/apps/details?id=$packageName&hl=$language")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".IxB2fe .hAyfc:nth-child(4) .htlgb span")[0]
                        .ownText()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}

class Cache(cachePath: String) {
    private val g = Gson()
    //private val cachePath = PathUtils.getInternalAppCodeCacheDir() + "/ApplicationDown/"
    val pathList = getPathList(cachePath)
    private val bucketSourceUrl = "https://github.com/zhihaofans/Android-ApplicationDown/raw/master/buckets.json"

    fun getPathList(cachePath: String): PathList {
        return PathList(
                cachePath, "$cachePath/buckets/", "$cachePath/buckets.json"
        )
    }

    // Bucket Source

    fun getBucketSource(): Map<String, String>? {
        val jsonString = if (FileUtil.isFileExists(pathList.bucketSourceCacheFilePath)) {
            (FileUtils.getFile(pathList.bucketSourceCacheFilePath) ?: return null).toString()
        } else {
            return null
        }
        return g.fromJson(jsonString, object : TypeToken<Map<String, String>>() {}.type)
    }

    fun setBucketSource(jsonString: String): Boolean {
        return setBucketSource(g.toJson(jsonString, object : TypeToken<Map<String, String>>() {}.type))
    }

    fun setBucketSource(cacheData: Map<String, String>): Boolean {
        return when {
            cacheData.isEmpty() -> false
            else -> if (FileUtil.createFolder(pathList.bucketSourceCacheFilePath)) {
                val dataString = g.toJson(cacheData, object : TypeToken<Map<String, String>>() {}.type)
                FileUtil.saveFile(pathList.bucketSourceCacheFilePath, dataString)
            } else {
                false
            }
        }
    }

    fun hasBucketSource(): Boolean {
        return FileUtil.isFileExists(pathList.bucketSourceCacheFilePath)
    }
}

class Buckets(mContext: Context, cachePath: String) {
    private val g = Gson()
    private val bucketsCachePath = "$cachePath/buckets/"
    private val bucketSourceCachePath = "$cachePath/buckets.json"
    fun getBucketDataFromOnline(bucketName: String) {

    }

    fun getBucketSource(cache: Boolean = true): Map<String, String> {
        val jsonString = if (this.hasBucketSourceCache()) {
            (FileUtils.getFile(bucketSourceCachePath) ?: return mapOf()).toString()
        } else {
            if (AndroidUtil.isNetworkConnected()) {
                HttpUtil.httpGetString(bucketSourceUrl) ?: return mapOf()
            } else {
                mapOf()
            }
        }
        return g.fromJson(jsonString, object : TypeToken<Map<String, String>>() {}.type)
    }

    fun saveBucketSource(cacheData: Map<String, String>): Boolean {
        return if (FileUtil.createFolder(bucketSourceCachePath)) {
            val dataString = g.toJson(cacheData, object : TypeToken<Map<String, String>>() {}.type)
            FileUtil.saveFile(bucketSourceCachePath, dataString)
        } else {
            false
        }
    }

    fun hasBucketSourceCache(): Boolean {
        return FileUtil.isFileExists(bucketSourceCachePath)
    }
}

data class UrlList(
        val bucketSource: String = "https://github.com/zhihaofans/Android-ApplicationDown/raw/master/buckets.json"
)

data class PathList(
        val cachePath: String,
        val bucketsCachePath: String,
        val bucketSourceCacheFilePath: String
)