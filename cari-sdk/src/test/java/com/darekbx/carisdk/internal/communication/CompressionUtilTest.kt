package com.darekbx.carisdk.internal.communication

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [21])
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