package com.darekbx.cari.sdk.internal.wrappers.common

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class CommonCommandHandlerTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun handleCommand_version() {
        val handler = CommonCommandHandler(context)
        val resultJson = handler.handleCommand("\"version\"") as String

        assertEquals("{\"type\":\"common\",\"response\":[\"SDK-Version\",\"1.0.1\"]}", resultJson)
    }

    @Test
    fun handleCommand_unknown() {
        val handler = CommonCommandHandler(context)
        val resultJson = handler.handleCommand("\"unknown\"")

        assertFalse(resultJson as Boolean)
    }
}