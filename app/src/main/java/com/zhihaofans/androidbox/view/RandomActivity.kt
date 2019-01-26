package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.util.XUIUtil
import kotlinx.android.synthetic.main.activity_random.*
import kotlinx.android.synthetic.main.content_random.*


class RandomActivity : AppCompatActivity() {
    private val xuiUtil = XUIUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
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
                    xuiUtil.materialDialogInput4Int("请输入最小数", "", "", "", "OK",
                            "NO").apply {
                        inputRange(1, -1)
                        onPositive { dialog, which ->
                            val inputTextMin = dialog.inputEditText!!.text.toString()
                            xuiUtil.materialDialogInput4Int("请输入最大数", "必须大于$inputTextMin", "", "", "OK",
                                    "NO").apply {
                                inputRange(1, -1)
                                onPositive { dialogM, whichM ->
                                    val inputTextMax = dialogM.inputEditText!!.text.toString()
                                    if (inputTextMax < inputTextMin) {

                                    }
                                }
                            }
                        }
                    }.show()

                }
            }
        }
    }

}
