package com.darekbx.cari.sdk.internal.wrappers.common

import android.content.Context
import com.darekbx.cari.BuildConfig
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler

internal class CommonCommandHandler (val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "common"
    private val VERSION_COMMAND = "version"

    override fun handleCommand(commandString: String?): Any {
        when (commandString) {
            null -> return createErrorResponse("Command is empty!")
            else -> {
                parseCommand<String>(commandString)?.let { command ->
                    when (command) {
                        VERSION_COMMAND -> return handleVersion()
                        else -> return false
                    }
                }
            }
        }
        return false
    }

    override fun obtainType() = RESOURCE_NAME

    private fun handleVersion(): String {
        val result = mapOf(
            "SDK-Version" to BuildConfig.VERSION_NAME,
            "Application" to context.packageName
        )
        return createResponse(result)
    }
}