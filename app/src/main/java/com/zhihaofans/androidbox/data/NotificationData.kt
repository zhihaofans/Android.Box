package com.zhihaofans.androidbox.data

import androidx.core.app.NotificationCompat

/**
 * @author: zhihaofans

 * @date: 2018-11-06 19:03

 */
data class NotificationProgressData(
        val notificationId: Int,
        val builder: NotificationCompat.Builder
)