package com.darekbx.cari.sdk.internal.bubble.preferences

import android.content.Context
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceItem
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceScope
import com.darekbx.cari.sdk.internal.wrappers.preferences.PreferencesWrapper

class PreferencesParser(val context: Context) {

    fun parse(): List<PreferenceScope> {
        return preferencesWrapper
            .listScopes()
            .map { scope ->
                val items = preferencesWrapper
                    .dumpScope(scope)
                    .map { mapEntry ->
                        PreferenceItem(
                            mapEntry.key,
                            mapEntry.value ?: "NULL",
                            (mapEntry.value ?: 0)::class.java.simpleName
                        )
                    }
                PreferenceScope(scope, items)
            }
    }

    private val preferencesWrapper by lazy { PreferencesWrapper(context) }
}