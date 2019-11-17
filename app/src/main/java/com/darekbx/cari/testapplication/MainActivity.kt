package com.darekbx.cari.testapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this)[SocketCommunicationViewModel::class.java]


        // add port definition to SocketCommunicationViewModel
        // port should be also defined in main init component statement (as an extra setting)

        model.start()
        model.callback = { command ->

            Log.v("-----------", "Command: $command")

            // compute
            Thread.sleep(2000)

            "Command Result"
        }
    }
}
