package com.socketserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.socket.emitter.Emitter
import io.socket.client.Socket.EVENT_DISCONNECT
import io.socket.client.Socket.EVENT_CONNECT
import android.R.string.no
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {

            Log.v("-----", "Start")

            val serverSocket = ServerSocket(38300)
            var count = 0
            while (!isDestroyed) {

                val socket = serverSocket.accept()

                Log.v("-----------", "Attempt $count, ${socket.inetAddress}:${socket.port}")
                count++

                socket.getOutputStream()?.use {
                    OutputStreamWriter(it)?.use {
                        it.write("android_data")
                    }
                }

                /*
                Open seperate socket for read data?
                socket.getInputStream()?.use {
                    InputStreamReader(it)?.use {
                        BufferedReader(it)?.use {
                            Log.v("-----------", "Response: ${it.readLine()}")
                        }
                    }
                }*/


                socket.close()
            }
        }


    }
}
