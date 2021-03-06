package com.zhihaofans.androidbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.LogUtil
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.intOrNull
import io.zhihao.library.android.kotlinEx.string
import io.zhihao.library.android.util.ClipboardUtil
import io.zhihao.library.android.util.RandomUtil
import kotlinx.android.synthetic.main.activity_random.*
import kotlinx.android.synthetic.main.content_random.*


class RandomActivity : AppCompatActivity() {
    private val xuiUtil = XUIUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)
        setSupportActionBar(toolbar_random)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val list = listOf(
                "随机数", "多选一", "多选多"
        )
        listViewRandom.init(list)
        listViewRandom.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    xuiUtil.materialDialogInputIntSigned("请输入最小数", "", "", "", getString(R.string.text_yes),
                            getString(R.string.text_cancel)).apply {
                        inputRange(1, -1)
                        onPositive { dialogMin, _ ->
                            val inputTextMin = dialogMin.inputEditText!!.string.toIntOrNull()
                            xuiUtil.materialDialogInputIntSigned("请输入最大数", "必须大于$inputTextMin", "", "", getString(R.string.text_yes),
                                    getString(R.string.text_cancel)).apply {
                                inputRange(1, -1)
                                onPositive { dialogMax, _ ->
                                    val inputTextMax = dialogMax.inputEditText!!.string.toIntOrNull()
                                    LogUtil.d("inputTextMin:$inputTextMin\ninputTextMax:$inputTextMax")
                                    if (inputTextMin == null) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "最小数为null")
                                    } else if (inputTextMax == null) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "最大数为null")
                                    } else {
                                        if (inputTextMax > inputTextMin) {
                                            val randomResult = RandomUtil.getInt(inputTextMin, inputTextMax)
                                            xuiUtil.materialDialogInputIntSigned("结果", "", randomResult.toString(), randomResult.toString(), getString(R.string.text_copy),
                                                    getString(R.string.text_cancel)).apply {
                                                inputRange(1, -1)
                                                onPositive { dialogR, whichR ->
                                                    ClipboardUtil.copy(dialogR.inputEditText!!.string)
                                                }
                                            }.show()
                                        } else {
                                            xuiUtil.snackbarDanger(coordinatorLayout_random, "最小数不能大于最大数")
                                        }
                                    }
                                }
                            }.show()
                        }
                    }.show()

                }
                1 -> {
                    xuiUtil.materialDialogInputString("请输入想要随机的文本列表", "用英文逗号分割", "", "", getString(R.string.text_yes),
                            getString(R.string.text_cancel)).apply {
                        inputRange(1, -1)
                        onPositive { dialogMin, _ ->
                            val inputTextList = dialogMin.inputEditText!!.string
                            LogUtil.d("inputTextList:$inputTextList")
                            if (inputTextList.isEmpty()) {
                                xuiUtil.snackbarDanger(coordinatorLayout_random, "文本列表为空")
                            } else {
                                val randomResult = RandomUtil.getStringItems(inputTextList.split(","), 1)
                                if (randomResult == null) {
                                    xuiUtil.snackbarDanger(coordinatorLayout_random, "返回结果为null")
                                } else {
                                    val resultStr = randomResult.string(",")
                                    xuiUtil.materialDialogInputString("结果", "", resultStr, resultStr, getString(R.string.text_copy),
                                            getString(R.string.text_cancel)).apply {
                                        inputRange(1, -1)
                                        onPositive { dialogR, _ ->
                                            ClipboardUtil.copy(dialogR.inputEditText!!.string)
                                        }
                                    }.show()
                                }

                            }
                        }
                    }.show()
                }
                2 -> {
                    xuiUtil.materialDialogInputString("请输入想要随机的文本列表", "用英文逗号分割", "", "", getString(R.string.text_yes),
                            getString(R.string.text_cancel)).apply {
                        inputRange(1, -1)
                        onPositive { dialogMin, _ ->
                            val inputTextList = dialogMin.inputEditText!!.string
                            xuiUtil.materialDialogInputInt("请输入想要随机的文本列表", "用英文逗号分割", "", "1", getString(R.string.text_yes),
                                    getString(R.string.text_cancel)).apply {
                                inputRange(1, -1)
                                onPositive { dialogMin, _ ->
                                    val inputTextLength = dialogMin.inputEditText!!.intOrNull
                                    LogUtil.d("inputTextList:$inputTextList")
                                    if (inputTextList.isEmpty()) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "文本列表为空")
                                    } else if (inputTextLength == null) {
                                        xuiUtil.snackbarDanger(coordinatorLayout_random, "随机次数为null")
                                    } else {
                                        val randomResult = RandomUtil.getStringItems(inputTextList.split(","), inputTextLength)
                                        if (randomResult == null) {
                                            xuiUtil.snackbarDanger(coordinatorLayout_random, "返回结果为null")
                                        } else {
                                            val resultStr = randomResult.string(",")
                                            xuiUtil.materialDialogInputString("结果", "", resultStr, resultStr, getString(R.string.text_copy),
                                                    getString(R.string.text_cancel)).apply {
                                                inputRange(1, -1)
                                                onPositive { dialogR, _ ->
                                                    ClipboardUtil.copy(dialogR.inputEditText!!.string)
                                                }
                                            }.show()
                                        }

                                    }
                                }
                            }.show()

                        }
                    }.show()
                }
                else -> xuiUtil.snackbarDanger(coordinatorLayout_random, "未知错误")
            }
        }
    }

}
