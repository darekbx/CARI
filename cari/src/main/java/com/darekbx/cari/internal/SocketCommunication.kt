package com.darekbx.cari.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

internal class SocketCommunication(val port: Int) {

    private val CHARSET = Charsets.UTF_8

    val supervisiorJob = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + supervisiorJob)

    var callback: ((receivedData: String) -> String)? = null

    fun start() {
        ioScope.launch {
            val serverSocket = ServerSocket(port)
            while (supervisiorJob.isActive) {

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
        }
    }
}