package com.zhihaofans.androidbox.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.data.NotificationProgressData
import com.zhihaofans.androidbox.kotlinEx.appName
import io.zhihao.library.android.util.SystemServiceUtil

/**
 * @author: zhihaofans

 * @date: 2018-11-06 15:40

 */
class NotificationUtil {
    private var mContext: Context? = null
    private val defaultChannelId = "com.zhihaofans.androidbox.default"
    private val lowLevelChannelId = "com.zhihaofans.androidbox.low"
    private val downloadChannelId = "com.zhihaofans.androidbox.download"
    private val nm = SystemServiceUtil.getNotificationManager()
    private fun getDefaultNotificationChannel(): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mContext == null) return null
            try {
                val c = nm.getNotificationChannel(defaultChannelId)
                c
                        ?: createChannel(defaultChannelId, mContext!!.appName(), mContext!!.appName(), NotificationManager.IMPORTANCE_DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
                createChannel(defaultChannelId, mContext!!.appName(), mContext!!.appName(), NotificationManager.IMPORTANCE_DEFAULT)
            }
        } else {
            null
        }
    }

    private fun getLowLevelNotificationChannel(): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mContext == null) return null
            try {
                val c = nm.getNotificationChannel(lowLevelChannelId)
                c
                        ?: createChannel(lowLevelChannelId, mContext!!.appName(), mContext!!.appName(), NotificationManager.IMPORTANCE_LOW)
            } catch (e: Exception) {
                e.printStackTrace()
                createChannel(lowLevelChannelId, mContext!!.appName(), mContext!!.appName(), NotificationManager.IMPORTANCE_LOW)
            }
        } else {
            null
        }
    }

    private fun getDownloadNotificationChannel(): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mContext == null) return null
            try {
                val c = nm.getNotificationChannel(downloadChannelId)
                c
                        ?: createChannel(downloadChannelId, mContext!!.appName(), mContext!!.getString(R.string.text_download), NotificationManager.IMPORTANCE_LOW)
            } catch (e: Exception) {
                e.printStackTrace()
                createChannel(downloadChannelId, mContext!!.appName(), mContext!!.getString(R.string.text_download), NotificationManager.IMPORTANCE_LOW)
            }
        } else {
            null
        }
    }

    private fun getNotificationId(): Int {
        return DatetimeUtil.unixTimeStampMill().toInt()
    }

    private fun getNotification(icon: Int, title: String, msg: String): Notification {
        return this.getBaseBuilder(null, title, msg, icon).apply { setAutoCancel(true) }.build()
    }

    private fun getNotification(channelId: String, icon: Int, title: String, msg: String): Notification {
        return this.getBaseBuilder(channelId, title, msg, icon).apply { setAutoCancel(true) }.build()
    }

    private fun getIntentNotification(channelId: String?, icon: Int, title: String, msg: String, intent: PendingIntent): Notification {
        return this.getIntentBuilder(channelId, title, msg, icon, intent).apply {
            setAutoCancel(true)
        }.build()
    }

    private fun createChannel(id: String, name: String, desc: String, importance: Int): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mContext == null) return null
            var mChannel: NotificationChannel?
            try {
                mChannel = nm.getNotificationChannel(id)
                if (mChannel == null) {
                    mChannel = NotificationChannel(id, name, importance).apply {
                        description = desc
                    }
                    try {
                        nm.createNotificationChannel(mChannel)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mChannel = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mChannel = NotificationChannel(id, name, importance).apply {
                    description = desc
                }
                try {
                    nm.createNotificationChannel(mChannel)
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

    private fun getIntentBuilder(channelId: String?, title: String, message: String, icon: Int, intent: PendingIntent): NotificationCompat.Builder {
        return if (channelId == null) {
            NotificationCompat.Builder(mContext).apply {
                setSmallIcon(icon)
                setContentTitle(title)
                setContentText(message)
                setContentIntent(intent)
            }
        } else {
            NotificationCompat.Builder(mContext!!, channelId).apply {
                setSmallIcon(icon)
                setContentTitle(title)
                setContentText(message)
                setContentIntent(intent)
            }
        }

    }

    private fun getBaseBuilder(channelId: String?, title: String, message: String, icon: Int): NotificationCompat.Builder {
        return if (channelId == null) {
            NotificationCompat.Builder(mContext).apply {
                setSmallIcon(icon)
                setContentTitle(title)
                setContentText(message)
            }
        } else {
            NotificationCompat.Builder(mContext!!, channelId).apply {
                setSmallIcon(icon)
                setContentTitle(title)
                setContentText(message)
            }
        }

    }

    fun init(context: Context) {
        this.mContext = context
    }

    fun create(title: String, message: String, lowLevel: Boolean = false): Int? {
        val notificationId = this.getNotificationId()
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if ((if (lowLevel) this.getLowLevelNotificationChannel() else this.getDefaultNotificationChannel()) == null) {
                return null
            } else {
                this.getNotification(if (lowLevel) lowLevelChannelId else defaultChannelId, R.mipmap.ic_launcher, title, message)
            }
        } else {
            this.getNotification(R.mipmap.ic_launcher, title, message)
        }
        nm.notify(notificationId, notification)
        return notificationId
    }

    fun createIntent(title: String, message: String, intent: PendingIntent, lowLevel: Boolean = false): Int? {
        val notificationId = this.getNotificationId()
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if ((if (lowLevel) this.getLowLevelNotificationChannel() else this.getDefaultNotificationChannel()) == null) {
                return null
            } else {
                this.getIntentNotification(if (lowLevel) lowLevelChannelId else defaultChannelId, R.mipmap.ic_launcher, title, message, intent)
            }

        } else {
            this.getIntentNotification(null, R.mipmap.ic_launcher, title, message, intent)
        }
        nm.notify(notificationId, notification)
        return notificationId
    }

    fun createProgress(title: String, message: String): NotificationProgressData? {
        if (mContext == null) {
            return null
        }
        val notificationId = this.getNotificationId()
        if (this.getDownloadNotificationChannel() == null) return null
        val mBuilder = getBaseBuilder(downloadChannelId, title, message, R.mipmap.ic_launcher).apply {
            setProgress(0, 0, true)
        }
        nm.notify(notificationId, mBuilder.build())
        return NotificationProgressData(notificationId, mBuilder)
    }

    fun setProgressNotificationLength(notificationProgressData: NotificationProgressData, length: Int, maxProgress: Int): NotificationProgressData {
        val mBuilder = notificationProgressData.builder.apply {
            if (length == maxProgress) {
                setProgress(0, 0, false)
                setAutoCancel(true)
            } else {
                setProgress(maxProgress, length, true)
            }
        }
        nm.notify(notificationProgressData.notificationId,
                mBuilder.build())
        return NotificationProgressData(notificationProgressData.notificationId, mBuilder)
    }

    fun delete(notificationId: Int) {
        nm.cancel(notificationId)
    }

}