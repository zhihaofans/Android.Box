package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.MultipleItemQuickAdapter
import com.zhihaofans.androidbox.data.DataServer
import com.zhihaofans.androidbox.data.TophubHomepage
import com.zhihaofans.androidbox.data.TophubHomepageGroupItem
import com.zhihaofans.androidbox.mod.OtherAppMod
import com.zhihaofans.androidbox.mod.SettingMod
import com.zhihaofans.androidbox.mod.TophubMod
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.removeAllItems
import io.zhihao.library.android.util.AppUtil
import kotlinx.android.synthetic.main.activity_multiple_item_use.*
import kotlinx.android.synthetic.main.activity_tophub.*
import kotlinx.android.synthetic.main.content_tophub.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class TophubActivity : AppCompatActivity() {
    private val xui = XUIUtil(this)
    private var isHomepage = true
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
        if (isHomepage) finish() else loading()
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
        loading()
        fab_tophub.setOnClickListener {
            loading()
        }
    }

    private fun loading() {
        listview_tophub.removeAllItems()
        this@TophubActivity.title = "Loading..."
        isHomepage = true
        doAsync {
            try {
                val homePage = TophubMod.getHomePage()
                uiThread {
                    if (homePage == null) {
                        ToastUtil.error("加载主页失败")
                        this@TophubActivity.title = getString(R.string.title_activity_tophub)
                    } else {
                        listViewHomePageInit(homePage)
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

    private fun listViewHomePageInit(tophubHomepage: TophubHomepage) {
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
                val chooseSite = siteList[position]
                xui.selector(chooseSite.title, listOf("热门", "历史")).itemsCallback { _, _, which, _ ->
                    listview_tophub.removeAllItems()
                    this@TophubActivity.title = "Loading..."
                    when (which) {
                        0 -> listViewSiteHotInit(chooseSite)
                        1 -> listViewSiteHistoryInit(chooseSite)
                    }
                    isHomepage = false
                }.show()
            }
            this@TophubActivity.title = getString(R.string.title_activity_tophub)
            ToastUtil.success("加载主页完毕")
        }
    }

    private fun listViewSiteHotInit(groupItem: TophubHomepageGroupItem) {
        doAsync {
            val siteContent = TophubMod.getWebSite(groupItem.url)
            uiThread {
                if (siteContent == null) {
                    ToastUtil.error("获取站点失败，服务器返回空白数据")
                } else {
                    val hotList = siteContent.hotList
                    if (hotList.isNullOrEmpty()) {
                        ToastUtil.error("获取站点内容失败，服务器返回空白列表")
                    } else {
                        this@TophubActivity.title = siteContent.title
                        listview_tophub.removeAllItems()
                        listview_tophub.init(hotList.map { it.title })
                        listview_tophub.setOnItemClickListener { _, _, position, _ ->
                            browseWeb(hotList[position].url)
                        }
                        ToastUtil.success("加载主页完毕")
                    }
                }
            }
        }
    }

    private fun listViewSiteHistoryInit(groupItem: TophubHomepageGroupItem) {
        doAsync {
            val siteContent = TophubMod.getWebSite(groupItem.url)
            uiThread {
                if (siteContent == null) {
                    ToastUtil.error("获取站点失败，服务器返回空白数据")
                } else {
                    val historyList = siteContent.historyList
                    if (historyList.isNullOrEmpty()) {
                        ToastUtil.error("获取站点内容失败，服务器返回空白列表")
                    } else {
                        this@TophubActivity.title = siteContent.title
                        listview_tophub.removeAllItems()
                        listview_tophub.init(historyList.map { it.title })
                        listview_tophub.setOnItemClickListener { _, _, position, _ ->
                            browseWeb(historyList[position].url)
                        }
                        ToastUtil.success("加载主页完毕")
                    }
                }
            }
        }
    }

    private fun browseWeb(url: String) {
        try {
            if (SettingMod.loadBooleanSetting("ACTIVITY_TOPHUB_BROWSER_LYNKET") == true) {
                val pn = "arun.com.chromer"
                if (AppUtil.isAppInstalled(pn)) {
                    if (OtherAppMod.browserByLynket(url)) {
                        ToastUtil.success("启动成功")
                    } else {
                        ToastUtil.error("启动失败")
                    }
                } else {
                    ToastUtil.error("启动失败,未安装$pn")
                }
            } else {
                if (browse(url)) {
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
}
