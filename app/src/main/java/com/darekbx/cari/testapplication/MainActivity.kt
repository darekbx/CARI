package com.darekbx.cari.testapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTestPreferences()
    }

    private fun initTestPreferences() {
        getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .edit()
            .putString("test_key_1_1", "test_value_1_1")
            .putString("test_key_1_2", "test_value_1_2")
            .commit()
        getPreferences(Context.MODE_PRIVATE)
            .edit()
            .putString("test_key_2_1", "test_value_2_1")
            .putString("test_key_2_2", "test_value_2_2")
            .commit()
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("test_key_3_1", "test_value_3_1")
            .putString("test_key_3_2", "test_value_3_2")
            .commit()
    }
}
