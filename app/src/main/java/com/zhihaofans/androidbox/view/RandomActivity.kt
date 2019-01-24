package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import kotlinx.android.synthetic.main.activity_random.*
import kotlinx.android.synthetic.main.content_random.*

class RandomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val list = listOf(
                "随机数", "多选一", "多选多"
        )
        listViewRandom.init(this, list)
        listViewRandom.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {

                }
            }
        }
    }

}
