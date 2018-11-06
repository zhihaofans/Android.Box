package com.zhihaofans.androidbox.data

import androidx.core.app.NotificationCompat

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-06 19:03

 */
data class NotificationProgressData(
        val notificationId: Int,
        val builder: NotificationCompat.Builder
)