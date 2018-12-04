package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.copy
import com.zhihaofans.androidbox.kotlinEx.init
import com.zhihaofans.androidbox.kotlinEx.snackbar
import com.zhihaofans.androidbox.kotlinEx.string
import com.zhihaofans.androidbox.mod.FavoritesMod
import com.zhihaofans.androidbox.mod.ItemNameMod
import com.zhihaofans.androidbox.util.SystemUtil
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.content_favorites.*
import org.jetbrains.anko.*

class FavoritesActivity : AppCompatActivity() {
    private val favoritesMod = FavoritesMod()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        setSupportActionBar(toolbar_favorites)
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            coordinatorLayout_favorites.snackbar("初始化失败")
        }
    }

    private fun init() {
        fab_favorites.setOnClickListener {
            val menu = listOf(
                    "网址",
                    "文本"
            )
            val typeList = listOf(
                    ItemNameMod.FAVORITES_TYPE_URL, ItemNameMod.FAVORITES_TYPE_TEXT
            )
            val time = SystemUtil.unixTimeStampMill().toString()
            selector("收藏类型", menu) { _: DialogInterface, i: Int ->
                alert {
                    customView {
                        verticalLayout {
                            textView("标题")
                            val inputTitle = editText(menu[i] + time).apply {
                                singleLine = true
                            }
                            textView(menu[i])
                            val inputContent = editText(menu[i] + time)
                            positiveButton(R.string.text_add) {
                                if (inputContent.string().isEmpty() || inputTitle.string().isEmpty()) {
                                    coordinatorLayout_favorites.snackbar("标题与${menu[i]}都需要输入内容")
                                } else {
                                    val chooseType = typeList[i]
                                    when (chooseType) {
                                        ItemNameMod.FAVORITES_TYPE_URL -> {
                                            if (SystemUtil.checkUrl(inputContent.string()) == null) {
                                                coordinatorLayout_favorites.snackbar("${menu[i]}格式错误")
                                            } else {
                                                coordinatorLayout_favorites.snackbar(
                                                        getString(R.string.text_add) + ":" +
                                                                favoritesMod.add(time, inputTitle.string(), inputContent.string(), chooseType)
                                                                        .string(getString(R.string.text_yes), getString(R.string.text_no))
                                                )
                                            }
                                        }
                                        ItemNameMod.FAVORITES_TYPE_TEXT -> {
                                            coordinatorLayout_favorites.snackbar(
                                                    getString(R.string.text_add) + ":" +
                                                            favoritesMod.add(time, inputTitle.string(), inputContent.string(), chooseType)
                                                                    .string(getString(R.string.text_yes), getString(R.string.text_no))
                                            )

                                        }
                                        else -> coordinatorLayout_favorites.snackbar("错误：未知类型")
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
        initFavorites()
    }

    private fun initFavorites() {
        val favoritesList = favoritesMod.load()
        val listViewData = favoritesList.map {
            it.title
        }.toList()
        listView_favorites.init(this, listViewData)
        listView_favorites.setOnItemClickListener { parent, view, position, id ->
            val chooseFavorites = favoritesList[position]
            when (chooseFavorites.type) {
                ItemNameMod.FAVORITES_TYPE_TEXT -> {
                    alert {
                        title = "这是个文本"
                        customView {
                            verticalLayout {
                                editText(chooseFavorites.content).apply {
                                    isFocusable = false
                                }
                            }
                        }
                        onCancelled {
                            coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user)
                        }
                        positiveButton(R.string.text_share) {
                            share(chooseFavorites.content)
                        }
                        negativeButton(R.string.text_copy) {
                            copy(chooseFavorites.content)
                            coordinatorLayout_favorites.snackbar("复制完毕")
                        }
                    }.show()
                }
                ItemNameMod.FAVORITES_TYPE_URL -> {
                    alert {
                        title = "这是个链接"
                        message = "是否打开？"
                        onCancelled {
                            coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user)
                        }
                        positiveButton(R.string.text_open) {
                            SystemUtil.browse(this@FavoritesActivity, chooseFavorites.content)
                        }
                        negativeButton(R.string.text_delete) {
                            alert {
                                message = getString(R.string.text_delete) + " ?"
                                onCancelled {
                                    coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user)
                                }
                                yesButton {
                                    coordinatorLayout_favorites.snackbar(
                                            getString(R.string.text_delete) + ":" +
                                                    favoritesMod.delete(position).string(getString(R.string.text_yes), getString(R.string.text_no))
                                    )
                                }
                                cancelButton { coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user) }
                            }
                        }
                    }.show()
                }
                else -> coordinatorLayout_favorites.snackbar("错误：未知类型")
            }
        }
        coordinatorLayout_favorites.snackbar("加载完毕")
    }
}
