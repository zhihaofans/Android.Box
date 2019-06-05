package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import com.xuexiang.xui.XUI
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.TextMod
import com.zhihaofans.androidbox.util.ToastUtil
import com.zhihaofans.androidbox.util.XUIUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.string
import io.zhihao.library.android.util.ClipboardUtil
import kotlinx.android.synthetic.main.activity_text.*
import kotlinx.android.synthetic.main.content_text.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.share

class TextActivity : AppCompatActivity() {
    private val xuiUtil = XUIUtil(this)
    private val mList = TextMod.mList
    private val modTitleList = mList.map { it.title }
    private var hasChoose = false
    override fun onCreate(savedInstanceState: Bundle?) {
        XUI.initTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            if (hasChoose && listview_text.isNotEmpty()) {

            } else {
                ToastUtil.error("未完成")
                //chooseMod()
            }
        }
    }

    private fun chooseMod() {

        selector("", modTitleList) { _: DialogInterface, i: Int ->
            val showList = mutableListOf<String>()
            val inputList = mutableListOf<Any>()
            val mTool = mList[i]
            val mToolList = mTool.items
            if (mToolList.isEmpty()) {
                xuiUtil.snackbarDanger(coordinatorLayout_text, "可输入选项为空")
            } else {
                mToolList.map {
                    showList.add(it.title)
                    showList.add("")
                }
                listview_text.init(showList)
                listview_text.setOnItemClickListener { _, _, position, _ ->
                    // TODO:TextActivity
                    /*
                    if (position.isEvenNumber()) {
                        ToastUtil.info(showList[position])
                    } else {
                        val inputIndex = position / 2
                        val inputItem = mToolList[inputIndex]
                        when (inputItem.type) {
                            "string" -> xuiUtil.materialDialogInputString(inputItem.title, "", )
                        }
                    }*/
                }
                ToastUtil.normal("点击空白行输入内容")
            }
        }
    }

    private fun getResult(modIndex: Int, inputData: List<Any>) {

    }

    private fun showResult(result: String) {
        xuiUtil.materialDialogInputString("结果", "", result, result,
                getString(R.string.text_copy), getString(R.string.text_share), true)
                .apply {
                    onPositive { mDialog, _ ->
                        ClipboardUtil.copy(mDialog.inputEditText!!.string)
                    }
                    onNegative { mDialog, _ ->
                        share(mDialog.inputEditText!!.string)
                    }
                }.show()
    }
}
