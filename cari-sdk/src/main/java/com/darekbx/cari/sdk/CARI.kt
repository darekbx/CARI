package com.darekbx.cari.sdk

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.BubbleActivity
import com.darekbx.cari.sdk.internal.communication.CompressionUtil
import com.darekbx.cari.sdk.internal.communication.SocketCommunication
import com.darekbx.cari.sdk.internal.json.JsonParser
import com.darekbx.cari.sdk.internal.model.ErrorWrapper
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler
import com.darekbx.cari.sdk.internal.wrappers.common.CommonCommandHandler
import com.darekbx.cari.sdk.internal.wrappers.preferences.PreferencesCommandHandler
import com.darekbx.cari.sdk.internal.wrappers.sqlite.SqliteCommandHandler

object CARI {

    /**
     * Initializes CARI server for the application.
     *
     * @param context Use application context, cannot be null.
     * @param options Optional arguments, you can set for eg. different port.
     */
    fun initialize(context: Context, options: Options = Options()) {

        val commandHandlers = listOf(
            PreferencesCommandHandler(context),
            SqliteCommandHandler(context),
            CommonCommandHandler(context)
        )

        val port = options.port
        SocketCommunication(port).apply {
            callback = { encodedCommand ->
                val command = CompressionUtil.decodeData(encodedCommand)
                handleCommand(command, commandHandlers)
            }
            start()
        }

        if (options.isBubbleEnabled) {
            createBubble(context)
        }
    }

    private fun createBubble(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val CHANNEL_ID = "bubble_channel_id"
            val target = Intent(context, BubbleActivity::class.java)
            val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0 /* flags */)

            // Create bubble metadata
            val bubbleData = Notification.BubbleMetadata.Builder()
                .setDesiredHeight(600)
                .setIcon(Icon.createWithResource(context, com.darekbx.cari.R.drawable.ic_tree))
                .setIntent(bubbleIntent)
                .build()

            // Create notification
            val chatBot = Person.Builder()
                .setBot(true)
                .setName("BubbleBot")
                .setImportant(true)
                .build()

            val builder = Notification.Builder(context, CHANNEL_ID)
                .setContentIntent(bubbleIntent)
                .setSmallIcon(com.darekbx.cari.R.drawable.ic_arrow_back)
                .setBubbleMetadata(bubbleData)
                .addPerson(chatBot)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (channel == null) {
                channel = NotificationChannel(
                    CHANNEL_ID,
                    "app name",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(1, builder.build())
        }
    }

    private fun handleCommand(command: String, handlers: List<BaseCommandHandler>): String {
        handlers.forEach { commandHandler ->
            val result = commandHandler.handleCommand(command)
            if (result is String) {
                return CompressionUtil.encodeData(result)
            }
        }
        return notifyError()
    }

    private fun notifyError(): String {
        val error = ErrorWrapper("Unknown command")
        val result = JsonParser.toJson(error)
        return CompressionUtil.encodeData(result)
    }

}