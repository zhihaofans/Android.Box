package com.zhihaofans.androidbox.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.*
import com.zhihaofans.androidbox.mod.FavoritesMod
import com.zhihaofans.androidbox.mod.ItemNameMod
import com.zhihaofans.androidbox.util.SystemUtil
import dev.utils.app.AppUtils
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.content_favorites.*
import org.jetbrains.anko.*

class FavoritesActivity : AppCompatActivity() {
    private val favoritesMod = FavoritesMod()
    private val menu = listOf(
            "网址",
            "文本"
    )

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
        initShare()
        fab_favorites.setOnClickListener {
            val typeList = listOf(
                    ItemNameMod.FAVORITES_TYPE_URL, ItemNameMod.FAVORITES_TYPE_TEXT
            )
            selector("收藏类型", menu) { _: DialogInterface, i: Int ->
                askToAdd(typeList[i], menu[i], menu[i])
            }
        }
        initFavorites()
    }

    private fun initFavorites() {
        val favoritesList = favoritesMod.load().items
        val listViewData = favoritesList.map {
            it.title
        }.toList()
        listView_favorites.removeAllItems()
        listView_favorites.init(this, listViewData)
        listView_favorites.setOnItemClickListener { parent, view, position, id ->
            val chooseFavorites = favoritesList[position]
            Logger.d("chooseFavorites:$chooseFavorites")
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
                                    initFavorites()
                                }
                                cancelButton { coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user) }
                            }.show()
                        }
                        positiveButton(getString(R.string.text_copy) + "/" + getString(R.string.text_share)) {
                            selector("", listOf(getString(R.string.text_copy), getString(R.string.text_share))) { _: DialogInterface, i: Int ->
                                when (i) {
                                    0 -> {
                                        copy(chooseFavorites.content)
                                        coordinatorLayout_favorites.snackbar("复制完毕")
                                    }
                                    1 -> share(chooseFavorites.content)
                                }
                            }
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
                                    initFavorites()

                                }
                                cancelButton { coordinatorLayout_favorites.snackbar(R.string.text_canceled_by_user) }
                            }.show()
                        }
                    }.show()
                }
                else -> coordinatorLayout_favorites.snackbar("错误：未知类型")
            }
        }
        coordinatorLayout_favorites.snackbar("加载完毕")
    }

    private fun initShare() {
        val mIntent = intent
        var appName = AppUtils.getAppName(mIntent.`package`)
        if (appName.isNullOrEmpty()) appName = "其他应用"
        var defaultTitle = "来自$appName"
        if ((mIntent.action == Intent.ACTION_SEND) && mIntent.type != null && mIntent.type == "text/plain") {
            val st = mIntent.getStringExtra(Intent.EXTRA_TEXT)
            if (st != null) {
                if (st.isUrl()) {
                    defaultTitle += "的分享"
                    askToAdd(ItemNameMod.FAVORITES_TYPE_URL, defaultTitle, st)
                }
            }
        } else if (mIntent.action == Intent.ACTION_VIEW) {
            val uri = mIntent.data
            if (uri !== null) {
                defaultTitle += "的网址"
                askToAdd(ItemNameMod.FAVORITES_TYPE_URL, defaultTitle, uri.toString())
            }
        }
    }

    private fun askToAdd(favoritesType: String, mTitle: String, text: String) {
        alert {
            customView {
                title = getString(R.string.title_activity_favorites)
                verticalLayout {
                    textView(R.string.text_title)
                    val inputTitle = editText(mTitle).apply {
                        singleLine = true
                    }
                    textView(R.string.text_content)
                    val inputContent = editText(text)
                    positiveButton(R.string.text_add) {
                        if (inputContent.string().isEmpty() || inputTitle.string().isEmpty()) {
                            coordinatorLayout_favorites.snackbar("标题与内容都需要输入内容")
                        } else {
                            addFavorites(favoritesType, mTitle, text)
                        }
                    }
                }
            }
        }.show()
    }

    private fun addFavorites(favoritesType: String, title: String, text: String) {
        val time = SystemUtil.unixTimeStampMill().toString()
        when (favoritesType) {
            ItemNameMod.FAVORITES_TYPE_URL -> {
                if (text.isUrl()) {
                    coordinatorLayout_favorites.snackbar(
                            getString(R.string.text_add) + ":" +
                                    try {
                                        favoritesMod.add(time, title, text, favoritesType)
                                                .string(getString(R.string.text_yes), getString(R.string.text_no))

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        "Exception"
                                    })
                    initFavorites()
                } else {
                    coordinatorLayout_favorites.snackbar("错误：网址格式错误")
                    Snackbar.make(coordinatorLayout_favorites, "错误：网址格式错误,是否保存为文本？", Snackbar.LENGTH_LONG).setAction(R.string.text_save) {
                        askToAdd(ItemNameMod.FAVORITES_TYPE_TEXT, title, text)
                    }.show()

                }
            }
            ItemNameMod.FAVORITES_TYPE_TEXT -> {
                coordinatorLayout_favorites.snackbar(
                        getString(R.string.text_add) + ":" +
                                try {
                                    favoritesMod.add(time, title, text, favoritesType)
                                            .string(getString(R.string.text_yes), getString(R.string.text_no))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    "Exception"
                                })
                initFavorites()
            }
            else -> {
                coordinatorLayout_favorites.snackbar("错误：未知类型")
                askToAdd(favoritesType, title, text)
            }

        }

    }
}
