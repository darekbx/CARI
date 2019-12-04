package com.darekbx.carisdk.testapplication

import android.app.Application
import com.darekbx.carisdk.CARI

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CARI.initialize(applicationContext)
    }
}