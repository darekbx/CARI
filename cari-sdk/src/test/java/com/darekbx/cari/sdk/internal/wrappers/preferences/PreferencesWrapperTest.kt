package com.darekbx.cari.sdk.internal.wrappers.preferences

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class PreferencesWrapperTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun initialize() {
        with(context) {
            getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                .edit()
                .putString("test_key_1_1", "test_value_1_1")
                .putString("test_key_1_2", "test_value_1_2")
                .commit()
            getSharedPreferences("activity_preferences", Context.MODE_PRIVATE)
                .edit()
                .putString("test_key_2_1", "test_value_2_1")
                .putString("test_key_2_2", "test_value_2_2")
                .commit()
        }
    }

    @Test
    fun listScopes() {
        val preferences = PreferencesWrapper(context)
        val scopes = preferences.listScopes()
        assertEquals(2, scopes.size)
    }

    @Test
    fun listKeys() {
        val preferences = PreferencesWrapper(context)
        val keys = preferences.listKeys("app_preferences")

        assertEquals(2, keys.size)
        assertEquals("test_key_1_2", keys[1])
    }

    @Test
    fun getValue() {
        val preferences = PreferencesWrapper(context)
        val value = preferences.getValue("activity_preferences", "test_key_2_1")

        assertEquals("test_value_2_1", value)
    }

    @Test
    fun remove() {
        val preferences = PreferencesWrapper(context)
        val scope = "activity_preferences"
        val keys = preferences.listKeys(scope)
        assertEquals(2, keys.size)

        preferences.remove(scope, "test_key_2_1")

        val keysAfterRemove = preferences.listKeys(scope)
        assertEquals(1, keysAfterRemove.size)
    }

    @Test
    fun save() {
        val preferences = PreferencesWrapper(context)
        val scope = "activity_preferences"
        val keys = preferences.listKeys(scope)
        assertEquals(2, keys.size)

        preferences.save(scope, "test_key", "test_value")

        val keysAfterSave = preferences.listKeys(scope)
        assertEquals(3, keysAfterSave.size)

        val value = preferences.getValue(scope, "test_key")
        assertEquals("test_value", value)
    }
}