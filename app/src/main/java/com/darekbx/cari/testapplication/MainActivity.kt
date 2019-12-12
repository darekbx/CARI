package com.darekbx.cari.testapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darekbx.cari.testapplication.testdata.defaultdatabase.TestDefaultDatabase
import com.darekbx.cari.testapplication.testdata.roomdatabase.TestRoomDatabase
import com.darekbx.cari.testapplication.testdata.sharedpreferences.TestSharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TestSharedPreferences.createTestPreferences(this)
        TestRoomDatabase().createTestDatabase(this)
        TestDefaultDatabase().createTestDatabase(this)
    }
}
