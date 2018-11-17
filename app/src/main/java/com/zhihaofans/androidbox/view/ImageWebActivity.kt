package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.adapter.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_image_web.*
import kotlinx.android.synthetic.main.content_image_web.*


class ImageWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_web)
        setSupportActionBar(toolbar_image_web)

        fab_image_web.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        init()
    }

    private fun init() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = RecyclerViewAdapter(this, createData())
    }

    private fun createData(): List<Int> {
        val data = ArrayList<Int>()
        for (i in 0..20) {
            data.add(R.mipmap.ic_launcher)
        }
        return data
    }

}
