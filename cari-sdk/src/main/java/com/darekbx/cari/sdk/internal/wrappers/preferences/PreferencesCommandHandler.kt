package com.darekbx.cari.sdk.internal.wrappers.preferences

import android.content.Context
import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.wrappers.BaseCommandHandler

internal class PreferencesCommandHandler(val context: Context) : BaseCommandHandler() {

    private val RESOURCE_NAME = "prefs"
    private val NULL_VALUE = "null"

    override fun handleCommand(commandString: String?): Any {
        when (commandString) {
            null -> return createErrorResponse("Command is empty!")
            else -> {
                parseCommand<CommandWrapper>(commandString)?.let { commandWrapper ->
                    if (commandWrapper.resource == RESOURCE_NAME) {
                        val argsCount = commandWrapper.arguments.size
                        return when (commandWrapper.command) {
                            "scopes" -> handleScopes()
                            "dump" -> handleDump(argsCount, commandWrapper)
                            "ls", "list" -> handleList(argsCount, commandWrapper)
                            "get" -> handleGet(argsCount, commandWrapper)
                            "set" -> handleSet(argsCount, commandWrapper)
                            "rm", "remove" -> handleRemove(argsCount, commandWrapper)
                            else -> false
                        }
                    }
                }
            }
        }
        return false
    }

    override fun obtainType() = RESOURCE_NAME

    private fun handleScopes(): String {
        val scopes = preferencesWrapper.listScopes()
        return createResponse(scopes)
    }

    private fun handleDump(argsCount: Int, command: CommandWrapper): String {
        return when (argsCount) {
            1 -> dumpScope(command)
            else -> dumpAll()
        }
    }

    private fun dumpAll(): String {
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

    private fun dumpScope(commandWrapper: CommandWrapper): String {
        val scope = commandWrapper.arguments.get(0)
        val keys = preferencesWrapper.listKeys(scope)
        val result = mutableMapOf<String, String>()
        keys.forEach { key ->
            val value = preferencesWrapper.getValue(scope, key)
            result.put(key, value ?: NULL_VALUE)
        }
        return createResponse(result)
    }

    private fun handleList(argsCount: Int, commandWrapper: CommandWrapper) =
        when (argsCount) {
            1 -> {
                val scope = commandWrapper.arguments.get(0)
                val keys = preferencesWrapper.listKeys(scope)
                createResponse(keys)
            }
            else -> createInvalidParametersError()
        }

    private fun handleGet(argsCount: Int, commandWrapper: CommandWrapper) =
        when (argsCount) {
            2 -> {
                val scope = commandWrapper.arguments.get(0)
                val key = commandWrapper.arguments.get(1)
                val value = preferencesWrapper.getValue(scope, key) ?: NULL_VALUE
                createResponse(value)
            }
            else -> createInvalidParametersError()
        }

    private fun handleSet(argsCount: Int, commandWrapper: CommandWrapper) =
        when (argsCount) {
            3 -> {
                val scope = commandWrapper.arguments.get(0)
                val key = commandWrapper.arguments.get(1)
                val value = commandWrapper.arguments.get(2)
                preferencesWrapper.save(scope, key, value)
                createResponse("Added \"${key}\"")
            }
            else -> createInvalidParametersError()
        }

    private fun handleRemove(argsCount: Int, commandWrapper: CommandWrapper) =
        when (argsCount) {
            2 -> {
                val scope = commandWrapper.arguments.get(0)
                val key = commandWrapper.arguments.get(1)
                preferencesWrapper.remove(scope, key)
                createResponse("Removed \"${key}\"")
            }
            else -> createInvalidParametersError()
        }

    private fun createInvalidParametersError() = createErrorResponse("Invalid parameters count")

    private val preferencesWrapper by lazy { PreferencesWrapper(context) }
}