package com.darekbx.cari.internal.wrappers.preferences

import android.content.Context
import com.darekbx.cari.internal.model.CommandWrapper
import com.darekbx.cari.internal.wrappers.BaseCommandHandler

internal class PreferencesCommandHandler(context: Context)  : BaseCommandHandler(context) {

    val EMPTY_RESPONSE = ""

    override fun handleCommand(commandString: String): Any {
        val command = parseCommand(commandString)
        val argsCount = command.arguments.size
        return when (command.command) {
            "ls", "list" -> handleList()
            "get" -> handleGet(argsCount, command)
            "set" -> handleSet(argsCount, command)
            "rm", "remove" -> handleRemove(argsCount, command)
            else -> false
        }
    }

    private fun handleList(): String {
        val keys = preferencesWrapper.listKeys()
        return createResponse(keys)
    }

    private fun handleGet(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            1 -> {
                val key = command.arguments.get(0)
                val value = preferencesWrapper.getValue(key.option)
                createResponse(value)
            }
            else -> createInvalidParametersError()
        }

    private fun handleSet(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            2 -> {
                val key = command.arguments.get(0)
                val value = command.arguments.get(20)
                preferencesWrapper.save(key.option, value.option)
                createResponse(EMPTY_RESPONSE)
            }
            else -> createInvalidParametersError()
        }

    private fun handleRemove(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            1 -> {
                val key = command.arguments.get(0)
                preferencesWrapper.remove(key.option)
                createResponse(EMPTY_RESPONSE)
            }
            else -> createInvalidParametersError()
        }

    private fun createInvalidParametersError() = createErrorResponse("Invalid parameters count")

    private val preferencesWrapper by lazy { PreferencesWrapper(context) }
}