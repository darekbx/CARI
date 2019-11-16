package com.socketserver

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

class SocketCommunicationViewModel: ViewModel() {

    private val CHARSET = Charsets.UTF_8

    private val viewModelJob = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var callback: ((receivedData: String) -> String)? = null

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun start(){
        ioScope.launch {
            val serverSocket = ServerSocket(38300 /* TODO remove from here */)
            while (viewModelJob.isActive) {

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