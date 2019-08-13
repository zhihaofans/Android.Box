package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.tencent.mmkv.MMKV
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.MultipleItemQuickAdapter
import com.zhihaofans.androidbox.data.DataServer
import com.zhihaofans.androidbox.data.TophubHomepage
import com.zhihaofans.androidbox.data.TophubHomepageGroupItem
import com.zhihaofans.androidbox.data.TophubModCategoryData
import com.zhihaofans.androidbox.kotlinEx.close
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.mod.SettingMod
import com.zhihaofans.androidbox.mod.TophubMod
import com.zhihaofans.androidbox.util.LogUtil
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.removeAllItems
import io.zhihao.library.android.kotlinEx.snackbar
import io.zhihao.library.android.kotlinEx.string
import io.zhihao.library.android.util.AppUtil
import kotlinx.android.synthetic.main.activity_multiple_item_use.*
import kotlinx.android.synthetic.main.activity_tophub.*
import kotlinx.android.synthetic.main.content_tophub.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class TophubActivity : AppCompatActivity() {
    private val xui = XUIUtil(this)
    private val tophubMod = TophubMod()
    private var nowType = tophubMod.NOW_TYPE_HOMEPAGE
    private var nowSite: TophubHomepageGroupItem? = null
    private var nowCategory: TophubModCategoryData? = null
    private var nowCategoryPage: Int? = null
    private var hasPage = false
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        backKeyListen()
    }

    private fun backKeyListen() {
        if (nowType == tophubMod.NOW_TYPE_HOMEPAGE) finish() else loading()
    }

    private fun newInit() {
        setContentView(R.layout.activity_multiple_item_use)
        title = "MultipleItem Use"
        val data = DataServer.getMultipleItemData()
        val multipleItemAdapter = MultipleItemQuickAdapter(data)
        val manager = GridLayoutManager(this, 4)
        rv_list.layoutManager = manager
        multipleItemAdapter.setSpanSizeLookup { _, position -> data[position].spanSize }
        rv_list.adapter = multipleItemAdapter
    }

    private fun init() {
        setContentView(R.layout.activity_tophub)
        setSupportActionBar(toolbar_tophub)
        val rootDir = MMKV.initialize(this)
        LogUtil.d("mmkv root: $rootDir")
        loading()
        fab_tophub_refresh.setOnClickListener {
            fab_tophub.close()
            when (nowType) {
                tophubMod.NOW_TYPE_HOMEPAGE -> loading()
                tophubMod.NOW_TYPE_CATEGORY -> {
                    if (nowCategory == null) {
                        nowType = tophubMod.NOW_TYPE_HOMEPAGE
                    } else {
                        initCategory(nowCategory!!)
                    }
                }
                tophubMod.NOW_TYPE_SITE -> {
                    if (nowSite == null) {
                        nowType = tophubMod.NOW_TYPE_HOMEPAGE
                    } else {
                        initSite(nowSite!!)
                    }
                }
            }
        }
        fab_tophub_homepage.setOnClickListener {
            fab_tophub.close()
            loading()
        }
        fab_tophub_category.setOnClickListener {
            fab_tophub.close()
            val categoryList = tophubMod.getCategoryList()
            if (categoryList.isEmpty()) {
                ToastUtil.error("加载失败，分类列表为空白")
                nowCategory = null
            } else {
                xui.selector(R.string.text_category, categoryList.map { it.title }).itemsCallback { _, _, which, _ ->
                    listview_tophub.removeAllItems()
                    this@TophubActivity.title = "Loading..."
                    initCategory(categoryList[which])
                }.show()
            }
        }
        fab_tophub_page.setOnClickListener {
            fab_tophub.close()
            if (hasPage) {
                when {
                    nowCategory !== null -> {
                        xui.materialDialogInputInt(getString(R.string.text_page), "必须大于0", nowCategoryPage.toString(), nowCategoryPage.toString(), getString(R.string.text_yes),
                                getString(R.string.text_cancel)).apply {
                            inputRange(1, -1)
                            onPositive { dialogMax, _ ->
                                try {
                                    val input = dialogMax.inputEditText!!.string.toIntOrNull()
                                    if (input !== null && input > 0) {
                                        initCategory(nowCategory!!, input)
                                    } else {
                                        ToastUtil.error("未知错误")
                                        hasPage = false
                                    }
                                } catch (e: Exception) {
                                    ToastUtil.error("未知异常")
                                    hasPage = false
                                }
                            }
                        }.show()
                    }
                    else -> {
                        ToastUtil.error("不支持更改页码啊")
                        hasPage = false
                    }
                }
            } else {
                ToastUtil.error("不支持更改页码")
            }
        }
        fab_tophub_login.setOnClickListener {
            if (tophubMod.libs.isLogin()) {
                Snackbar.make(coordinatorLayout_tophub, "已登录，是否清除登录记录?", Snackbar.LENGTH_SHORT).setAction("清除") {
                    coordinatorLayout_tophub.snackbar("注销" + tophubMod.libs.logout().string("成功", "失败"))
                }
            } else {
                MaterialDialog(this).show {
                    input { _: MaterialDialog, char: CharSequence ->
                        message(text = "itc_center_user")
                        if (char.isEmpty()) {
                            ToastUtil.error("请输入cookies")
                        } else {
                            if (tophubMod.libs.loginByCookies(char.toString())) {
                                ToastUtil.success("成功保存登录信息")
                            } else {
                                ToastUtil.error("保存失败登录信息")
                            }
                        }
                    }
                    positiveButton(R.string.text_login)
                }
            }
        }
    }

    private fun loading() {
        hasPage = false
        listview_tophub.removeAllItems()
        this@TophubActivity.title = "Loading..."
        nowType = tophubMod.NOW_TYPE_HOMEPAGE
        nowCategory = null
        nowSite = null
        doAsync {
            try {
                val homePage = tophubMod.getHomePage()
                uiThread {
                    if (homePage == null) {
                        ToastUtil.error("加载主页失败")
                        this@TophubActivity.title = getString(R.string.title_activity_tophub)
                    } else {
                        initHomePage(homePage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiThread {
                    ToastUtil.error("加载出错")
                    this@TophubActivity.title = getString(R.string.title_activity_tophub)
                }
            }
        }
    }

    private fun initHomePage(tophubHomepage: TophubHomepage) {
        hasPage = false
        val resultList = tophubHomepage.groupList
        if (resultList.isNullOrEmpty()) {
            ToastUtil.error("获取主页失败，服务器返回空白数据")
            this@TophubActivity.title = getString(R.string.title_activity_tophub)
        } else {
            val siteList = mutableListOf<TophubHomepageGroupItem>()
            resultList.map { group ->
                group.items.map { item ->
                    siteList.add(item)
                }
            }
            listview_tophub.init(siteList.map { it.title })
            listview_tophub.setOnItemClickListener { _, _, position, _ ->
                fab_tophub.close()
                initSite(siteList[position])
            }
            this@TophubActivity.title = getString(R.string.title_activity_tophub)
            ToastUtil.success("加载主页完毕")
        }
    }


    private fun initCategory(category: TophubModCategoryData, page: Int = 1) {
        hasPage = true
        if (category.url.isEmpty()) {
            ToastUtil.error("获取分类内容失败，地址为空白")
        } else {
            val siteList = mutableListOf<TophubHomepageGroupItem>()
            doAsync {
                val categoryContent = tophubMod.getCategoryContent(category.url, page)
                uiThread {
                    if (categoryContent == null) {
                        ToastUtil.error("获取主页失败，服务器返回空白数据")
                    } else {
                        if (categoryContent.item.isEmpty()) {
                            nowCategory = null
                            ToastUtil.error("获取主页失败，服务器返回空白列表")
                        } else {
                            categoryContent.item.map {
                                siteList.add(TophubHomepageGroupItem(it.title, it.url, it.icon))
                            }
                            listview_tophub.init(siteList.map { it.title })
                            listview_tophub.setOnItemClickListener { _, _, position, _ ->
                                fab_tophub.close()
                                initSite(siteList[position])
                            }
                            nowCategory = category
                            nowCategoryPage = page
                            this@TophubActivity.title = category.title
                            ToastUtil.success("加载${categoryContent.title}第 $page 页完毕,${categoryContent.subtitle}")
                        }
                    }
                }
            }
        }
    }

    private fun initSite(chooseSite: TophubHomepageGroupItem) {
        hasPage = false
        xui.selector(chooseSite.title, listOf("热门", "历史")).itemsCallback { _, _, which, _ ->
            listview_tophub.removeAllItems()
            this@TophubActivity.title = "Loading..."
            when (which) {
                0 -> initSiteList(chooseSite, false)
                1 -> initSiteList(chooseSite, true)
            }
            nowSite = chooseSite
            nowType = tophubMod.NOW_TYPE_SITE
        }.show()
    }

    private fun initSiteList(groupItem: TophubHomepageGroupItem, history: Boolean) {
        nowCategory = null
        nowSite = null
        doAsync {
            val siteContent = tophubMod.getWebSite(groupItem.url)
            uiThread {
                if (siteContent == null) {
                    ToastUtil.error("获取站点失败，服务器返回空白数据")
                } else {
                    val resultList = if (history) {
                        siteContent.historyList
                    } else {
                        siteContent.hotList
                    }
                    if (resultList.isNullOrEmpty()) {
                        ToastUtil.error("获取站点内容失败，服务器返回空白列表")
                    } else {
                        this@TophubActivity.title = siteContent.title
                        listview_tophub.removeAllItems()
                        listview_tophub.init(resultList.map { it.title })
                        listview_tophub.setOnItemClickListener { _, _, position, _ ->
                            fab_tophub.close()
                            browseWeb(resultList[position].url)
                        }
                        ToastUtil.success("加载主页完毕")
                    }
                }
            }
        }
    }

    private fun initDashboard(groupItem: TophubHomepageGroupItem, history: Boolean) {
        nowCategory = null
        nowSite = null
        doAsync {
            val siteContent = tophubMod.getWebSite(groupItem.url)
            uiThread {
                if (siteContent == null) {
                    ToastUtil.error("获取站点失败，服务器返回空白数据")
                } else {
                    val resultList = if (history) {
                        siteContent.historyList
                    } else {
                        siteContent.hotList
                    }
                    if (resultList.isNullOrEmpty()) {
                        ToastUtil.error("获取站点内容失败，服务器返回空白列表")
                    } else {
                        this@TophubActivity.title = siteContent.title
                        listview_tophub.removeAllItems()
                        listview_tophub.init(resultList.map { it.title })
                        listview_tophub.setOnItemClickListener { _, _, position, _ ->
                            fab_tophub.close()
                            browseWeb(resultList[position].url)
                        }
                        ToastUtil.success("加载主页完毕")
                    }
                }
            }
        }
    }

    private fun browseWeb(url: String, defaultBrowser: Boolean = false) {
        val newUrl = urlRedirect(url)
        try {
            if (SettingMod.loadBooleanSetting("ACTIVITY_TOPHUB_BROWSER_LYNKET") == true && !defaultBrowser) {
                val pn = "arun.com.chromer"
                if (AppUtil.isAppInstalled(pn)) {
                    if (OtherAppMod.browserByLynket(newUrl)) {
                        ToastUtil.success("启动成功")
                    } else {
                        ToastUtil.error("启动失败")
                        browseWeb(newUrl, true)
                    }
                } else {
                    ToastUtil.error("启动失败,未安装$pn")
                    browseWeb(newUrl, true)
                }
            } else {
                if (browse(newUrl)) {
                    ToastUtil.success("启动成功")
                } else {
                    ToastUtil.error("启动失败")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("启动失败,发现应用异常")
        }
    }

    private fun urlRedirect(url: String): String {
        return if (url.isEmpty()) {
            url
        } else {
            try {
                val mURL = URL(url)
                urlRedirect(mURL).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                url
            }
        }
    }

    private fun urlRedirect(url: URL): URL {
        val redirectList = mutableMapOf(
                "www.zhihu.com" to "www.zhihuvvv.com"
        )
        val newHost = if (redirectList[url.host] == null) {
            url.host
        } else {
            redirectList[url.host]
        }
        return URL(url.protocol, newHost, url.port, url.file)
    }

    private fun importCookies(value: String) {
        tophubMod.libs.loginByCookies(value)
    }
}
