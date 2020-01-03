package com.darekbx.cari.sdk.internal.json

import com.darekbx.cari.sdk.internal.model.CommandWrapper
import org.json.JSONObject

internal class JsonParser {

    fun parse(value: String): String {
        return value.removeSurrounding("\"")
    }

    fun parse(value: CommandWrapper) {

    }
}