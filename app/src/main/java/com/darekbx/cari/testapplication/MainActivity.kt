package com.darekbx.cari.testapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.darekbx.cari.CARI
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTestPreferences()

        val rootPath = applicationContext.getApplicationInfo().dataDir + "/shared_prefs"

        val list = File(rootPath).list()

        // PreferenceManager.getDefaultSharedPreferencesName(applicationContext)

        CARI.initialize(applicationContext)
    }

    private fun initTestPreferences() {
        getSharedPreferences("app_preferences", Context.MODE_PRIVATE).edit().putString("test_key_1", "test_value_1").commit()
        getPreferences(Context.MODE_PRIVATE).edit().putString("test_key_2", "test_value_2").commit()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("test_key_3", "test_value_3").commit()
    }
}
