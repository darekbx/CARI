package com.darekbx.cari.sdk.internal.wrappers

import com.darekbx.cari.sdk.internal.model.ErrorWrapper
import com.darekbx.cari.sdk.internal.model.ResponseWrapper
import com.google.gson.Gson
import java.lang.Exception

internal abstract class BaseCommandHandler {

    protected val EMPTY_RESPONSE = ""

    abstract fun handleCommand(commandString: String?): Any

    protected inline fun <reified T: Any> parseCommand(commandString: String): T? {
        try {
            return gson.fromJson(commandString, T::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    protected fun createResponse(response: Any): String {
        try {
            val wrapper = ResponseWrapper(response)
            return gson.toJson(wrapper)
        } catch (e: Exception) {
            return ""
        }
    }

    protected fun createErrorResponse(errorMessage: String): String {
        val errorWrapper = ErrorWrapper(errorMessage)
        return gson.toJson(errorWrapper)
    }

    protected val gson by lazy { Gson() }
}