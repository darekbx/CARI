package com.darekbx.cari.testapplication.testdata.sharedpreferences

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager

object TestSharedPreferences {

    fun createTestPreferences(activity: Activity) {
        activity.getSharedPreferences("shared", Context.MODE_PRIVATE)
            .edit()
            .putString("test_key_1_1", "test_value_1_1")
            .putString("test_key_1_2", "test_value_1_2")
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