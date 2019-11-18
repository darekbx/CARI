package com.darekbx.cari.internal.wrappers

import android.content.Context
import com.darekbx.cari.internal.model.CommandWrapper
import com.darekbx.cari.internal.model.ErrorWrapper
import com.google.gson.Gson

internal abstract class BaseCommandHandler(private val context: Context) {

    abstract fun handleCommand(command: String): Any

    protected fun parseCommand(command: String) : CommandWrapper {
        return gson.fromJson(command,  CommandWrapper::class.java)
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