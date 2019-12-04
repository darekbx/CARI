package com.darekbx.carisdk

import android.content.Context
import com.darekbx.carisdk.internal.communication.CompressionUtil
import kotlinx.coroutines.Job

import com.darekbx.carisdk.internal.communication.SocketCommunication
import com.darekbx.carisdk.internal.model.ErrorWrapper
import com.darekbx.carisdk.internal.wrappers.BaseCommandHandler
import com.darekbx.carisdk.internal.wrappers.preferences.PreferencesCommandHandler
import com.darekbx.carisdk.internal.wrappers.sqlite.SqliteCommandHandler
import com.google.gson.Gson

object CARI {

    private val VERSION_COMMAND = "version"

    /**
     * Initializes CARI server for the application.
     *
     * @param context Use application context, cannot be null.
     * @param options Optional arguments, you can set for eg. different port.
     *
     * @return Job responsible for module communication.
     *         You can use this job to stop processing, when app is being destroyed
     */
    fun initialize(context: Context, options: Options = Options()): Job {

        val commandHandlers = listOf(
            PreferencesCommandHandler(context),
            SqliteCommandHandler(context)
        )

        val port = options.port
        val socketCommunication = SocketCommunication(port).apply {
            callback = { encodedCommand ->
                val command = CompressionUtil.decodeData(encodedCommand)
                handleCommand(command, commandHandlers)
            }
            start()
        }

        return socketCommunication.supervisiorJob
    }

    private fun handleCommand(command: String, handlers: List<BaseCommandHandler>): String {
        if (command.replace("\"", "").toLowerCase() == VERSION_COMMAND) {
            return notifyVersion()
        }
        handlers.forEach {  commandHandler ->
            val result = commandHandler.handleCommand(command)
            if (result is String) {
                return CompressionUtil.encodeData(result)
            }
        }
        return notifyError()
    }

    private fun notifyVersion(): String {
        val result = gson.toJson(arrayOf("SDK-Version", BuildConfig.VERSION_NAME))
        return CompressionUtil.encodeData(result)
    }

    private fun notifyError(): String {
        val error = ErrorWrapper("Unknown command")
        val result = gson.toJson(error)
        return CompressionUtil.encodeData(result)
    }

    private val gson by lazy { Gson() }
}