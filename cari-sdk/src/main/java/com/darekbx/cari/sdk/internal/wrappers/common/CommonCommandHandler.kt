package com.darekbx.cari.sdk.internal.wrappers.common

import android.content.Context
import com.darekbx.cari.BuildConfig
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler

internal class CommonCommandHandler (val context: Context) : BaseCommandHandler() {

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

    private fun handleVersion(): String {
        val result = arrayOf("SDK-Version", BuildConfig.VERSION_NAME)
        return createResponse(result)
    }
}