package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wx.android.common.util.ClipboardUtils
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.*
import com.zhihaofans.androidbox.util.JsoupUtil
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_bilibili.*
import kotlinx.android.synthetic.main.content_bilibili.*
import okhttp3.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*


//import org.xml.sax
class BilibiliActivity : AppCompatActivity() {
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    private val g = Gson()
    private val sysUtil = SystemUtil()
    private var defaultVid: String = "17027625"
    private var defaultPart: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilibili)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val listData = mutableListOf(
                "视频弹幕查用户uid",
                "视频封面下载"
        )
        listView_bilibili.adapter = sysUtil.listViewAdapter(this,  listData)
        listView_bilibili.onItemClick { _, _, index, _ ->
            if (defaultVid.startsWith("av")) {
                defaultVid = defaultVid.substring(2, defaultVid.length - 1)
            }
            when (index) {
                0 -> bilibiliCommentHash2uid()
                1 -> getVideoCoverUri()
            }
        }
        checkShare()
    }

    private fun checkShare() {
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st.isNullOrEmpty()) {
                toast("你分享了个空白链接")
            } else {
                Logger.d("url:$st")
                val urlCheck = sysUtil.getUrlFromBiliShare(st)
                Logger.d("urlCheck:$urlCheck")
                if (urlCheck == null) {
                    toast("你分享的不是链接")
                    finish()
                } else {
                    var urlPath = urlCheck.path
                    val urlQuery = urlCheck.query
                    when (urlCheck.scheme.toLowerCase()) {
                        "http", "https" -> {
                            when (urlCheck.host) {
                                "www.bilibili.com", "m.bilibili.com", "bilibili.com" -> {
                                    Logger.d(urlPath)
                                    if (!urlPath.startsWith("/")) {
                                        urlPath = "/$urlPath"
                                    }
                                    val _a = urlPath.split("/")
                                    if (_a.size != 3) {
                                        toast("链接格式错误$urlCheck")
                                        finish()
                                    } else {
                                        if (_a[1] != "video" && _a[2].isNotEmpty()) {
                                            toast("链接格式错误$urlCheck")
                                            finish()
                                        } else {
                                            defaultVid = _a[2]
                                            if (urlQuery.isNotEmpty()) {
                                                var querys = mutableListOf<String>()
                                                if (urlQuery.indexOf("&") < 0) {
                                                    querys.add(urlQuery)
                                                } else {
                                                    querys = urlPath.split("&").toMutableList()
                                                }
                                                querys.map {
                                                    val _b = it.toLowerCase().split("=")
                                                    if (_b.size == 2 && _b[0] == "p") {
                                                        val _part = _b[2].toIntOrNull()
                                                        if (_part != null && _part > 0) {
                                                            defaultPart = _part
                                                        }
                                                        return@map
                                                    }
                                                }
                                            }
                                            toast("vid:$defaultVid\npart:$defaultPart")
                                        }
                                    }
                                }
                            }
                        }
                        "bilibili" -> {
                            if (urlQuery.isNotEmpty()) {
                                var querys = mutableListOf<String>()
                                if (urlQuery.indexOf("&") < 0) {
                                    querys.add(urlQuery)
                                } else {
                                    querys = urlPath.split("&").toMutableList()
                                }
                                querys.map {
                                    val _b = it.toLowerCase().split("=")
                                    if (_b.size == 2 && _b[0] == "av") {
                                        if (_b[2].isNotEmpty()) {
                                            defaultVid = _b[2]
                                            defaultPart = 1
                                            toast("vid:$defaultVid\npart:$defaultPart")
                                        }
                                        return@map
                                    }
                                }
                            } else {
                                toast("链接格式错误$urlCheck")
                                finish()
                            }
                        }
                        else -> {
                            toast("链接格式错误$urlCheck")
                            finish()
                        }
                    }
                }
            }
        } else {
            Logger.i("分享失败")
        }
    }

    private fun bilibiliCommentHash2uid() {
        //弹幕用户hash查用户id
        alert("仅输入av后面的数字", "视频id") {
            customView {
                verticalLayout {
                    textView("id:")
                    val input = editText(defaultVid)
                    textView("Part:")
                    val input1 = editText(defaultPart.toString())
                    input.inputType = InputType.TYPE_CLASS_NUMBER
                    input1.inputType = InputType.TYPE_CLASS_NUMBER
                    yesButton {
                        sysUtil.closeKeyborad(this@BilibiliActivity)
                        if (input.text.isNullOrEmpty() || input1.text.isNullOrEmpty()) {
                            Snackbar.make(coordinatorLayout_bilibili, "请输入视频id和第几P", Snackbar.LENGTH_SHORT).show()
                        } else if (input1.text.toString().toInt() <= 0) {
                            Snackbar.make(coordinatorLayout_bilibili, "Part必须大于0", Snackbar.LENGTH_SHORT).show()
                        } else {
                            defaultVid = input.text.toString()
                            defaultPart = input1.text.toString().toIntOrNull() ?: 1
                            val videoPartCidUrl = "https://biliquery.typcn.com/api/cid/$defaultVid/$defaultPart"
                            val client = OkHttpClient()
                            val loadingProgressBar_cid = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                            loadingProgressBar_cid.setCancelable(false)
                            loadingProgressBar_cid.setCanceledOnTouchOutside(false)
                            loadingProgressBar_cid.show()
                            request.url(videoPartCidUrl).header("Content-Type", "application/json; charset=utf-8")
                            Logger.d("videoPartCidUrl:$videoPartCidUrl")
                            val call_cid = client.newCall(request.build())
                            call_cid.enqueue(object : Callback {
                                override fun onFailure(_call: Call, e: IOException) {
                                    runOnUiThread {
                                        loadingProgressBar_cid.dismiss()
                                        Snackbar.make(coordinatorLayout_bilibili, "获取失败", Snackbar.LENGTH_SHORT).show()
                                    }
                                    e.printStackTrace()
                                }

                                @Throws(IOException::class)
                                override fun onResponse(_call: Call, _response: Response) {
                                    val resBody_cid = _response.body()
                                    runOnUiThread {
                                        loadingProgressBar_cid.dismiss()
                                    }
                                    if (resBody_cid != null) {
                                        val responseStr_cid = resBody_cid.string()
                                        if (responseStr_cid.isEmpty()) {
                                            runOnUiThread {
                                                Snackbar.make(coordinatorLayout_bilibili, "获取视频cid失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            runOnUiThread {
                                                Logger.d(responseStr_cid)
                                                val loadingProgressBar_comment = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                                                loadingProgressBar_comment.setCancelable(false)
                                                loadingProgressBar_comment.setCanceledOnTouchOutside(false)
                                                loadingProgressBar_comment.show()
                                                val bilibiliVideoGson = g.fromJson(responseStr_cid, BilibiliVideoGson::class.java) as BilibiliVideoGson
                                                if (bilibiliVideoGson.error == 0) {
                                                    if (bilibiliVideoGson.cid.isNullOrEmpty()) {
                                                        loadingProgressBar_comment.dismiss()
                                                        Snackbar.make(coordinatorLayout_bilibili, "获取cid失败,空白结果", Snackbar.LENGTH_SHORT).show()

                                                    } else {
                                                        val commentUrl = "https://comment.bilibili.com/${bilibiliVideoGson.cid}.xml"
                                                        doAsync {
                                                            val doc: Document = Jsoup.connect(commentUrl).get()
                                                            uiThread {
                                                                //Logger.d(doc.html())
                                                                val body: Element = Jsoup.parseBodyFragment(doc.html())
                                                                val comments: Elements = body.getElementsByTag("d")
                                                                fun _search(_msg: String = "") {
                                                                    alert(_msg, "搜索弹幕") {
                                                                        customView {
                                                                            verticalLayout {
                                                                                val input_search = editText("")
                                                                                input_search.setSingleLine(true)
                                                                                okButton {
                                                                                    sysUtil.closeKeyborad(this@BilibiliActivity)
                                                                                    val search_key: String = input_search.text.toString()
                                                                                    if (search_key.isEmpty()) {
                                                                                        _search("请输入搜索内容")
                                                                                    } else {
                                                                                        val searchResult = mutableListOf<Element>()
                                                                                        comments.map {
                                                                                            if (it.hasAttr("p") && it.html().indexOf(search_key) >= 0) {
                                                                                                searchResult.add(it)
                                                                                            }
                                                                                        }
                                                                                        if (searchResult.size == 0) {
                                                                                            _search("空白搜索结果")
                                                                                        } else {
                                                                                            selector("选择你想查看的弹幕", searchResult.map { it.html() }) { dialogInterface, i ->
                                                                                                val thisComment: Element = searchResult[i]
                                                                                                val commentStr = thisComment.html()
                                                                                                val commentAttr = thisComment.attr("p")
                                                                                                val commentAttrList = commentAttr.split(",")
                                                                                                Logger.d("outerHtml:${thisComment.outerHtml()}")
                                                                                                Logger.d("commentAttrList:$commentAttrList")
                                                                                                if (commentAttrList.size != 8) {
                                                                                                    alert(thisComment.outerHtml(), "弹幕解析失败").show()
                                                                                                } else {
                                                                                                    val userHash = commentAttrList[commentAttrList.size - 2]
                                                                                                    uHash2uid(userHash, client)
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }.show()

                                                                }
                                                                runOnUiThread {

                                                                    loadingProgressBar_comment.dismiss()
                                                                    if (comments.size == 0) {
                                                                        Snackbar.make(coordinatorLayout_bilibili, "空白弹幕列表", Snackbar.LENGTH_SHORT).show()
                                                                    } else {
                                                                        _search()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        runOnUiThread {
                                            Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回结果出现代码错误", Snackbar.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })

                        }
                    }
                }
                noButton { }
            }
        }.show()
    }

    private fun uHash2uid(userHash: String, client: OkHttpClient) {
        //用户hash转换成用户Id
        val loadingProgressBar_hash = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
        loadingProgressBar_hash.setCancelable(false)
        loadingProgressBar_hash.setCanceledOnTouchOutside(false)
        loadingProgressBar_hash.show()
        val userHashUrl = "https://biliquery.typcn.com/api/user/hash/$userHash"
        request.url(userHashUrl).header("Content-Type", "application/json; charset=utf-8")
        Logger.d("userHash:$userHash")
        Logger.d("userHashUrl:$userHashUrl")
        val call_hash = client.newCall(request.build())
        call_hash.enqueue(object : Callback {
            override fun onFailure(_call: Call, e: IOException) {
                runOnUiThread {
                    loadingProgressBar_hash.dismiss()
                    Snackbar.make(coordinatorLayout_bilibili, "获取失败", Snackbar.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(_call: Call, _response: Response) {
                val resBody_hash = _response.body()
                runOnUiThread {
                    loadingProgressBar_hash.dismiss()
                }

                if (resBody_hash != null) {
                    val responseStr_hash = resBody_hash.string()
                    if (responseStr_hash.isEmpty()) {
                        runOnUiThread {
                            Snackbar.make(coordinatorLayout_bilibili, "获取视频cid失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        val bilibiliDanmuGetHashGson = g.fromJson(responseStr_hash, BilibiliDanmuGetHashGson::class.java) as BilibiliDanmuGetHashGson
                        if (bilibiliDanmuGetHashGson.error == 0) {
                            val data = bilibiliDanmuGetHashGson.data
                            if (data.size == 0) {
                                runOnUiThread {
                                    Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回结果列表空白", Snackbar.LENGTH_SHORT).show()
                                }
                            } else if (data.size == 1) {
                                bilibiliCommentHash2uidJx(data, client)
                            } else {
                                runOnUiThread {
                                    Logger.e("获取uid失败，服务器返回结果太多，解析失败\n$data")
                                    val act_uids = mutableListOf<String>()
                                    data.map {
                                        act_uids.add(it.id.toString())
                                    }
                                    selector("服务器返回uid结果太多，请选择要解析的uid", act_uids) { _, index ->
                                        bilibiliCommentHash2uidJx(mutableListOf(data[index]), client)
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(coordinatorLayout_bilibili, "获取uid失败，服务器返回错误代码(${bilibiliDanmuGetHashGson.error})", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Snackbar.make(coordinatorLayout_bilibili, "获取视频cid失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    private fun bilibiliCommentHash2uidJx(data: MutableList<BilibiliDanmuGetHashItemGson>, client: OkHttpClient) {
        sysUtil.closeKeyborad(this)
        runOnUiThread {
            val uid = data[0].id
            val acts = mutableListOf(
                    "获取用户信息"
            )
            selector("获取成功，用户uid为$uid", acts) { _, index ->
                when (index) {
                    0 -> {
                        val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                        loadingProgressBar.setCancelable(false)
                        loadingProgressBar.setCanceledOnTouchOutside(false)
                        loadingProgressBar.show()
                        val uInfoUrl = "https://api.bilibili.com/x/web-interface/card?mid=$uid&type=json"
                        request.url(uInfoUrl).header("Content-Type", "application/json;charset=utf-8")
                        Logger.d("url:$uInfoUrl")
                        val call = client.newCall(request.build())
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                runOnUiThread {
                                    loadingProgressBar.dismiss()
                                    Snackbar.make(coordinatorLayout_bilibili, "获取用户信息失败", Snackbar.LENGTH_SHORT).show()
                                }
                                e.printStackTrace()
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                val resBody = response.body()
                                var responseStr = ""
                                runOnUiThread {
                                    loadingProgressBar.dismiss()
                                    if (resBody != null) {
                                        doAsync {
                                            responseStr = resBody.string()
                                            uiThread {
                                                if (responseStr.isEmpty()) {
                                                    Snackbar.make(coordinatorLayout_bilibili, "获取用户信息失败，服务器返回空白结果", Snackbar.LENGTH_SHORT).show()

                                                } else {
                                                    val bilibiliUserInfoResultGson = g.fromJson(responseStr, BilibiliUserInfoResultGson::class.java) as BilibiliUserInfoResultGson
                                                    if (bilibiliUserInfoResultGson.code == 0) {
                                                        val userCard: BilibiliUserInfoResultCardGson = bilibiliUserInfoResultGson.data.card
                                                        if (userCard.mid != uid.toString()) {
                                                            Snackbar.make(coordinatorLayout_bilibili, "发生错误，所请求的id与服务器返回用户id不同(请求id:$uid|返回id:${userCard.mid}|返回用户昵称:${userCard.name})", Snackbar.LENGTH_SHORT).show()

                                                        } else {
                                                            val acts_result = mutableListOf(
                                                                    "昵称:${userCard.name}",
                                                                    "uid:${userCard.mid}",
                                                                    "性别:${userCard.sex}",
                                                                    "头像地址:${userCard.face}",
                                                                    "关注:${userCard.attention}",
                                                                    "粉丝:${userCard.fans}",
                                                                    "等级:${userCard.level_info.current_level}"
                                                            )
                                                            selector("", acts_result) { _, index_a ->
                                                                when (index_a) {
                                                                    0, 1, 2, 6 -> {
                                                                        copy(acts_result[index_a])
                                                                    }
                                                                    3 -> {
                                                                        selector("", mutableListOf(
                                                                                getString(R.string.text_open),
                                                                                getString(R.string.text_copy),
                                                                                getString(R.string.text_share)
                                                                        )) { _, index_b ->
                                                                            when (index_b) {
                                                                                0 -> sysUtil.browse(this@BilibiliActivity, userCard.face)
                                                                                1 -> copy(userCard.face)
                                                                                2 -> share(userCard.face)
                                                                            }
                                                                        }
                                                                    }
                                                                    4 -> {
                                                                        if (userCard.attention > 0) {
                                                                            val followers: MutableList<String> = userCard.attentions.map { it.toString() }.toMutableList()
                                                                            selector("uid按关注倒序显示", followers) { _, index_c ->
                                                                                sysUtil.browse(this@BilibiliActivity, "https://space.bilibili.com/${followers[index_c]}")
                                                                            }
                                                                        } else {
                                                                            Snackbar.make(coordinatorLayout_bilibili, "Ta没有关注", Snackbar.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                    5 -> {
                                                                        if (userCard.fans > 0) {
                                                                            Snackbar.make(coordinatorLayout_bilibili, "暂不支持查看粉丝列表", Snackbar.LENGTH_SHORT).show()
                                                                        } else {
                                                                            Snackbar.make(coordinatorLayout_bilibili, "Ta没有粉丝", Snackbar.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    } else {
                                                        Snackbar.make(coordinatorLayout_bilibili, "返回错误(${bilibiliUserInfoResultGson.message})", Snackbar.LENGTH_SHORT).show()

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        })

                    }
                }
            }
        }
    }

    private fun getVideoCoverUri() {
        alert("仅输入av后面的数字", "视频id") {
            customView {
                verticalLayout {
                    textView("id:")
                    val input = editText(defaultVid)
                    input.inputType = InputType.TYPE_CLASS_NUMBER
                    yesButton {
                        sysUtil.closeKeyborad(this@BilibiliActivity)
                        val vid = input.text.toString()
                        if (vid.isEmpty()) {
                            Snackbar.make(coordinatorLayout_bilibili, "请输入视频id", Snackbar.LENGTH_SHORT).show()
                        } else {
                            defaultVid = vid
                            //TODO:Bilibili Video Cover
                            val loadingProgressBar = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")
                            loadingProgressBar.setCancelable(false)
                            loadingProgressBar.setCanceledOnTouchOutside(false)
                            loadingProgressBar.show()
                            doAsync {
                                val coverUri: String? = getVideoCoverUriJx(defaultVid, 1)
                                uiThread {
                                    if (coverUri.isNullOrEmpty()) {
                                        loadingProgressBar.dismiss()
                                        Snackbar.make(coordinatorLayout_bilibili, "获取视频封面失败,返回地址空白", Snackbar.LENGTH_SHORT).show()
                                    } else {
                                        doAsync {
                                            val imageBitmap = sysUtil.getBitmapFromURL(coverUri.toString())
                                            uiThread {
                                                loadingProgressBar.dismiss()
                                                Snackbar.make(coordinatorLayout_bilibili, "获取视频封面成功", Snackbar.LENGTH_SHORT).show()
                                                selector("获取视频封面成功", listOf(
                                                        getString(R.string.text_open), getString(R.string.text_copy), getString(R.string.text_share)
                                                )) { _, i ->
                                                    when (i) {
                                                        0 -> sysUtil.browse(this@BilibiliActivity, coverUri.toString(), "av$defaultVid")
                                                        1 -> {
                                                            ClipboardUtils.copy(this@BilibiliActivity, coverUri.toString())
                                                            Snackbar.make(coordinatorLayout_bilibili, "复制成功", Snackbar.LENGTH_SHORT).show()
                                                        }
                                                        2 -> share(coverUri.toString())
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                noButton { }
            }
        }.show()
    }

    private fun getVideoCoverUriJx(vid: String, type: Int): String? {
        var videoCover: String? = null
        var html: String? = null
        val g = Gson()
        val client = OkHttpClient()
        when (type) {
            0 -> {
                try {
                    val response = client.newCall(Request.Builder().get().url("https://www.bilibili.com/video/$vid/").build()).execute()
                    html = response.body()!!.string()
                    Logger.d(html)
                    if (html.isNullOrEmpty()) {
                        return null
                    }
                    val ju = JsoupUtil(Jsoup.parse(html))
                    videoCover = ju.attr("head > meta[itemprop=\"image\"]", "content")
                    if (videoCover.isEmpty()) videoCover = ju.attr("head > meta[property=\"og:image\"]", "content")
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            1 -> {
                try {
                    val url = "https://www.galmoe.com/t.php?aid=$vid"
                    Logger.d(url)
                    val response = client.newCall(Request.Builder().get().url(url).build()).execute()
                    Logger.d("code:${response.code()}")
                    if (response.isSuccessful) {
                        val body = response.body()
                        Logger.d(body)
                        val rejson: String? = body?.string()
                        Logger.d(rejson)
                        val biliBiliGalmoeGson = g.fromJson(rejson, BiliBiliGalmoeGson::class.java)
                        if (biliBiliGalmoeGson.result == 1) videoCover = biliBiliGalmoeGson.url
                    } else {
                        Logger.e("code:${response.code()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Logger.e("error:$e")
                }

            }
            else -> return null
        }
        if (!videoCover.isNullOrEmpty()) videoCover = sysUtil.urlAutoHttps(videoCover, true)
        Logger.d(videoCover)
        return videoCover
    }

    private fun copy(string: String) {//复制到剪切板
        ClipboardUtils.copy(this@BilibiliActivity, string)
        Snackbar.make(coordinatorLayout_bilibili, "已复制到剪切板", Snackbar.LENGTH_SHORT).show()
    }
}
