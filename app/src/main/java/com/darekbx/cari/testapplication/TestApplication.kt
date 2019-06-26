package com.darekbx.cari.testapplication

import android.app.Application
import com.darekbx.cari.sdk.CARI
import com.darekbx.cari.sdk.Options

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CARI.initialize(applicationContext, Options(isBubbleEnabled = true))
    }
}