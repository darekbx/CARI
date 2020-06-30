package com.darekbx.cari.sdk.internal.bubble

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import com.darekbx.cari.R

class BubbleNotification(val context: Context) {

    companion object {
        private val CHANNEL_ID = "bubble_channel_id"
        private val BUBBLE_VIEW_HEIGHT = 500
    }

    fun createBubble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val target = Intent(context, BubbleActivity::class.java)
            val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0 /* flags */)

            val bubbleData = Notification.BubbleMetadata.Builder()
                .setDesiredHeight(BUBBLE_VIEW_HEIGHT)
                .setIcon(Icon.createWithResource(context, R.drawable.ic_tree))
                .setIntent(bubbleIntent)
                .build()

            val chatBot = Person.Builder()
                .setBot(true)
                .setName(context.getString(R.string.bubble_title))
                .setImportant(true)
                .build()

            val builder = Notification.Builder(context, CHANNEL_ID)
                .setContentIntent(bubbleIntent)
                .setSmallIcon(R.drawable.ic_arrow_back)
                .setBubbleMetadata(bubbleData)
                .addPerson(chatBot)

            var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (channel == null) {
                channel = provideNotificationChannel(CHANNEL_ID)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(1, builder.build())
        }
    }

    @SuppressLint("NewApi")
    private fun provideNotificationChannel(CHANNEL_ID: String) = NotificationChannel(
        CHANNEL_ID,
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
    )

    private val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
}