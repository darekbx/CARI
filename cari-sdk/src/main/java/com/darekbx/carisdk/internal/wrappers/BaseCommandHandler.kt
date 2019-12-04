package com.darekbx.carisdk.internal.wrappers

import com.darekbx.carisdk.internal.model.CommandWrapper
import com.darekbx.carisdk.internal.model.ErrorWrapper
import com.google.gson.Gson

internal abstract class BaseCommandHandler {

    protected val EMPTY_RESPONSE = ""

    abstract fun handleCommand(commandString: String?): Any

    protected fun parseCommand(commandString: String): CommandWrapper {
        return gson.fromJson(commandString, CommandWrapper::class.java)
    }

    protected fun createResponse(response: Any): String {
        return gson.toJson(response)
    }

    protected fun createErrorResponse(errorMessage: String): String {
        val errorWrapper = ErrorWrapper(errorMessage)
        return gson.toJson(errorWrapper)
    }

    private val gson by lazy { Gson() }
}