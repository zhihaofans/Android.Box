package com.zhihaofans.androidbox.kotlinEx

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

/**
 * Created by zhihaofans on 2018/11/4.
 */
fun CoordinatorLayout.snackbar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()

fun CoordinatorLayout.snackbar(message: Int) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
fun CoordinatorLayout.LongSnackbar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
fun CoordinatorLayout.LongSnackbar(message: Int) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
fun CoordinatorLayout.IndefiniteSnackbar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).show()
fun CoordinatorLayout.IndefiniteSnackbar(message: Int) = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).show()