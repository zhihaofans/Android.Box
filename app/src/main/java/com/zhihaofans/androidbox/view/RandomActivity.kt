package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.copy
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.string
import com.zhihaofans.androidbox.util.RandomUtil
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
                    xuiUtil.materialDialogInput4IntSigned("请输入最小数", "", "", "", getString(R.string.text_yes),
                            getString(R.string.text_cancel)).apply {
                        inputRange(1, -1)
                        onPositive { dialogMin, _ ->
                            val inputTextMin = dialogMin.inputEditText!!.string.toIntOrNull()
                            xuiUtil.materialDialogInput4IntSigned("请输入最大数", "必须大于$inputTextMin", "", "", getString(R.string.text_yes),
                                    getString(R.string.text_cancel)).apply {
                                inputRange(1, -1)
                                onPositive { dialogMax, _ ->
                                    val inputTextMax = dialogMax.inputEditText!!.string.toIntOrNull()
                                    Logger.d("inputTextMin:$inputTextMin\ninputTextMax:$inputTextMax")
                                    if (inputTextMin == null) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "最小数为null").show()
                                    } else if (inputTextMax == null) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "最大数为null").show()
                                    } else {
                                        if (inputTextMax > inputTextMin) {
                                            val randomResult = RandomUtil.getInt(inputTextMin, inputTextMax)
                                            xuiUtil.materialDialogInput4IntSigned("结果", "", randomResult.toString(), randomResult.toString(), getString(R.string.text_copy),
                                                    getString(R.string.text_cancel)).apply {
                                                inputRange(1, -1)
                                                onPositive { dialogR, whichR ->
                                                    copy(dialogR.inputEditText!!.string)
                                                }
                                            }.show()
                                        } else {
                                            xuiUtil.snackbarDanger(coordinatorLayout_random, "最小数不能大于最大数").show()
                                        }
                                    }
                                }
                            }.show()
                        }
                    }.show()

                }
                1 -> {
                    xuiUtil.materialDialogInput4String("请输入想要随机的文本列表", "用英文逗号分割", "", "", getString(R.string.text_yes),
                            getString(R.string.text_cancel)).apply {
                        inputRange(1, -1)
                        onPositive { dialogMin, _ ->
                            val inputTextList = dialogMin.inputEditText!!.string
                            Logger.d("inputTextList:$inputTextList")
                            if (inputTextList.isEmpty()) {
                                xuiUtil.snackbarDanger(coordinatorLayout_random, "文本列表为空").show()
                            } else {
                                val randomResult = RandomUtil.getStringItems(inputTextList.split(","), 1)
                                if (randomResult == null) {
                                    xuiUtil.snackbarDanger(coordinatorLayout_random, "返回结果为null").show()
                                } else {
                                    xuiUtil.materialDialogInput4String("结果", "", randomResult.toString(), randomResult.toString(), getString(R.string.text_copy),
                                            getString(R.string.text_cancel)).apply {
                                        inputRange(1, -1)
                                        onPositive { dialogR, _ ->
                                            copy(dialogR.inputEditText!!.string)
                                        }
                                    }.show()
                                }

                            }
                        }
                    }.show()
                }
                else -> xuiUtil.snackbarDanger(coordinatorLayout_random, "未知错误").show()
            }
        }
    }

}
