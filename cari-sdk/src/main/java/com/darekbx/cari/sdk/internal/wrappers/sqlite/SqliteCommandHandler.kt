package com.darekbx.cari.sdk.internal.wrappers.sqlite

import android.content.Context
import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler

internal class SqliteCommandHandler(val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "sqlite"

    override fun handleCommand(commandString: String?): Any {
        when (commandString) {
            null -> return createErrorResponse("Command is empty!")
            else -> {
                parseCommand<CommandWrapper>(commandString)?.let { command ->
                    if (command.resource == RESOURCE_NAME) {
                        //val argsCount = command.arguments.size

                    }
                }
            }
        }
        return false
    }
}