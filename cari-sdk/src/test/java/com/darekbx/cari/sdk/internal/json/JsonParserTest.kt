package com.darekbx.cari.sdk.internal.json

import android.os.Build
import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.model.ErrorWrapper
import com.darekbx.cari.sdk.internal.model.ResponseWrapper
import com.darekbx.cari.sdk.internal.model.SqliteResultWrapper
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class JsonParserTest {

    @Test
    fun parseStringCommand() {
        val output = JsonParser.parse("\"version\"")
        assertEquals("version", output)
    }

    @Test
    fun parseJsonCommand() {
        val output = JsonParser.parse("{\"resource\": \"prefs\", \"command\": \"dump\", \"arguments\": [\"app_preferences\"]}")
        val command = output as CommandWrapper
        assertEquals("prefs", command.resource)
        assertEquals("dump", command.command)
        assertEquals("app_preferences", command.arguments[0])
    }

    @Test
    fun toJson_error() {
        val wrapper = ErrorWrapper("Error message")
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"error\":\"Error message\"}", json)
    }

    @Test
    fun toJson_SqliteResultWrapper() {
        val sqliteResponse = SqliteResultWrapper(listOf(listOf("A", "B"), listOf("1", "2")), "Sumary", 10)
        val wrapper = ResponseWrapper("prefs", sqliteResponse)
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":{\"result\":[[\"A\",\"B\"],[\"1\",\"2\"]],\"summary\":\"Sumary\",\"limitedRows\":10}}", json)
    }

    @Test
    fun toJson_string() {
        val wrapper = ResponseWrapper("prefs", "NULL")
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":\"NULL\"}", json)
    }

    @Test
    fun toJson_list() {
        val wrapper = ResponseWrapper("prefs", listOf("scope_1", "scope_2"))
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":[\"scope_1\",\"scope_2\"]}", json)
    }

    @Test
    fun toJson_array() {
        val wrapper = ResponseWrapper("prefs", arrayOf("version", "1.0"))
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":[\"version\",\"1.0\"]}", json)
    }

    @Test
    fun toJson_map() {
        val wrapper = ResponseWrapper("prefs", mapOf("a" to "scope_1", "b" to "scope_2"))
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":{\"a\":\"scope_1\",\"b\":\"scope_2\"}}", json)
    }

    @Test
    fun toJson_map_map() {
        val wrapper = ResponseWrapper("prefs", mapOf("key_1" to mapOf("a" to "scope_1", "b" to "scope_2"), "key_2" to mapOf("c" to "scope_a")))
        val json = JsonParser.toJson(wrapper)

        assertEquals("{\"type\":\"prefs\",\"response\":{\"key_1\":{\"a\":\"scope_1\",\"b\":\"scope_2\"},\"key_2\":{\"c\":\"scope_a\"}}}", json)
    }
}