package com.darekbx.cari.sdk.internal.communication

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object CompressionUtil {

    fun decodeData(data64: String): String {
        val data = Base64.decode(data64, Base64.DEFAULT)
        ByteArrayInputStream(data).use { bais ->
            GZIPInputStream(bais).use { gzip ->
                val bytes = gzip.readBytes()
                return String(bytes)
            }
        }
    }

    fun encodeData(data: String): String {
        ByteArrayOutputStream().use { baos ->
            GZIPOutputStream(baos).use { gzip ->
                gzip.write(data.toByteArray(Charsets.UTF_8))
            }
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }
    }
}