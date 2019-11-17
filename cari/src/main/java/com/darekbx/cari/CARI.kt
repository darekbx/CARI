package com.darekbx.cari

import android.content.Context
import com.darekbx.cari.internal.CompressionUtil
import kotlinx.coroutines.Job

import com.darekbx.cari.internal.SocketCommunication

object CARI {

    /**
     * Initializes CARI server for the application.
     *
     * @param context Use application context, cannot be null.
     * @param options Optional arguments, you can set for eg. different port.
     *
     * @return Job responsible for module communication.
     *         You can use this job to stop processing, when app is being destroyed
     */
    fun initialize(context: Context, options: Options = Options()): Job {
        val port = options.port
        val socketCommunication = SocketCommunication(port).apply {
            callback = { encodedCommand ->
                val command = CompressionUtil.decodeData(encodedCommand)


                // computation
                val result = "Computation result"


                val encodedResult = CompressionUtil.encodeData(result)
                encodedResult
            }
            start()
        }

        return socketCommunication.supervisiorJob
    }
}