package com.darekbx.cari.sdk.internal.wrappers

import com.darekbx.cari.BuildConfig
import com.darekbx.cari.sdk.internal.json.JsonParser
import com.darekbx.cari.sdk.internal.model.ErrorWrapper
import com.darekbx.cari.sdk.internal.model.ResponseWrapper
import java.lang.Exception

internal abstract class BaseCommandHandler {

    abstract fun handleCommand(commandString: String?): Any

    abstract fun obtainType(): String

    protected inline fun <reified T: Any> parseCommand(commandString: String): T? {
        try {
            return JsonParser.parse(commandString) as T
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return null
        }
    }

    protected fun createResponse(response: Any): String {
        try {
            val type = obtainType()
            val wrapper = ResponseWrapper(type, response)
            return JsonParser.toJson(wrapper)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return ""
        }
    }

    protected fun createErrorResponse(errorMessage: String): String {
        val errorWrapper = ErrorWrapper(errorMessage)
        return JsonParser.toJson(errorWrapper)
    }
}