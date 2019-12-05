package com.darekbx.carisdk.internal.wrappers.preferences

import android.content.Context
import android.content.SharedPreferences
import java.io.File

internal class PreferencesWrapper(val context: Context) {

    companion object {
        private val PREFERENCES_DIR = "/shared_prefs"
        private val RESOURCE_SUFIX = ".xml"
    }

    fun listScopes(): List<String> {
        val rootPath = context.getApplicationInfo().dataDir + PREFERENCES_DIR
        return File(rootPath)
            .takeIf { it.exists() }
            ?.list()
            ?.let { filesList ->
                return filesList
                    .filter { it.endsWith(RESOURCE_SUFIX, true) }
                    .map { it.removeSuffix(RESOURCE_SUFIX) }
                    .toList()
            } ?: emptyList()
    }

    fun listKeys(scope: String): List<String> {
        val preferences = providePreferences(scope)
        return preferences.all.keys.toList()
    }

    fun getValue(scope: String, key: String): String? {
        val preferences = providePreferences(scope)
        val entry = preferences
            .all
            .filter { it.key == key }
        return when (entry.isNullOrEmpty()) {
            true -> null
            else -> entry.get(key)?.toString()
        }
    }

    fun remove(scope: String, key: String) {
        val preferences = providePreferences(scope)
        preferences.edit().remove(key).commit()
    }

    fun save(scope: String, key: String, value: String) {
        val preferences = providePreferences(scope)
        preferences.edit().putString(key, value).commit()
    }

    private fun providePreferences(scope: String): SharedPreferences {
        return context.getSharedPreferences(scope, Context.MODE_PRIVATE)
    }
}