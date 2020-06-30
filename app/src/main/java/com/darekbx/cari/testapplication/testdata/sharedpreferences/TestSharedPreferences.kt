package com.darekbx.cari.testapplication.testdata.sharedpreferences

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager

object TestSharedPreferences {

    fun createTestPreferences(activity: Activity) {
        activity.getSharedPreferences("shared", Context.MODE_PRIVATE)
            .edit()
            .putString("test_key_String", "test_value_1_1")
            .putLong("test_key_Long", 10000L)
            .putBoolean("test_key_Boolean", true)
            .putFloat("test_key_Float", 56.1234F)
            .putInt("test_key_Int", 15)
            .putStringSet("test_key_StringSet", setOf("A", "B", "C"))
            .commit()
        activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .putString("test_key_2_1", "test_value_2_1")
            .putString("test_key_2_2", "test_value_2_2")
            .commit()
        PreferenceManager.getDefaultSharedPreferences(activity)
            .edit()
            .putString("test_key_3_1", "test_value_3_1")
            .putString("test_key_3_2", "test_value_3_2")
            .commit()
    }
}