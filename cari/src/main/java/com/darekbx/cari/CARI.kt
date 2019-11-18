package com.darekbx.cari

import android.content.Context
import com.darekbx.cari.internal.communication.CompressionUtil
import kotlinx.coroutines.Job

import com.darekbx.cari.internal.communication.SocketCommunication
import com.darekbx.cari.internal.model.ErrorWrapper
import com.darekbx.cari.internal.wrappers.preferences.PreferencesCommandHandler
import com.darekbx.cari.internal.wrappers.sqlite.SqliteCommandHandler
import com.google.gson.Gson

object CARI {

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

        val preferencesCommandHandler = PreferencesCommandHandler(context)
        val sqliteCommandHandler = SqliteCommandHandler(context)

        val port = options.port
        val socketCommunication = SocketCommunication(port).apply {
            callback = { encodedCommand ->
                handleCommand(encodedCommand, preferencesCommandHandler, sqliteCommandHandler)
            }
            start()
        }

        return socketCommunication.supervisiorJob
    }

    private fun handleCommand(
        encodedCommand: String,
        preferencesCommandHandler: PreferencesCommandHandler,
        sqliteCommandHandler: SqliteCommandHandler
    ): String {
        val command = CompressionUtil.decodeData(encodedCommand)

        val preferencesResult = preferencesCommandHandler.handleCommand(command)
        if (preferencesResult is String) {
            return CompressionUtil.encodeData(preferencesResult)
        }

        val sqliteResult = sqliteCommandHandler.handleCommand(command)
        if (sqliteResult is String) {
            return CompressionUtil.encodeData(sqliteResult)
        }

        return notifyError()
    }

    private fun notifyError(): String {
        val error = ErrorWrapper("Unknown command")
        val result = gson.toJson(error)
        return CompressionUtil.encodeData(result)
    }

    private val gson by lazy { Gson() }
}