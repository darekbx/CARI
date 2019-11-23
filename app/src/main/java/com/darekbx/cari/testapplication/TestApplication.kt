package com.darekbx.cari.testapplication

import android.app.Application
import com.darekbx.cari.CARI

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CARI.initialize(applicationContext)
    }
}