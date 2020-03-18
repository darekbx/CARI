package com.darekbx.cari.sdk.internal.communication

import android.os.AsyncTask
import android.util.Log
import com.darekbx.cari.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.BindException
import java.net.ServerSocket

internal class SocketCommunication(val port: Int) {

    private val CHARSET = Charsets.UTF_8

    var callback: ((receivedData: String) -> String)? = null

    fun start() {
        AsyncTask.execute {
            try {
                val serverSocket = ServerSocket(port)
                while (true) {

                    val socketRead = serverSocket.accept()

                    val input = socketRead.getInputStream()
                    val outputWriter = PrintWriter(socketRead.getOutputStream(), true)
                    val inputReader = InputStreamReader(input, CHARSET)
                    val bufferedReader = BufferedReader(inputReader)

                    val receivedData = bufferedReader.readLine()
                    val dataToWrite = callback?.invoke(receivedData)
                    outputWriter.println(dataToWrite)

                    inputReader.close()
                    bufferedReader.close()
                    outputWriter.close()

                    socketRead.close()
                }
            } catch (e: Exception) {
                if (e is BindException) {
                    Log.e("CARI", "There is a problem with communication: ${e.message}")
                }
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }
}