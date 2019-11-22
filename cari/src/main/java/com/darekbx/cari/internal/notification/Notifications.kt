package com.darekbx.cari.internal.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.darekbx.cari.R

internal class Notifications(val context: Context) {

    private val CHANNEL_ID = "CARI_notification_channel"
    private val NOTIFICATION_ID = 1009

    fun displayNotification() {
        val customNotification = createNotification()
        showNotification(customNotification)
    }

    private fun createNotification(): Notification? {
        val packageName = context.packageName
        val layout = RemoteViews(packageName, R.layout.layout_notification_small)
        val layoutExpanded = RemoteViews(packageName, R.layout.layout_notification_large)

        layoutExpanded.setOnClickFillInIntent(R.id.button_back, null)
        layoutExpanded.setRemoteAdapter(R.id.items_list, Intent(context, ListService::class.java))

        val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tree)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(layout)
            .setCustomBigContentView(layoutExpanded)
            .build()

        return customNotification
    }

    private fun showNotification(customNotification: Notification?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel: NotificationChannel? =
                notificationManager.getNotificationChannel(CHANNEL_ID)
            if (channel == null) {
                channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        notificationManager.notify(NOTIFICATION_ID, customNotification)
    }
}