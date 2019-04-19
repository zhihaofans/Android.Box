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
import com.zhihaofans.androidbox.mod.TophubMod
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.removeAllItems
import kotlinx.android.synthetic.main.activity_multiple_item_use.rv_list
import kotlinx.android.synthetic.main.content_tophub.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TophubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        /*
        setContentView(R.layout.activity_tophub)
        setSupportActionBar(toolbar_tophub)
        listview_tophub.visibility = View.INVISIBLE
        init()
        fab_tophub.setOnClickListener {
            //init()
        }
        */
        newInit()
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
        listview_tophub.removeAllItems()
        this@TophubActivity.title = "Loading..."
        doAsync {
            try {
                val homePage = TophubMod.getHomePage()
                uiThread {
                    if (homePage == null) {
                        ToastUtil.error("加载主页失败")
                    } else {
                        listViewHomePageInit(homePage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.error("加载出错")
            }
        }
    }

    private fun listViewHomePageInit(tophubHomepage: TophubHomepage) {
        val resultList = tophubHomepage.groupList
        if (resultList.isNullOrEmpty()) {
            ToastUtil.error("获取主页失败，服务器返回空白数据")
        } else {
            val siteList = mutableListOf<TophubHomepageGroupItem>()
            resultList.map { group ->
                group.items.map { item ->
                    siteList.add(item)
                }
            }
            listview_tophub.init(siteList.map { it.title })
            listview_tophub.setOnItemClickListener { _, _, position, _ ->
                listview_tophub.removeAllItems()
                this@TophubActivity.title = "Loading..."
                listViewSiteInit(siteList[position])
            }
            ToastUtil.success("加载主页完毕")
        }

        this@TophubActivity.title = getString(R.string.title_activity_tophub)
    }

    private fun listViewSiteInit(groupItem: TophubHomepageGroupItem) {
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
                            browse(hotList[position].url)
                        }
                        ToastUtil.success("加载主页完毕")
                    }
                }
            }
        }
    }
}
