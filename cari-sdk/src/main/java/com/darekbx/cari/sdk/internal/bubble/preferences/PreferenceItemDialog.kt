package com.darekbx.cari.sdk.internal.bubble.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceItem
import com.darekbx.cari.sdk.internal.wrappers.preferences.PreferencesWrapper
import kotlinx.android.synthetic.main.dialog_preference_item.*

class PreferenceItemDialog(
    context: Context,
    val scope: String,
    val preferenceItem: PreferenceItem
) : Dialog(context) {

    var valueChanged: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_preference_item)

        preferences_item_key.setText(preferenceItem.key)
        preferences_item_value.setText("${preferenceItem.value}")

        button_save.setOnClickListener { save() }
        button_cancel.setOnClickListener { dismiss() }

        displayTypeNotification()
    }

    private fun save() {
        val editor = PreferencesWrapper(context)
            .providePreferences(scope)
            .edit()
        val newValue = preferences_item_value.text.toString()

        val result = when {
            preferenceItem.value is Int -> {
                newValue.toIntOrNull()?.let {
                    editor.putInt(preferenceItem.key, it)
                    preferenceItem.value = it
                    true
                } ?: false
            }
            preferenceItem.value is Boolean -> {
                editor.putBoolean(preferenceItem.key, newValue.toBoolean())
                preferenceItem.value = newValue.toBoolean()
                true
            }
            preferenceItem.value is Float -> {
                newValue.toFloatOrNull()?.let {
                    editor.putFloat(preferenceItem.key, it)
                    preferenceItem.value = it
                    true
                } ?: false
            }
            preferenceItem.value is Long -> {
                newValue.toLongOrNull()?.let {
                    editor.putLong(preferenceItem.key, it)
                    preferenceItem.value = it
                    true
                } ?: false
            }
            preferenceItem.value is String -> {
                editor.putString(preferenceItem.key, newValue)
                preferenceItem.value = newValue
                true
            }
            preferenceItem.value is Set<*> -> {
                val chunks = newValue.removePrefix("[").removeSuffix("]").split(",").toSet()
                editor.putStringSet(preferenceItem.key, chunks)
                preferenceItem.value = chunks
                true
            }
            else -> false
        }

        when {
            result == true -> {
                editor.apply()
                valueChanged?.invoke()
                dismiss()
            }
            else -> {
                preferences_item_value.setError(context.getString(R.string.nofitication_error))
            }
        }
    }

    private fun displayTypeNotification() {
        val notificationResId = when {
            preferenceItem.value is Int -> R.string.nofitication_int
            preferenceItem.value is Boolean -> R.string.nofitication_boolean
            preferenceItem.value is Float -> R.string.nofitication_float
            preferenceItem.value is Long -> R.string.nofitication_long
            preferenceItem.value is String -> R.string.nofitication_string
            preferenceItem.value is Set<*> -> R.string.nofitication_string_set
            else -> R.string.nofitication_empty
        }
        preferences_type_notification.setText(notificationResId)
    }
}