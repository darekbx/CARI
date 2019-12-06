package com.darekbx.cari.sdk.internal.wrappers.preferences

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.darekbx.cari.sdk.internal.model.Argument
import com.darekbx.cari.sdk.internal.model.CommandWrapper
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
        val result = handler.handleCommand(gson.toJson(command)) as String

        assertEquals("{\"app_preferences\":{\"test_key_1_1\":\"test_value_1_1\"}}", result)
    }

    @Test
    fun handleCommand_dump_scope() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "dump", mutableListOf(
                Argument(scope, "")
            )
        )
        val result = handler.handleCommand(gson.toJson(command)) as String

        assertEquals("{\"test_key_1_1\":\"test_value_1_1\"}", result)
    }

    @Test
    fun handleCommand_scopes() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper("prefs", "scopes", mutableListOf())
        val result = handler.handleCommand(gson.toJson(command)) as String

        assertEquals("[\"app_preferences\"]", result)
    }

    @Test
    fun handleCommand_list() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "list", mutableListOf(
                Argument(scope, "")
            )
        )
        val result = handler.handleCommand(gson.toJson(command)) as String

        assertEquals("[\"test_key_1_1\"]", result)
        assertError(handler, command)
    }

    @Test
    fun handleCommand_get() {
        val handler = PreferencesCommandHandler(context)
        val command = CommandWrapper(
            "prefs", "get", mutableListOf(
                Argument(scope, ""),
                Argument("test_key_1_1", "")
            )
        )
        val result = handler.handleCommand(gson.toJson(command)) as String

        assertEquals("\"test_value_1_1\"", result)
        assertError(handler, command)
    }

    @Test
    fun handleCommand_remove() {
        val handler = PreferencesCommandHandler(context)
        val commandRemove = CommandWrapper(
            "prefs", "remove", mutableListOf(
                Argument(scope, ""),
                Argument("test_key_1_1", "")
            )
        )
        handler.handleCommand(gson.toJson(commandRemove)) as String
        assertError(handler, commandRemove)

        val commandResult = CommandWrapper(
            "prefs", "list", mutableListOf(
                Argument(scope, "")
            )
        )
        val resultResult = handler.handleCommand(gson.toJson(commandResult)) as String

        assertEquals("[]", resultResult)
    }

    @Test
    fun handleCommand_set() {
        val handler = PreferencesCommandHandler(context)
        val commandSet = CommandWrapper(
            "prefs", "set", mutableListOf(
                Argument(scope, ""),
                Argument("test_key", ""),
                Argument("test_value", "")
            )
        )
        handler.handleCommand(gson.toJson(commandSet)) as String
        assertError(handler, commandSet)

        val commandResult = CommandWrapper(
            "prefs", "list", mutableListOf(
                Argument(scope, "")
            )
        )
        val resultResult = handler.handleCommand(gson.toJson(commandResult)) as String

        assertEquals("[\"test_key_1_1\",\"test_key\"]", resultResult)
    }

    private fun assertError(handler: PreferencesCommandHandler, command: CommandWrapper) {
        val resultError = handler.handleCommand(
            gson.toJson(command.also { it.arguments.clear() })
        ) as String
        assertEquals("{\"error\":\"Invalid parameters count\"}", resultError)
    }

    val gson by lazy { Gson() }
}