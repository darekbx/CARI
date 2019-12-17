package com.darekbx.cari.sdk.internal.wrappers.sqlite

import android.content.Context
import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler
import java.lang.Exception

internal class SqliteCommandHandler(val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "sqlite"

    override fun handleCommand(commandString: String?): Any {
        when (commandString) {
            null -> return createErrorResponse("Command is empty!")
            else -> {
                parseCommand<CommandWrapper>(commandString)?.let { commandWrapper ->
                    if (commandWrapper.resource == RESOURCE_NAME) {
                        return when (commandWrapper.command) {
                            "databases" -> handleDatabasesList()
                            "tables" -> handleTablesList()
                            "q" -> handleQuery(commandWrapper)
                            else -> false
                        }
                    }
                }
            }
        }
        return false
    }

    override fun obtainType() = RESOURCE_NAME

    private fun handleDatabasesList(): String {
        val dataBaseList = context.databaseList()
        return createResponse(dataBaseList)
    }

    private fun handleTablesList(): String {

        val tables = "TODO"

        return createResponse(tables)
    }

    private fun handleQuery(commandWrapper: CommandWrapper): String {
        val database = commandWrapper.arguments.get(0)
        val query = commandWrapper.arguments.get(1)
        try {
            val result = sqliteWrapper.execute(database, query)
            return createResponse(result ?: "Unknown")
        } catch (e: Exception) {
            return createErrorResponse(e.message ?: e.toString())
        }
    }

    private val sqliteWrapper by lazy { SqliteWrapper(context) }
}