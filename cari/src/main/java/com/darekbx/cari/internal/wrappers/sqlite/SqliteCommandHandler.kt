package com.darekbx.cari.internal.wrappers.sqlite

import android.content.Context
import com.darekbx.cari.internal.wrappers.BaseCommandHandler

internal class SqliteCommandHandler(val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "sqlite"

    override fun handleCommand(commandString: String): Any {
        val command = parseCommand(commandString)
        if (command.resource == RESOURCE_NAME) {
            val argsCount = command.arguments.size

        }
        return false
    }
}