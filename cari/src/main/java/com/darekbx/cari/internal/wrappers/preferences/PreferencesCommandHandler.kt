package com.darekbx.cari.internal.wrappers.preferences

import android.content.Context
import com.darekbx.cari.internal.model.CommandWrapper
import com.darekbx.cari.internal.wrappers.BaseCommandHandler

internal class PreferencesCommandHandler(val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "prefs"
    private val NULL_VALUE = "{NULL}"

    override fun handleCommand(commandString: String): Any {
        val command = parseCommand(commandString)
        if (command.resource == RESOURCE_NAME) {
            val argsCount = command.arguments.size
            return when (command.command) {
                "dump" -> handleDump()
                "scopes" -> handleScopes()
                "ls", "list" -> handleList(argsCount, command)
                "get" -> handleGet(argsCount, command)
                "set" -> handleSet(argsCount, command)
                "rm", "remove" -> handleRemove(argsCount, command)
                else -> false
            }
        }
        return false
    }

    private fun handleDump(): String {
        val scopes = preferencesWrapper.listScopes()
        val result = mutableMapOf<String, MutableMap<String, String>>()

        scopes.forEach { scope ->
            val scopeMap = mutableMapOf<String, String>()
            val keys = preferencesWrapper.listKeys(scope)
            keys.forEach { key ->
                val value = preferencesWrapper.getValue(scope, key)
                scopeMap.put(key, value ?: NULL_VALUE)
            }
            result.put(scope, scopeMap)
        }

        return createResponse(result)
    }

    private fun handleScopes(): String {
        val scopes = preferencesWrapper.listScopes()
        return createResponse(scopes)
    }

    private fun handleList(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            1 -> {
                val scope = command.arguments.get(0)
                val keys = preferencesWrapper.listKeys(scope.option)
                createResponse(keys)
            }
            else -> createInvalidParametersError()
        }

    private fun handleGet(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            2 -> {
                val scope = command.arguments.get(0)
                val key = command.arguments.get(1)
                val value = preferencesWrapper.getValue(scope.option, key.option) ?: NULL_VALUE
                createResponse(value)
            }
            else -> createInvalidParametersError()
        }

    private fun handleSet(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            2 -> {
                val scope = command.arguments.get(0)
                val key = command.arguments.get(1)
                val value = command.arguments.get(2)
                preferencesWrapper.save(scope.option, key.option, value.option)
                createResponse(EMPTY_RESPONSE)
            }
            else -> createInvalidParametersError()
        }

    private fun handleRemove(argsCount: Int, command: CommandWrapper) =
        when (argsCount) {
            1 -> {
                val scope = command.arguments.get(0)
                val key = command.arguments.get(1)
                preferencesWrapper.remove(scope.option, key.option)
                createResponse(EMPTY_RESPONSE)
            }
            else -> createInvalidParametersError()
        }

    private fun createInvalidParametersError() = createErrorResponse("Invalid parameters count")

    private val preferencesWrapper by lazy { PreferencesWrapper(context) }
}