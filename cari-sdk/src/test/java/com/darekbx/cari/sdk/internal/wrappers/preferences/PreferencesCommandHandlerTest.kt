package com.darekbx.cari.sdk.internal.wrappers.preferences

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.darekbx.cari.sdk.internal.model.CommandWrapper
import com.darekbx.cari.sdk.internal.model.ResponseWrapper
import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class PreferencesCommandHandlerTest {

    val context = ApplicationProvider.getApplicationContext<Context>()
    val scope = "app_preferences"

    @Before
    fun initialize() {
        with(context) {
            getSharedPreferences(scope, Context.MODE_PRIVATE)
                .edit()
                .putString("test_key_1_1", "test_value_1_1")
                .commit()
        }
    }

    @Test
    fun handleCommand_dump() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper("prefs", "dump", mutableListOf())
        val result = processCommand(handler, command)

        assertEquals("{app_preferences={test_key_1_1=test_value_1_1}}", result.response.toString())
    }

    @Test
    fun handleCommand_dump_scope() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "dump", mutableListOf(scope)
        )
        val result = processCommand(handler, command)

        assertEquals("{test_key_1_1=test_value_1_1}", result.response.toString())
    }

    @Test
    fun handleCommand_scopes() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper("prefs", "scopes", mutableListOf())
        val result = processCommand(handler, command)

        assertEquals("[app_preferences]", result.response.toString())
    }

    @Test
    fun handleCommand_list() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "list", mutableListOf(scope)
        )
        val result = processCommand(handler, command)

        assertEquals("[test_key_1_1]", result.response.toString())
        assertError(handler, command)
    }

    @Test
    fun handleCommand_get() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "get", mutableListOf(scope, "test_key_1_1")
        )
        val result = processCommand(handler, command)

        assertEquals("test_value_1_1", result.response.toString())
        assertError(handler, command)
    }

    @Test
    fun handleCommand_get_null() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "get", mutableListOf(scope, "unknown")
        )
        val result = processCommand(handler, command)

        assertEquals("null", result.response)
    }

    @Test
    fun handleCommand_remove() {
        val handler = PreferencesCommandHandler(context)
        val commandRemove = CommandWrapper(
            "prefs", "remove", mutableListOf(scope, "test_key_1_1")
        )
        val result = processCommand(handler, commandRemove)
        assertEquals("Removed \"test_key_1_1\"", result.response.toString())
        assertError(handler, commandRemove)

        val commandResult = CommandWrapper(
            "prefs", "list", mutableListOf(scope)
        )
        val resultResult = processCommand(handler, commandResult)

        assertEquals("[]", resultResult.response.toString())
    }

    @Test
    fun handleCommand_set() {
        val handler = PreferencesCommandHandler(context)
        val commandSet = CommandWrapper(
            "prefs", "set", mutableListOf(scope, "test_key", "test_value")
        )
        val result = processCommand(handler, commandSet)
        assertEquals("Added \"test_key\"", result.response.toString())
        assertError(handler, commandSet)

        val commandResult = CommandWrapper(
            "prefs", "list", mutableListOf(scope)
        )
        val resultResult = processCommand(handler, commandResult)

        assertEquals("[test_key_1_1, test_key]", resultResult.response.toString())
    }

    private fun processCommand(handler: PreferencesCommandHandler, command: CommandWrapper ): ResponseWrapper {
        val resultJson = handler.handleCommand(gson.toJson(command)) as String
        return gson.fromJson<ResponseWrapper>(resultJson, ResponseWrapper::class.java)
    }

    private fun assertError(handler: PreferencesCommandHandler, command: CommandWrapper) {
        val resultError = handler.handleCommand(
            gson.toJson(command.also { it.arguments.clear() })
        ) as String
        assertEquals("{\"error\":\"Invalid parameters count\"}", resultError)
    }

    val gson by lazy { Gson() }
}