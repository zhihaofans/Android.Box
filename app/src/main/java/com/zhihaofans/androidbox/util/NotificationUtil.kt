package com.zhihaofans.androidbox.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.kotlinEx.appName

/**
 * 在此写用途

 * @author: zhiuhaofans

 * @date: 2018-11-06 15:40

 */
class NotificationUtil {
    companion object {
        private const val defaultChannelId = "com.zhihaofans.androidbox.default"
        private fun getDefaultNotificationChannel(mContext: Context): NotificationChannel? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                try {
                    val c = nm.getNotificationChannel(defaultChannelId)
                    c
                            ?: createChannel(mContext, defaultChannelId, mContext.appName(), mContext.appName(), NotificationManager.IMPORTANCE_DEFAULT)
                } catch (e: Exception) {
                    e.printStackTrace()
                    createChannel(mContext, defaultChannelId, mContext.appName(), mContext.appName(), NotificationManager.IMPORTANCE_DEFAULT)
                }
            } else {
                null
            }
        }

        fun create(mContext: Context, title: String, message: String): Int? {
            val nm = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = SystemUtil.unixTimeStampMill().toInt()
            val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (this.getDefaultNotificationChannel(mContext) == null) {
                    return null
                } else {
                    this.getNotification(mContext, defaultChannelId, R.mipmap.ic_launcher, title, message)
                }

            } else {
                this.getNotification(mContext, R.mipmap.ic_launcher, title, message)
            }
            nm.notify(notificationId, notification)
            return notificationId
        }

        private fun getNotification(context: Context, icon: Int, title: String, msg: String): Notification = NotificationCompat.Builder(context).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(msg)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
        }.build()

        private fun getNotification(context: Context, channelId: String, icon: Int, title: String, msg: String): Notification = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(msg)
        }.build()

        private fun createChannel(mContext: Context, id: String, name: String, desc: String, importance: Int): NotificationChannel? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                var mChannel: NotificationChannel?
                try {
                    mChannel = nm.getNotificationChannel(id)
                    if (mChannel == null) {
                        val notificationManager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        mChannel = NotificationChannel(id, name, importance).apply {
                            description = desc
                        }
                        try {
                            notificationManager.createNotificationChannel(mChannel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mChannel = null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    val notificationManager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    mChannel = NotificationChannel(id, name, importance).apply {
                        description = desc
                    }
                    try {
                        notificationManager.createNotificationChannel(mChannel)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mChannel = null
                    }
                }
                return mChannel
            } else {
                null
            }
        }
    }
}