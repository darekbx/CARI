package com.darekbx.cari.sdk.internal.json

import android.os.Build
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class JsonParserTest {

    @Test
    fun parseStringCommand() {
        val jsonParser = JsonParser()
        val output = jsonParser.parse("\"version\"")
        assertEquals("version", output)
    }

    @Test
    fun parseJsonCommand() {
    }
}