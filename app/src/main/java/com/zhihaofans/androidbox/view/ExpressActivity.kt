package com.zhihaofans.androidbox.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_express.*

class ExpressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_express)
        setSupportActionBar(toolbar_express)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun listViewMode(mode: Int, data: Any) {
        when (mode) {
            0 -> {
                // 所有快递

            }
            1 -> {
                // 查看某个快递

            }
        }
    }
}