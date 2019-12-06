package com.darekbx.cari.sdk.internal.communication

import android.os.Build
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class CompressionUtilTest {

    @Test
    fun compressionTest() {
        val testData = "Command to execute"
        val compressed = CompressionUtil.encodeData(testData)
        val decompressed = CompressionUtil.decodeData(compressed)

        assertEquals(testData, decompressed)
    }
}