package com.zhihaofans.androidbox.view

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.mod.SettingMod
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.kotlinEx.init
import io.zhihao.library.android.kotlinEx.removeAllItems
import io.zhihao.library.android.kotlinEx.string
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.content_setting.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.editText
import org.jetbrains.anko.switch


class SettingActivity : AppCompatActivity() {
    private val settingList = SettingMod.getSettingList()
    private val settingIdList = settingList.map { it.key }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar_setting)
        init()
        fab.setOnClickListener {
            init()
        }
    }

    private fun init() {
        try {
            listview_setting.removeAllItems()
            listview_setting.init(settingList.map { it.key })
            listview_setting.setOnItemClickListener { _, _, position, _ ->
                val setId = settingIdList[position]
                when (settingList[setId]) {
                    SettingMod.SETTING_TYPE_BOOLEAN,
                    SettingMod.SETTING_TYPE_STRING,
                    SettingMod.SETTING_TYPE_INT -> setting(setId)
                    else -> ToastUtil.error("获取设置类型失败")
                }
            }
            ToastUtil.success("初始化成功")
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("初始化失败")
        }
    }

    private fun setting(setId: String) {
        if (settingIdList.indexOf(setId) >= 0) {
            when (val setType = settingList[setId]) {
                SettingMod.SETTING_TYPE_BOOLEAN -> {
                    val setData = SettingMod.loadBooleanSetting(setId)
                    alert {
                        title = getString(R.string.text_setting)
                        message = setId
                        customView {
                            val mSwitch = switch()
                            mSwitch.text = "开关"
                            mSwitch.isChecked = setData ?: false
                            negativeButton(R.string.text_cancel) {}
                            positiveButton(R.string.text_save) {
                                if (SettingMod.saveSetting(setId, setType, mSwitch.isChecked)) {
                                    ToastUtil.success("保存成功")
                                } else {
                                    ToastUtil.error("保存失败")
                                }
                            }
                        }
                    }.show()
                }
                SettingMod.SETTING_TYPE_STRING -> {
                    val setData = SettingMod.loadStringSetting(setId)
                    alert {
                        title = getString(R.string.text_setting)
                        message = setId
                        customView {
                            val mEditText = editText(setData ?: "")
                            negativeButton(R.string.text_cancel) {}
                            positiveButton(R.string.text_save) {
                                if (SettingMod.saveSetting(setId, setType, mEditText.string)) {
                                    ToastUtil.success("保存成功")
                                } else {
                                    ToastUtil.error("保存失败")
                                }
                            }
                        }
                    }.show()
                }
                SettingMod.SETTING_TYPE_INT -> {
                    val setData = SettingMod.loadIntSetting(setId)
                    alert {
                        title = getString(R.string.text_setting)
                        message = setId
                        customView {
                            val mEditText = editText()
                            if (setData !== null) mEditText.setText(setData.toString())
                            mEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                            negativeButton(R.string.text_cancel) {}
                            positiveButton(R.string.text_save) {
                                if (SettingMod.saveSetting(setId, setType, mEditText.string.toInt())) {
                                    ToastUtil.success("保存成功")
                                } else {
                                    ToastUtil.error("保存失败")
                                }
                            }
                        }
                    }.show()
                }
                else -> ToastUtil.error("未知设置类型")
            }
        } else {
            ToastUtil.error("不存在该设置选项")
        }
    }
}
