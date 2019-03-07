package com.zhihaofans.androidbox.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.*
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.util.ClipboardUtil
import com.zhihaofans.androidbox.util.SystemUtil
import com.zhihaofans.androidbox.util.ToastUtil
import dev.utils.app.DialogUtils
import kotlinx.android.synthetic.main.activity_bilibili.*
import kotlinx.android.synthetic.main.content_bilibili.*
import okhttp3.*
import org.jetbrains.anko.*
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
    private var defaultVid: String = "17027625"
    private var defaultPart: Int = 1
    private var clipboardUtil: ClipboardUtil? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilibili)
        setSupportActionBar(toolbar_bilibili)
        clipboardUtil = ClipboardUtil(this@BilibiliActivity)
        fab_bilibili.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val listData = mutableListOf(
                "视频弹幕查用户uid", "小黑屋"
        )
        listView_bilibili.adapter = SystemUtil.listViewAdapter(this, listData)
        listView_bilibili.setOnItemClickListener { _, _, index, _ ->
            if (defaultVid.startsWith("av")) {
                defaultVid = defaultVid.substring(2, defaultVid.length - 1)
            }
            when (index) {
                0 -> bilibiliCommentHash2uid()
                1 -> bilibiliBlackroom()
                else -> ToastUtil.error("未知错误")
            }
        }
        checkShare()
    }

    private fun checkShare() {
        val intent = intent
        if (Objects.equals(Intent.ACTION_SEND, intent.action) && intent.type != null && Objects.equals("text/plain", intent.type)) {
            val st = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (st.isNullOrEmpty()) {
                ToastUtil.error("你分享了个空白链接")
            } else {
                Logger.d("url:$st")
                val urlCheck = SystemUtil.getUrlFromBiliShare(st)
                Logger.d("urlCheck:$urlCheck")
                if (urlCheck == null) {
                    ToastUtil.error("你分享的不是链接")
                    finish()
                } else {
                    var urlPath = urlCheck.path
                    val urlQuery = urlCheck.query
                    when (urlCheck.protocol.toLowerCase()) {
                        "http", "https" -> {
                            when (urlCheck.host) {
                                "www.bilibili.com", "m.bilibili.com", "bilibili.com" -> {
                                    Logger.d(urlPath)
                                    if (!urlPath.startsWith("/")) {
                                        urlPath = "/$urlPath"
                                    }
                                    val _a = urlPath.split("/")
                                    if (_a.size != 3) {
                                        ToastUtil.error("链接格式错误$urlCheck")
                                        finish()
                                    } else {
                                        if (_a[1] != "video" && _a[2].isNotEmpty()) {
                                            ToastUtil.error("链接格式错误$urlCheck")
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
                                            ToastUtil.info("vid:$defaultVid\npart:$defaultPart")
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
                                            ToastUtil.info("vid:$defaultVid\npart:$defaultPart")
                                        }
                                        return@map
                                    }
                                }
                            } else {
                                ToastUtil.error("链接格式错误$urlCheck")
                                finish()
                            }
                        }
                        else -> {
                            ToastUtil.error("链接格式错误$urlCheck")
                            finish()
                        }
                    }
                }
            }
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
                        SystemUtil.closeKeyborad(this@BilibiliActivity)
                        if (input.text.isNullOrEmpty() || input1.text.isNullOrEmpty()) {
                            Snackbar.make(coordinatorLayout_bilibili, "请输入视频id和第几P", Snackbar.LENGTH_SHORT).show()
                        } else if (input1.text.toString().toInt() <= 0) {
                            Snackbar.make(coordinatorLayout_bilibili, "Part必须大于0", Snackbar.LENGTH_SHORT).show()
                        } else {
                            defaultVid = input.text.toString()
                            defaultPart = input1.text.toString().toIntOrNull() ?: 1
                            val videoPartCidUrl = "https://biliquery.typcn.com/api/cid/$defaultVid/$defaultPart"
                            val client = OkHttpClient()
                            val loadingProgressBar_cid = DialogUtils.createProgressDialog(this@BilibiliActivity, "下载中...", "Please wait a bit…")
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
                                                                                    SystemUtil.closeKeyborad(this@BilibiliActivity)
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
        val loadingProgressBar_hash = DialogUtils.createProgressDialog(this, "下载中...", "Please wait a bit…")
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
        SystemUtil.closeKeyborad(this)
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
                                                                                0 -> SystemUtil.browse(this@BilibiliActivity, userCard.face)
                                                                                1 -> copy(userCard.face)
                                                                                2 -> share(userCard.face)
                                                                            }
                                                                        }
                                                                    }
                                                                    4 -> {
                                                                        if (userCard.attention > 0) {
                                                                            val followers: MutableList<String> = userCard.attentions.map { it.toString() }.toMutableList()
                                                                            selector("uid按关注倒序显示", followers) { _, index_c ->
                                                                                SystemUtil.browse(this@BilibiliActivity, "https://space.bilibili.com/${followers[index_c]}")
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

    private fun bilibiliBlackroom() {
        if (OtherAppMod.bilibiliBlackroom(this)) {
            ToastUtil.success("启动成功")
        } else {
            ToastUtil.error("启动失败")
        }
    }

    private fun copy(string: String) {//复制到剪切板
        clipboardUtil?.copy(string)
        Snackbar.make(coordinatorLayout_bilibili, "已复制到剪切板", Snackbar.LENGTH_SHORT).show()
    }
}
