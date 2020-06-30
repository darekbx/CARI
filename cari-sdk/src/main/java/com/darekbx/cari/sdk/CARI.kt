package com.darekbx.cari.sdk

import android.content.Context
import com.darekbx.cari.sdk.internal.bubble.BubbleNotification
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
            BubbleNotification(context).createBubble()
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