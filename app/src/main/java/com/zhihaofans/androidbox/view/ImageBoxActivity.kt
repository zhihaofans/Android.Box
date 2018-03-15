package com.zhihaofans.androidbox.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.ImageBoxItemAdapter
import com.zhihaofans.androidbox.data.ImageBoxDataModel
import kotlinx.android.synthetic.main.activity_image_box.*
import kotlinx.android.synthetic.main.content_image_box.*


class ImageBoxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_box)
        setSupportActionBar(toolbar)
        imageListLoad(this@ImageBoxActivity)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun initData(): MutableList<ImageBoxDataModel> {
        val data = mutableListOf<ImageBoxDataModel>()
        val itemList = mutableListOf(
                mutableListOf("png", "https://httpbin.org/image/png"),
                mutableListOf("jpeg", "https://httpbin.org/image/jpeg"),
                mutableListOf("webp", "https://httpbin.org/image/webp")
        )
        itemList.map {
            data.add(ImageBoxDataModel(it[0], Uri.parse(it[1])))
        }
        return data
    }

    fun imageListLoad(context: Context) {
        recyclerView_image_box.setHasFixedSize(true) // 设置固定大小
        recyclerView_image_box.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView_image_box.adapter = ImageBoxItemAdapter(initData())
        //initItemDecoration(recyclerView); // 初始化装饰
        recyclerView_image_box.itemAnimator = DefaultItemAnimator()// 默认动画
    }
}
