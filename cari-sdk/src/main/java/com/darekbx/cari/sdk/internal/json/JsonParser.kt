package com.darekbx.cari.sdk.internal.json

import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.model.ErrorWrapper
import com.darekbx.cari.sdk.internal.model.ResponseWrapper
import com.darekbx.cari.sdk.internal.model.SqliteResultWrapper
import org.json.JSONObject

internal object JsonParser {

    fun parse(value: String): Any {
        if (value[0] == '[' || value[0] == '{') {
            val data = JSONObject(value)

            val resource = data.getString("resource")
            val command = data.getString("command")
            val argumentsArray = data.getJSONArray("arguments")
            val arguments = (0..(argumentsArray.length() - 1))
                .map { argumentsArray.getString(it) }
                .toMutableList()

            return CommandWrapper(resource, command, arguments)
        } else {
            return value.removeSurrounding("\"")
        }
    }

    fun toJson(data: Any): String {
        return when (data) {
            is ErrorWrapper -> errorWrapperToJson(data)
            is ResponseWrapper -> responseWrapperToJson(data)
            else -> "Unknown data format"
        }
    }

    private fun responseWrapperToJson(data: ResponseWrapper): String {
        val root = JSONObject()
        root.put("type", data.type)

        when (data.response) {
            is SqliteResultWrapper -> {
                val child = JSONObject().apply {
                    put("result", JSONObject.wrap(data.response.result))
                    put("summary", data.response.summary)
                    put("limitedRows", data.response.limitedRows)
                }
                root.put("response", child)
            }
            else -> root.put("response", JSONObject.wrap(data.response))
        }

        return root.toString()
    }

    private fun errorWrapperToJson(data: ErrorWrapper): String {
        return JSONObject().apply {
            put("error", data.error)
        }.toString()
    }
}